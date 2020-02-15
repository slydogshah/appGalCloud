package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.SourceNotification;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.*;

//TODO: Look into adding multiple KafkaConsumers per topic

@ApplicationScoped
public class KafkaDaemonClient {
    private static Logger logger = LoggerFactory.getLogger(KafkaDaemonClient.class);

    private KafkaConsumer<String,String> kafkaConsumer;
    private CountDownLatch shutdownLatch;
    private List<String> topics;
    private ExecutorService executorService;
    private Map<String,List<TopicPartition>> topicPartitions;

    private KafkaProducer<String,String> kafkaProducer;

    private Queue<NotificationContext> readNotificationsQueue;

    private boolean active = false;

    public KafkaDaemonClient()
    {

    }

    @PostConstruct
    public void start() throws UnknownHostException {
        Properties config = new Properties();
        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("group.id", "foo");
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class);
        config.put("value.deserializer", org.springframework.kafka.support.serializer.JsonDeserializer.class);
        config.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
        config.put("value.serializer", org.springframework.kafka.support.serializer.JsonSerializer.class);

        this.kafkaConsumer = new KafkaConsumer<String, String>(config);
        this.kafkaProducer = new KafkaProducer<>(config);

        this.topics = Arrays.asList(new String[]{SourceNotification.TOPIC});
        this.shutdownLatch = new CountDownLatch(1);
        this.topicPartitions = new HashMap<>();

        this.readNotificationsQueue = new LinkedList<>();

        //Integrate into an ExecutorService
        this.executorService = Executors.newCachedThreadPool();
        this.executorService.submit(new DaemonClientLauncher());
    }

    @PreDestroy
    public void stop()
    {
        this.readNotificationsQueue = null;
        this.executorService.shutdown();
        this.kafkaProducer.close();
        this.kafkaConsumer.close();
    }

    public boolean isActive() {
        return active;
    }

    public void produceData(JsonObject jsonObject)
    {
        if(!this.active)
        {
            throw new IllegalStateException("KAFKA_DAEMON_CLIENT_NOT_READY");
        }

        final ProducerRecord<String, String> record = new ProducerRecord<>(this.topics.get(0),
                "sourceNotification", jsonObject.toString());

        this.kafkaProducer.send(record, new Callback() {
            public void onCompletion(RecordMetadata metadata, Exception e) {
                if (e != null) {
                    logger.error("Send failed for record {}", record, e);
                }
                else
                {
                    //logger.info("******************************************");
                    //logger.info("PRODUCE_DATA");
                    //logger.info("RECORD_META_DATA: "+metadata.toString());
                    //logger.info("******************************************");
                }
            }
        });
    }

    public JsonArray readNotifications(String topic, MessageWindow messageWindow)
    {
        try {
            Future future = this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    //TODO: make this a synchronized write
                    NotificationContext notificationContext = new NotificationContext(topic, messageWindow);
                    readNotificationsQueue.add(notificationContext);

                    while (messageWindow.getMessages() == null)
                    {
                        logger.info(notificationContext.getMessageWindow().toString());
                        logger.info(messageWindow.toString());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            });
            future.get();

            return messageWindow.getMessages();
        }
        catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    //-----Runnables/Tasks that execute on the DaemonClientThreadPool-----------
    private class DaemonClientLauncher implements Runnable
    {
        @Override
        public void run() {
            try {
                kafkaConsumer.subscribe(topics, new ConsumerRebalanceListener() {
                    @Override
                    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    }

                    @Override
                    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                        List<TopicPartition> partitionList = Arrays.asList(partitions.toArray(new TopicPartition[0]));
                        topicPartitions.put(partitions.iterator().next().topic(), partitionList);
                        logger.info("******************************************");
                        logger.info("Number of Partitions: "+topicPartitions.size());
                        logger.info("******************************************");
                        active = true;
                    }
                });
                this.findNotifications();
            }
            catch (Exception e)
            {
                logger.error(e.getMessage(), e);
            }
            finally {
                try {
                    kafkaConsumer.commitSync();
                } finally {
                    kafkaConsumer.close();
                    shutdownLatch.countDown();
                }
            }
        }

        private void findNotifications() throws InterruptedException
        {
            JsonArray jsonArray = new JsonArray();
                do {
                    //logger.info("Start Long Poll");
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(20000);
                    records.forEach(record -> process(record));

                    //TODO: Read multiple NotificationContexts during this run
                    NotificationContext notificationContext = readNotificationsQueue.poll();
                    if(notificationContext == null)
                    {
                        logger.info("NO_ACTIVE_READS_IN_PROGRESS");
                        continue;
                    }
                    MessageWindow messageWindow = notificationContext.getMessageWindow();
                    if(messageWindow == null)
                    {
                        logger.info("*********KAFKA_DAEMON***********");
                        logger.info("SKIP_READ_NOTIFICATIONS");
                        logger.info("********************");
                        continue;
                    }

                    logger.info("*********KAFKA_DAEMON***********");
                    logger.info("START_READ_NOTIFICATIONS");
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
                        for(int i=0; i<30; i++) {
                            logger.info("Start Short Poll: ("+i+")");
                            ConsumerRecords<String, String> notificationRecords =
                                    kafkaConsumer.poll(100);
                            if(notificationRecords == null || notificationRecords.isEmpty())
                            {
                                continue;
                            }
                            for (ConsumerRecord<String, String> record : notificationRecords) {
                                //logger.info("CONSUME_DATA_TEST_RECORD");
                                //logger.info("RECORD_OFFSET: "+record.offset());
                                //logger.info("RECORD_KEY: "+record.key());
                                //logger.info("RECORD_VALUE: "+record.value());
                                //logger.info("....");

                                String jsonValue = record.value();

                                JsonObject jsonObject = JsonParser.parseString(jsonValue).getAsJsonObject();
                                jsonArray.add(jsonObject);
                            }
                            messageWindow.setMessages(jsonArray);
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error(e.getMessage(), e);
                    }

                    if(messageWindow.getMessages() == null)
                    {
                        messageWindow.setMessages(new JsonArray());
                    }
                    logger.info("*********KAFKA_DAEMON***********");
                    logger.info("END_READ_NOTIFICATIONS");
                    logger.info("********************");
                }while (true);
        }

        private void process(ConsumerRecord<String, String> record) {

            //logger.info("CONSUME_DATA");
            //logger.info("RECORD_OFFSET: "+record.offset());
            //logger.info("RECORD_KEY: "+record.key());
            //logger.info("RECORD_VALUE: "+record.value());
            //logger.info("....");

            doCommitSync(record);
        }

        private void doCommitSync(ConsumerRecord<String,String> consumerRecord) {
            try {

                //Create the proper OffsetMetaData
                //OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(consumerRecord.offset());
                //Map<TopicPartition,OffsetAndMetadata> metadataMap = new HashMap<>();
                //metadataMap.put(topicPartitions.get(0), offsetAndMetadata);

                kafkaConsumer.commitSync();
            } catch (WakeupException e) {
                // we're shutting down, but finish the commit first and then
                // rethrow the exception so that the main loop can exit
                //doCommitSync();
                throw e;
            } catch (CommitFailedException e) {
                // the commit failed with an unrecoverable error. if there is any
                // internal state which depended on the commit, you can clean it
                // up here. otherwise it's reasonable to ignore the error and go on
                logger.debug("Commit failed", e);
            }
        }
    }
}
