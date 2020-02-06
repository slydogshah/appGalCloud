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
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.*;

@ApplicationScoped
public class SourceNotificationsSource {
    private static Logger logger = LoggerFactory.getLogger(SourceNotificationsSource.class);

    private KafkaConsumer<String,String> kafkaConsumer;
    private CountDownLatch shutdownLatch;
    private List<String> topics;
    private ExecutorService executorService;
    private List<TopicPartition> topicPartitions;

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
        try {
            while (this.topicPartitions == null || this.topicPartitions.isEmpty()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    logger.debug(e.getMessage(), e);
                }
            }

            Future<JsonArray> future = this.executorService.submit(new SourceNotificationsReader(start, end));
            JsonArray jsonArray = future.get();

            return jsonArray;
        }
        catch(InterruptedException ie)
        {
            logger.debug(ie.getMessage(), ie);
            return new JsonArray();
        }
        catch(ExecutionException ie)
        {
            logger.debug(ie.getMessage(), ie);
            return new JsonArray();
        }
    }

    private class SourceNotificationsSubscriber implements Runnable
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
                        topicPartitions = Arrays.asList(partitions.toArray(new TopicPartition[0]));
                        logger.info("Number of Partitions: "+topicPartitions.size());
                    }
                });

                while (true) {
                    kafkaConsumer.poll(Long.MAX_VALUE);
                }
            }
            finally {
                shutdownLatch.countDown();
            }
        }
    }

    private class SourceNotificationsReader implements Callable<JsonArray>
    {
        private OffsetDateTime start;
        private OffsetDateTime end;

        private SourceNotificationsReader(OffsetDateTime start, OffsetDateTime end)
        {
            this.start = start;
            this.end = end;
        }

        @Override
        public JsonArray call() throws Exception {
            JsonArray jsonArray = new JsonArray();

            //Construct the parameters to read the Kafka Log
            Map<TopicPartition, Long> partitionParameter = new HashMap<>();
            for(TopicPartition topicPartition:topicPartitions)
            {
                partitionParameter.put(topicPartition, start.toEpochSecond());
                partitionParameter.put(topicPartition, end.toEpochSecond());
            }

            //
            Map<TopicPartition, OffsetAndTimestamp> topicPartitionOffsetAndTimestampMap = kafkaConsumer.offsetsForTimes(partitionParameter);
            Set<Map.Entry<TopicPartition, OffsetAndTimestamp>> entrySet = topicPartitionOffsetAndTimestampMap.entrySet();
            for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry:entrySet)
            {
                TopicPartition partition = entry.getKey();
                OffsetAndTimestamp offsetAndTimestamp = entry.getValue();

                JsonObject jsonObject = JsonParser.parseString(offsetAndTimestamp.toString()).getAsJsonObject();
                jsonArray.add(jsonObject);
            }

            return jsonArray;
        }
    }
}
