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
            config.put("auto.commit.interval.ms", 1000);
            config.put("enable.auto.commit", true);
            //config.put("session.timeout.ms", 30000);

            this.kafkaConsumer = new KafkaConsumer<String, String>(config);

            KafkaRebalanceListener rebalanceListener = new KafkaRebalanceListener(this.kafkaConsumer, this.readNotificationsQueue, this.topicPartitions, this.active);
            this.kafkaConsumer.subscribe(topics, rebalanceListener);
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
}
