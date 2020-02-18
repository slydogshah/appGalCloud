package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.persistence.MongoDBJsonStore;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;

@ApplicationScoped
public class KafkaDaemon {
    private static Logger logger = LoggerFactory.getLogger(KafkaDaemon.class);

    private KafkaConsumer<String,String> kafkaConsumer;
    private CountDownLatch shutdownLatch;
    private ExecutorService executorService;
    private Map<String,List<TopicPartition>> topicPartitions;
    private KafkaProducer<String,String> kafkaProducer;

    private Queue<NotificationContext> readNotificationsQueue;
    private List<String> topics = new ArrayList<>();

    private boolean active = false;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    private ForkJoinPool commonPool;

    @PostConstruct
    public void start()
    {
        try {

            this.topics = this.mongoDBJsonStore.findKafakaDaemonBootstrapData();

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

            this.shutdownLatch = new CountDownLatch(1);
            this.topicPartitions = new HashMap<>();

            this.readNotificationsQueue = new LinkedList<>();

            //Integrate into an ExecutorService
            this.executorService = Executors.newCachedThreadPool();

            this.commonPool = ForkJoinPool.commonPool();

            //Start the KafakDaemon
            StartDaemonTask startDaemonTask = new StartDaemonTask(this.topics, this.kafkaConsumer);
            this.commonPool.execute(startDaemonTask);
        }
        catch(UnknownHostException unknownHostException)
        {
            logger.error(unknownHostException.getMessage(), unknownHostException);
            throw new RuntimeException(unknownHostException);
        }
    }

    @PreDestroy
    public void stop()
    {
        this.readNotificationsQueue = null;
        this.executorService.shutdown();
        this.kafkaProducer.close();
        this.kafkaConsumer.close();
    }

    public void produceData(String topic, JsonObject jsonObject)
    {
        ProducerTask producerTask = new ProducerTask(topic, jsonObject, this.kafkaProducer);
        this.commonPool.execute(producerTask);
    }

    public JsonArray readNotifications(String topic, MessageWindow messageWindow)
    {
        ConsumerTask consumerTask = new ConsumerTask(topic, messageWindow,
                this.kafkaConsumer,
                this.executorService,
                this.readNotificationsQueue);
        this.commonPool.execute(consumerTask);
        JsonArray jsonArray = consumerTask.join();
        return jsonArray;
    }
}
