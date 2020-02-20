package io.appgal.cloud.messaging;


import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class StartDaemonTask extends RecursiveAction {
    private static Logger logger = LoggerFactory.getLogger(StartDaemonTask.class);

    private KafkaConsumer<String,String> kafkaConsumer;
    private List<String> topics = new ArrayList<>();

    private Boolean active;
    private CountDownLatch shutdownLatch;
    private Map<String,List<TopicPartition>> topicPartitions;
    private Queue<NotificationContext> readNotificationsQueue;

    public StartDaemonTask(KafkaConsumer<String,String> kafkaConsumer, Boolean active, List<String> topics,Queue<NotificationContext> readNotificationsQueue,
                           Map<String,List<TopicPartition>> topicPartitions)
    {
        this.active = active;
        this.topics = topics;
        this.readNotificationsQueue = readNotificationsQueue;
        this.topicPartitions = topicPartitions;
        this.shutdownLatch = new CountDownLatch(1);
        this.kafkaConsumer = kafkaConsumer;
    }
    
    @Override
    protected void compute() {
        try {
            ForkJoinPool commonPool = ForkJoinPool.commonPool();
            KafkaRebalanceListener rebalanceListener = new KafkaRebalanceListener(this.kafkaConsumer, this.readNotificationsQueue, this.topics,
                    this.topicPartitions, this.active);
            commonPool.execute(rebalanceListener);

            rebalanceListener.join();
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
