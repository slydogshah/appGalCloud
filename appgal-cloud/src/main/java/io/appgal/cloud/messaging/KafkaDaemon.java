package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.google.gson.JsonParser;
import io.appgal.cloud.persistence.MongoDBJsonStore;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.net.InetAddress;
import java.time.OffsetDateTime;
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

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    private ForkJoinPool commonPool;

    public KafkaDaemon()
    {
        this.topicPartitions = new HashMap<>();
        this.readNotificationsQueue = new LinkedTransferQueue<>();
        this.shutdownLatch = new CountDownLatch(1);
        this.commonPool = ForkJoinPool.commonPool();
        this.lookupTable = new HashMap<>();
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

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("Start the FindNotifications Thread");
                findNotifications();
            }
        });
        t.start();
    }

    @PreDestroy
    public void stop()
    {
        this.readNotificationsQueue = null;
        this.kafkaProducer.close();
        this.kafkaConsumer.close();
        this.shutdownLatch.countDown();
    }

    public Boolean getActive() {
        return (this.kafkaProducer != null && this.kafkaConsumer != null);
    }

    public void logStartUp()
    {
        logger.info("**********");
        logger.info("STARTING_KAFKA_DAEMON");
        logger.info("**********");
    }

    public void produceData(String topic, JsonObject jsonObject)
    {
        ProducerTask producerTask = new ProducerTask(this.kafkaProducer,topic, jsonObject);
        this.commonPool.execute(producerTask);
    }

    public JsonArray readNotifications(String topic, MessageWindow messageWindow)
    {
        NotificationContext notificationContext = new NotificationContext(topic, messageWindow);
        readNotificationsQueue.add(notificationContext);
        NotificationFinderTask notificationFinderTask = new NotificationFinderTask(notificationContext);
        this.commonPool.execute(notificationFinderTask);
        notificationFinderTask.join();


        Map<String, JsonArray> topicTable = this.lookupTable.get(topic);
        if(topicTable == null)
        {
            return new JsonArray();
        }
        return topicTable.get(messageWindow.getLookupTableIndex());
    }

    private void findNotifications()
    {
        try {
            do {
                logger.info("Start Long Poll");
                ConsumerRecords<String, String> records = kafkaConsumer.poll(20000);
                records.forEach(record -> process(record));

                //TODO: Read multiple NotificationContexts during this run
                printNotificationsQueue();
                if (readNotificationsQueue.isEmpty()) {
                    logger.info("NO_ACTIVE_READS_IN_PROGRESS");
                    continue;
                }
                this.processNotifications();
            } while (true);
        }
        catch(Exception ie)
        {
            logger.error(ie.getMessage(), ie);
            throw new RuntimeException(ie);
        }
    }

    private void processNotifications()
    {
        NotificationContext notificationContext = readNotificationsQueue.poll();
        if(notificationContext == null)
        {
            logger.info("*********KAFKA_DAEMON***********");
            logger.info("SKIP_READ_NOTIFICATIONS");
            logger.info("********************");
            return;
        }

        MessageWindow messageWindow = notificationContext.getMessageWindow();

        logger.info("*********KAFKA_DAEMON***********");
        logger.info("START_READ_NOTIFICATIONS ("+notificationContext.getMessageWindow().getStart()+")");
        logger.info("********************");

        String topic = notificationContext.getTopic();
        try {
            OffsetDateTime start = messageWindow.getStart();
            OffsetDateTime end = messageWindow.getEnd();

            //Construct the parameters to read the Kafka Log
            Map<TopicPartition, Long> partitionParameter = new HashMap<>();
            List<TopicPartition> currentTopicPartitions = topicPartitions.get(topic);
            for (TopicPartition topicPartition : currentTopicPartitions) {
                partitionParameter.put(topicPartition, start.toEpochSecond());
                partitionParameter.put(topicPartition, end.toEpochSecond());
            }

            //
            Map<TopicPartition, OffsetAndTimestamp> topicPartitionOffsetAndTimestampMap = kafkaConsumer.offsetsForTimes(partitionParameter);
            kafkaConsumer.poll(0);


            OffsetAndTimestamp offsetAndTimestamp = topicPartitionOffsetAndTimestampMap.values().iterator().next();
            kafkaConsumer.seek(currentTopicPartitions.get(0), offsetAndTimestamp.offset());
            JsonArray jsonArray = new JsonArray();
            for (int i = 0; i < 100; i++) {
                //logger.info("Start Short Poll: (" + i + ")");
                ConsumerRecords<String, String> notificationRecords =
                        kafkaConsumer.poll(100);
                if (notificationRecords == null || notificationRecords.isEmpty()) {
                    logger.info("****NOTIFICATION_RECORDS_NOT_READ_YET****");
                    continue;
                }
                for (ConsumerRecord<String, String> record : notificationRecords) {
                    logger.info("****CONSUME_DATA_TEST_RECORD****");
                    //logger.info("RECORD_OFFSET: "+record.offset());
                    //logger.info("RECORD_KEY: "+record.key());
                    logger.info("RECORD_VALUE: "+record.value());
                    //logger.info("....");

                    String jsonValue = record.value();

                    JsonObject jsonObject = JsonParser.parseString(jsonValue).getAsJsonObject();
                    messageWindow.addMessage(jsonObject);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        String lookupTableIndex = messageWindow.getLookupTableIndex();
        JsonArray copyOfMessages = messageWindow.getCopyOfMessages();
        Map<String, JsonArray> topicTable = lookupTable.get(topic);
        if(topicTable == null)
        {
            topicTable = new HashMap<>();
            lookupTable.put(topic, topicTable);
            topicTable.put(lookupTableIndex, copyOfMessages);
        }
        topicTable.put(lookupTableIndex, copyOfMessages);
        logger.info(copyOfMessages.toString());

        logger.info("*********KAFKA_DAEMON***********");
        logger.info("END_READ_NOTIFICATIONS");
        logger.info("********************");
    }

    private void process(ConsumerRecord<String, String> record) {

        //logger.info("CONSUME_DATA");
        //logger.info("RECORD_OFFSET: "+record.offset());
        //logger.info("RECORD_KEY: "+record.key());
        //logger.info("RECORD_VALUE: "+record.value());
        //logger.info("....");
    }

    private synchronized void printNotificationsQueue()
    {
        logger.info("******************");
        logger.info("Queue Size: "+readNotificationsQueue.size());
        Iterator<NotificationContext> iterator = readNotificationsQueue.iterator();
        while(iterator.hasNext())
        {
            NotificationContext notificationContext = iterator.next();
            logger.info("NotificationContextId: "+notificationContext.getMessageWindow().getStart());
        }
        logger.info("**********************");
    }
}
