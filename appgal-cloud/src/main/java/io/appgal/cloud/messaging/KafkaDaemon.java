package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.appgal.cloud.persistence.MongoDBJsonStore;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.*;
import java.util.concurrent.*;

@ApplicationScoped
public class KafkaDaemon {
    private static Logger logger = LoggerFactory.getLogger(KafkaDaemon.class);

    private Map<String,List<TopicPartition>> topicPartitions;

    private Queue<NotificationContext> readNotificationsQueue;
    private List<String> topics = new ArrayList<>();

    private Boolean active = Boolean.FALSE;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    private ForkJoinPool commonPool;

    @PostConstruct
    public void start()
    {
        this.topics = this.mongoDBJsonStore.findKafakaDaemonBootstrapData();

        this.topicPartitions = new HashMap<>();

        this.readNotificationsQueue = new LinkedList<>();


        this.commonPool = ForkJoinPool.commonPool();

        //Start the KafakDaemon
        StartDaemonTask startDaemonTask = new StartDaemonTask(this.active, this.topics, this.readNotificationsQueue,topicPartitions);
        this.commonPool.execute(startDaemonTask);
        startDaemonTask.join();
    }

    @PreDestroy
    public void stop()
    {
        this.readNotificationsQueue = null;
    }

    public Boolean getActive() {
        return active;
    }

    public void logStartUp()
    {
        logger.info("**********");
        logger.info("STARTING_KAFKA_DAEMON");
        logger.info("**********");
    }

    public void produceData(String topic, JsonObject jsonObject)
    {
        ProducerTask producerTask = new ProducerTask(topic, jsonObject);
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
