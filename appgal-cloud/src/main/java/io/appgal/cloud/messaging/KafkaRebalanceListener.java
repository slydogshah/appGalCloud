package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.*;

public class KafkaRebalanceListener implements ConsumerRebalanceListener {
    private static Logger logger = LoggerFactory.getLogger(KafkaRebalanceListener.class);

    private KafkaConsumer<String,String> kafkaConsumer;
    private Map<String,List<TopicPartition>> topicPartitions;
    private Boolean active;
    private Queue<NotificationContext> readNotificationsQueue;

    public KafkaRebalanceListener(KafkaConsumer<String,String> kafkaConsumer,Queue<NotificationContext> readNotificationsQueue,Map<String, List<TopicPartition>> topicPartitions, Boolean active) {
        this.kafkaConsumer = kafkaConsumer;
        this.readNotificationsQueue = readNotificationsQueue;
        this.topicPartitions = topicPartitions;
        this.active = active;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> collection) {

        logger.info("********PARTITIONS_REVOKED**********");
        logger.info("************************************");
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        logger.info("********PARTITIONS_ASSIGNED**********");
        logger.info("************************************");
        this.active = Boolean.TRUE;

        List<TopicPartition> partitionList = Arrays.asList(partitions.toArray(new TopicPartition[0]));
        for (TopicPartition topicPartition : partitionList) {
            String registeredTopic = topicPartition.topic();

            List<TopicPartition> local = topicPartitions.get(registeredTopic);
            if (local != null) {
                local.add(topicPartition);
            } else {
                topicPartitions.put(registeredTopic, Arrays.asList(topicPartition));
            }
            logger.info("******************************************");
            logger.info("NUMBER_OF_PARTITIONS registered for :(" + registeredTopic + ") " + topicPartitions.size());
            logger.info("******************************************");
        }

        findNotifications();
    }

    @Override
    public void onPartitionsLost(Collection<TopicPartition> partitions) {
        logger.info("********PARTITIONS_LOST**********");
        logger.info("************************************");

    }

    private void findNotifications()
    {
        try {
            do {
                logger.info("Start Long Poll");
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Long.MAX_VALUE);
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
                            messageWindow.addMessage(jsonObject);
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
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
