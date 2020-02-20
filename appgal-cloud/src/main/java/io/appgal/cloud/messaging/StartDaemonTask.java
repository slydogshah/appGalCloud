package io.appgal.cloud.messaging;


import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RecursiveAction;

public class StartDaemonTask extends RecursiveAction {
    private static Logger logger = LoggerFactory.getLogger(StartDaemonTask.class);

    private KafkaConsumer<String,String> kafkaConsumer;
    private List<String> topics = new ArrayList<>();

    private CountDownLatch shutdownLatch;
    private Map<String,List<TopicPartition>> topicPartitions;
    private Queue<NotificationContext> readNotificationsQueue;

    public StartDaemonTask(KafkaConsumer<String,String> kafkaConsumer,List<String> topics,Queue<NotificationContext> readNotificationsQueue,
                           Map<String,List<TopicPartition>> topicPartitions)
    {
        this.topics = topics;
        this.readNotificationsQueue = readNotificationsQueue;
        this.topicPartitions = topicPartitions;
        this.shutdownLatch = new CountDownLatch(1);
        this.kafkaConsumer = kafkaConsumer;
    }
    
    @Override
    protected void compute() {
        try {
            KafkaRebalanceListener rebalanceListener = new KafkaRebalanceListener(this.kafkaConsumer, this.readNotificationsQueue, this.topics,
                    this.topicPartitions);
            this.kafkaConsumer.subscribe(this.topics, rebalanceListener);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
        finally {
            kafkaConsumer.close();
            shutdownLatch.countDown();
        }
    }
}
