package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RecursiveAction;

public class StartDaemonTask extends RecursiveAction {
    private static Logger logger = LoggerFactory.getLogger(StartDaemonTask.class);

    private KafkaConsumer<String,String> kafkaConsumer;
    private List<String> topics = new ArrayList<>();

    private Boolean active;
    private CountDownLatch shutdownLatch;
    private Map<String,List<TopicPartition>> topicPartitions;
    private Queue<NotificationContext> readNotificationsQueue;

    public StartDaemonTask(Boolean active, List<String> topics,Queue<NotificationContext> readNotificationsQueue,
                           Map<String,List<TopicPartition>> topicPartitions)
    {
        this.active = active;
        this.topics = topics;
        this.readNotificationsQueue = readNotificationsQueue;
        this.topicPartitions = topicPartitions;
    }
    
    @Override
    protected void compute() {
        try {
            Properties config = new Properties();
            config.put("client.id", InetAddress.getLocalHost().getHostName());
            config.put("group.id", "foodRunnerSyncProtocol_notifications");
            config.put("bootstrap.servers", "localhost:9092");
            config.put("key.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class);
            config.put("value.deserializer", org.springframework.kafka.support.serializer.JsonDeserializer.class);
            config.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
            config.put("value.serializer", org.springframework.kafka.support.serializer.JsonSerializer.class);
            //config.put("session.timeout.ms", 30000);

            this.kafkaConsumer = new KafkaConsumer<String, String>(config);

            KafkaRebalanceListener rebalanceListener = new KafkaRebalanceListener(this.topicPartitions);
            this.kafkaConsumer.subscribe(topics, rebalanceListener);

            active = Boolean.TRUE;
            findNotifications();
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

    private void findNotifications()
    {
        try {
            do {
                logger.info("Start Long Poll");
                ConsumerRecords<String, String> records = kafkaConsumer.poll(20000);
                records.forEach(record -> process(record));

                //TODO: Read multiple NotificationContexts during this run
                NotificationContext notificationContext = readNotificationsQueue.poll();
                if (notificationContext == null) {
                    logger.info("NO_ACTIVE_READS_IN_PROGRESS");
                    continue;
                }
                MessageWindow messageWindow = notificationContext.getMessageWindow();
                if (messageWindow == null) {
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
                    JsonArray jsonArray = new JsonArray();
                    for (int i = 0; i < 30; i++) {
                        logger.info("Start Short Poll: (" + i + ")");
                        ConsumerRecords<String, String> notificationRecords =
                                kafkaConsumer.poll(100);
                        if (notificationRecords == null || notificationRecords.isEmpty()) {
                            continue;
                        }
                        for (ConsumerRecord<String, String> record : notificationRecords) {
                            logger.info("CONSUME_DATA_TEST_RECORD");
                            logger.info("RECORD_OFFSET: "+record.offset());
                            logger.info("RECORD_KEY: "+record.key());
                            logger.info("RECORD_VALUE: "+record.value());
                            logger.info("....");

                            String jsonValue = record.value();

                            JsonObject jsonObject = JsonParser.parseString(jsonValue).getAsJsonObject();
                            jsonArray.add(jsonObject);
                        }
                        messageWindow.setMessages(jsonArray);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                if (messageWindow.getMessages() == null) {
                    messageWindow.setMessages(new JsonArray());
                }
                logger.info("*********KAFKA_DAEMON***********");
                logger.info("END_READ_NOTIFICATIONS");
                logger.info("********************");
            } while (true);
        }
        catch(Exception ie)
        {
            logger.error(ie.getMessage(), ie);
            throw new RuntimeException(ie);
        }
    }

    private void process(ConsumerRecord<String, String> record) {

        logger.info("CONSUME_DATA");
        logger.info("RECORD_OFFSET: "+record.offset());
        logger.info("RECORD_KEY: "+record.key());
        logger.info("RECORD_VALUE: "+record.value());
        logger.info("....");

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
