package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.appgal.cloud.persistence.MongoDBJsonStore;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.net.InetAddress;
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
    private List<String> topics = new ArrayList<>();

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    private ForkJoinPool commonPool;

    public KafkaDaemon()
    {
        this.topicPartitions = new HashMap<>();
        this.readNotificationsQueue = new LinkedList<>();
        this.shutdownLatch = new CountDownLatch(1);
        this.commonPool = ForkJoinPool.commonPool();
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

                this.kafkaConsumer = new KafkaConsumer<String, String>(config);
            }
            catch(Exception e)
            {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }

            //Start the KafakDaemon
            /*StartDaemonTask startDaemonTask = new StartDaemonTask(this.kafkaConsumer, this.topics, this.readNotificationsQueue, topicPartitions);
            this.commonPool.execute(startDaemonTask);
            startDaemonTask.join();*/
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
        ConsumerTask consumerTask = new ConsumerTask(topic, messageWindow,
                this.readNotificationsQueue, this.commonPool);
        this.commonPool.execute(consumerTask);
        JsonArray jsonArray = consumerTask.join();

        return messageWindow.getCopyOfMessages();
    }
}
