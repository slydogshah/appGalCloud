package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class SourceNotificationsSource {
    private static Logger logger = LoggerFactory.getLogger(SourceNotificationsSource.class);

    private KafkaConsumer<String,String> kafkaConsumer;
    private CountDownLatch shutdownLatch;
    private List<String> topics;
    private ExecutorService executorService;
    private Set<TopicPartition> topicPartitions;

    public SourceNotificationsSource()
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

        this.topics = Arrays.asList(new String[]{"foodRunnerSyncProtocol_source_notification"});
        this.kafkaConsumer = new KafkaConsumer<String, String>(config);
        this.shutdownLatch = new CountDownLatch(1);

        //Integrate into an ExecutorService
        this.executorService = Executors.newCachedThreadPool();
        this.executorService.submit(new SourceNotificationsSubscriber());
    }

    @PreDestroy
    public void stop()
    {
        this.executorService.shutdown();
        this.kafkaConsumer.close();
    }

    public JsonArray readNotifications(OffsetDateTime start, OffsetDateTime end)
    {
        JsonArray jsonArray = new JsonArray();

        //Set<TopicPartition> topicPartitions = this.kafkaConsumer.assignment();

        //Construct the parameters to read the Kafka Log
        //Map<TopicPartition, Long> partitionParameter = new HashMap<>();
        //for(TopicPartition topicPartition:topicPartitions)
        //{
        //    partitionParameter.put(topicPartition, start.toEpochSecond());
        //    partitionParameter.put(topicPartition, end.toEpochSecond());
        //}

        //
        //Map<TopicPartition, OffsetAndTimestamp> topicPartitionOffsetAndTimestampMap = this.kafkaConsumer.offsetsForTimes(partitionParameter);
        //Set<Map.Entry<TopicPartition, OffsetAndTimestamp>> entrySet = topicPartitionOffsetAndTimestampMap.entrySet();
        //for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry:entrySet)
        //{
        //    TopicPartition partition = entry.getKey();
        //    OffsetAndTimestamp offsetAndTimestamp = entry.getValue();

        //    JsonObject jsonObject = JsonParser.parseString(offsetAndTimestamp.toString()).getAsJsonObject();
        //    jsonArray.add(jsonObject);
        //}

        return jsonArray;
    }

    private class SourceNotificationsSubscriber implements Runnable
    {
        @Override
        public void run() {
            try {
                kafkaConsumer.subscribe(topics);
                topicPartitions = kafkaConsumer.assignment();
                logger.info("Number of Partitions: "+topicPartitions.size());

                //Construct the parameters to read the Kafka Log
                OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC).minusHours(Duration.ofHours(6).toHours());
                OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
                Map<TopicPartition, Long> partitionParameter = new HashMap<>();
                for(TopicPartition topicPartition:topicPartitions)
                {
                    partitionParameter.put(topicPartition, start.toEpochSecond());
                    partitionParameter.put(topicPartition, end.toEpochSecond());
                }

                //
                JsonArray jsonArray = new JsonArray();
                Map<TopicPartition, OffsetAndTimestamp> topicPartitionOffsetAndTimestampMap = kafkaConsumer.offsetsForTimes(partitionParameter);
                Set<Map.Entry<TopicPartition, OffsetAndTimestamp>> entrySet = topicPartitionOffsetAndTimestampMap.entrySet();
                for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry:entrySet)
                {
                    TopicPartition partition = entry.getKey();
                    OffsetAndTimestamp offsetAndTimestamp = entry.getValue();

                    JsonObject jsonObject = JsonParser.parseString(offsetAndTimestamp.toString()).getAsJsonObject();
                    jsonArray.add(jsonObject);
                }

                logger.info(jsonArray.toString());

                while (true) {
                    kafkaConsumer.poll(Long.MAX_VALUE);
                }
            }
            finally {
                shutdownLatch.countDown();
            }
        }
    }
}
