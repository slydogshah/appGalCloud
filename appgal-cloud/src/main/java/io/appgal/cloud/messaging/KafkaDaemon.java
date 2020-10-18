package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.appgal.cloud.model.ActiveFoodRunnerData;
import io.appgal.cloud.model.DataSetFromBegginningOffset;
import io.appgal.cloud.model.DestinationNotification;
import io.appgal.cloud.model.SourceNotification;
import io.appgal.cloud.persistence.MongoDBJsonStore;
import io.appgal.cloud.util.MapUtils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.net.InetAddress;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;

@ApplicationScoped
public class KafkaDaemon {
    private static Logger logger = LoggerFactory.getLogger(KafkaDaemon.class);

    private KafkaProducer<String,String> kafkaProducer;
    private KafkaConsumer<String,String> kafkaConsumer;
    private CountDownLatch shutdownLatch;
    private Map<String,List<TopicPartition>> topicPartitions;

    private Queue<NotificationContext> readNotificationsQueue;
    private Map<String,Map<String, JsonArray>> lookupTable;
    private List<String> topics = new ArrayList<>();

    private Queue<DataSetFromBegginningOffset> dataSetFromQueue;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    private ForkJoinPool commonPool;

    private KafkaDaemonListener daemonListener;

    public KafkaDaemon()
    {
        this.topicPartitions = new HashMap<>();
        this.readNotificationsQueue = new LinkedTransferQueue<>();
        this.shutdownLatch = new CountDownLatch(1);
        this.commonPool = ForkJoinPool.commonPool();
        this.lookupTable = new HashMap<>();
        this.dataSetFromQueue = new LinkedTransferQueue<>();
    }

    public Map<String, Map<String, JsonArray>> getLookupTable() {
        return lookupTable;
    }

    public void registerDaemonListener(KafkaDaemonListener daemonListener)
    {
        this.daemonListener = daemonListener;
    }

    @PostConstruct
    public void start()
    {
        this.topics = this.mongoDBJsonStore.findKafakaDaemonBootstrapData();
        if(this.kafkaConsumer == null) {
            try {
                Properties config = new Properties();
                config.put("client.id", InetAddress.getLocalHost().getHostName());
                config.put("group.id", "foodRunnerSyncProtocol_notifications");
                config.put("bootstrap.servers", "localhost:9092");
                config.put("key.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class);
                config.put("value.deserializer", org.springframework.kafka.support.serializer.JsonDeserializer.class);
                config.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
                config.put("value.serializer", org.springframework.kafka.support.serializer.JsonSerializer.class);
                config.put("auto.commit.interval.ms", 1000);
                config.put("enable.auto.commit", true);
                config.put("max.poll.records", 100);

                this.kafkaConsumer = new KafkaConsumer<String, String>(config);
            }
            catch(Exception e)
            {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }

            KafkaRebalanceListener rebalanceListener = new KafkaRebalanceListener(this.kafkaConsumer, this.readNotificationsQueue, this.topics,
                    this.topicPartitions);
            this.kafkaConsumer.subscribe(this.topics, rebalanceListener);

            kafkaConsumer.poll(Duration.of(20, ChronoUnit.SECONDS));
        }

        if(this.kafkaProducer == null) {
            try {
                Properties config = new Properties();
                config.put("client.id", InetAddress.getLocalHost().getHostName());
                config.put("group.id", "foodRunnerSyncProtocol_notifications");
                config.put("bootstrap.servers", "localhost:9092");
                config.put("key.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class);
                config.put("value.deserializer", org.springframework.kafka.support.serializer.JsonDeserializer.class);
                config.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
                config.put("value.serializer", org.springframework.kafka.support.serializer.JsonSerializer.class);
                this.kafkaProducer = new KafkaProducer<>(config);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }

    @PreDestroy
    public void stop()
    {
        this.readNotificationsQueue = null;
        this.dataSetFromQueue = null;
        this.kafkaProducer.close();
        this.kafkaConsumer.close();
        this.shutdownLatch.countDown();
    }

    public void addTopic(String topic)
    {
        this.topics.add(topic);
    }

    public Boolean getActive() {
        return (this.kafkaProducer != null && this.kafkaConsumer != null);
    }

    public Queue<DataSetFromBegginningOffset> getDataSetFromQueue() {
        return dataSetFromQueue;
    }

    public void logStartUp()
    {
        logger.info("**********");
        logger.info("STARTING_KAFKA_DAEMON");
        logger.info("**********");
    }

    public void produceData(String topic, JsonObject jsonObject)
    {
        //ProducerTask producerTask = new ProducerTask(this.kafkaProducer,topic, jsonObject);
        //this.commonPool.execute(producerTask);
        final ProducerRecord<String, String> record = new ProducerRecord<>(topic,
                topic, jsonObject.toString());

        this.kafkaProducer.send(record, new Callback() {
            public void onCompletion(RecordMetadata metadata, Exception e) {
                if (e != null) {
                    logger.error("Send failed for record {}", record, e);
                }
                else
                {
                    logger.info("******************************************");
                    //logger.info("PRODUCE_DATA");
                    logger.info("RECORD_META_DATA: "+metadata.timestamp());
                    logger.info("******************************************");
                }
            }
        });
    }

    public JsonArray readNotifications(String topic, MessageWindow messageWindow)
    {
        messageWindow.setTopic(topic);
        return this.readLogByWindow(messageWindow);
    }

    public void produceActiveFoodRunnerData(String topic, List<ActiveFoodRunnerData> activeFoodRunnerData)
    {
        for(ActiveFoodRunnerData local:activeFoodRunnerData) {
            OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
            OffsetDateTime end = OffsetDateTime.now(ZoneOffset.UTC);
            MessageWindow messageWindow = new MessageWindow();
            messageWindow.setStart(start);
            messageWindow.setEnd(end);
            NotificationContext notificationContext = new NotificationContext(topic,messageWindow);
            readNotificationsQueue.add(notificationContext);
            ProducerTask producerTask = new ProducerTask(this.kafkaProducer, topic, local.toJson());
            this.commonPool.execute(producerTask);
        }
    }

    public JsonArray findTheClosestFoodRunner(SourceNotification sourceNotification)
    {
        JsonArray jsonArray = new JsonArray();

        double sourceLatitude = Double.parseDouble(sourceNotification.getLatitude());
        double sourceLongitude = Double.parseDouble(sourceNotification.getLongitude());
        Iterator<DataSetFromBegginningOffset> iterator = this.dataSetFromQueue.iterator();
        while(iterator.hasNext())
        {
            DataSetFromBegginningOffset local = iterator.next();
            JsonArray activeFoodRunnerData = local.getJsonArray();
            String jsonString = activeFoodRunnerData.iterator().next().getAsString();
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            try {
                if (!jsonObject.has("latitude") || !jsonObject.has("longitude")) {
                    logger.info("IGNORING_INVALID_DATA: " + jsonString);
                    continue;
                }

                String latitude = jsonObject.get("latitude").getAsString();
                String longitude = jsonObject.get("longitude").getAsString();
                double foodRunnerLatitude = 0.0d;
                double foodRunnerLongitude = 0.0d;
                try {
                    foodRunnerLatitude = Double.parseDouble(latitude);
                    foodRunnerLongitude = Double.parseDouble(longitude);
                } catch (Exception e) {
                    logger.info("IGNORING_INVALID_DATA: " + jsonString);
                    continue;
                }

                //Match the coordinates with the FoodRunner
                double distance = MapUtils.calculateDistance(sourceLatitude, sourceLongitude, foodRunnerLatitude, foodRunnerLongitude);
                //if(distance < 5d)
                //{
                //    jsonArray.add(jsonObject);
                //}
                //logger.info("....");
                //logger.info("Distance: "+distance);
                //logger.info("....");
                jsonArray.add(jsonObject);
            }
            catch(Exception e)
            {
                logger.info("IGNORING_INVALID_DATA: " + jsonString);
                continue;
            }
        }
        return jsonArray;
    }


    //TODO: Investigate if this method should be synchronized because of poll invocation
    private JsonArray readLogByWindow(MessageWindow messageWindow)
    {
        OffsetDateTime start = messageWindow.getStart();

        //Construct the parameters to read the Kafka Log
        Map<TopicPartition, Long> partitionParameter = new HashMap<>();
        long time = start.toInstant().toEpochMilli();
        List<TopicPartition> currentTopicPartitions = topicPartitions.get(messageWindow.getTopic());
        for (TopicPartition topicPartition : currentTopicPartitions) {
            partitionParameter.put(topicPartition, time);
        }

        //
        Map<TopicPartition, OffsetAndTimestamp> topicPartitionOffsetAndTimestampMap = kafkaConsumer.offsetsForTimes(partitionParameter);
        Set<Map.Entry<TopicPartition, OffsetAndTimestamp>> entrySet = topicPartitionOffsetAndTimestampMap.entrySet();
        OffsetAndTimestamp offsetAndTimestamp=null;
        for(Map.Entry<TopicPartition, OffsetAndTimestamp> entry:entrySet) {
            offsetAndTimestamp = entry.getValue();
            logger.info("******************");
            logger.info("Criteria: "+time);
            logger.info("Offset: "+offsetAndTimestamp);
            logger.info("******************");
        }


        kafkaConsumer.seek(currentTopicPartitions.get(0), offsetAndTimestamp.offset());
        ConsumerRecords<String,String> records = kafkaConsumer.poll(Duration.of(20, ChronoUnit.SECONDS));
        JsonArray jsonArray = new JsonArray();
        for(ConsumerRecord<String, String> record:records)
        {
            logger.info(record.value());
            jsonArray.add(JsonParser.parseString(record.value()).getAsJsonObject());
            DataSetFromBegginningOffset dataSetFromBegginningOffset = new DataSetFromBegginningOffset(jsonArray);
            this.dataSetFromQueue.add(dataSetFromBegginningOffset);
        }
        return jsonArray;
    }
}
