package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
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
public class KafkaDaemonClient {
    private static Logger logger = LoggerFactory.getLogger(KafkaDaemonClient.class);

    private KafkaConsumer<String,String> kafkaConsumer;
    private CountDownLatch shutdownLatch;
    private List<String> topics;
    private ExecutorService executorService;
    private List<TopicPartition> topicPartitions;
    private boolean active = false;

    private KafkaProducer<String,String> kafkaProducer;

    public KafkaDaemonClient()
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
        config.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
        config.put("value.serializer", org.springframework.kafka.support.serializer.JsonSerializer.class);

        this.kafkaConsumer = new KafkaConsumer<String, String>(config);
        this.kafkaProducer = new KafkaProducer<>(config);

        this.topics = Arrays.asList(new String[]{"foodRunnerSyncProtocol_source_notification"});
        this.shutdownLatch = new CountDownLatch(1);

        this.active = true;

        //Integrate into an ExecutorService
        this.executorService = Executors.newCachedThreadPool();
        this.executorService.submit(new DaemonClientLauncher());
    }

    @PreDestroy
    public void stop()
    {
        this.executorService.shutdown();
        this.kafkaProducer.close();
        this.kafkaConsumer.close();
    }

    public void produceData(JsonObject jsonObject)
    {
        final ProducerRecord<String, String> record = new ProducerRecord<>(this.topics.get(0),
                "sourceNotification", jsonObject.toString());

        this.kafkaProducer.send(record, new Callback() {
            public void onCompletion(RecordMetadata metadata, Exception e) {
                if (e != null) {
                    logger.debug("Send failed for record {}", record, e);
                }
                else
                {
                    logger.info("******************************************");
                    logger.info("PRODUCE_DATA");
                    logger.info("RECORD_META_DATA: "+metadata.toString());
                    logger.info("******************************************");
                }
            }
        });
    }

    public JsonArray readNotifications(OffsetDateTime start, OffsetDateTime end)
    {
        try {
            while (!this.active) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    logger.debug(e.getMessage(), e);
                }
            }

            Future<JsonArray> future = this.executorService.
                    submit(new SourceNotificationsReader(start, end));
            JsonArray jsonArray = future.get(10000, TimeUnit.MILLISECONDS);

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
        catch(TimeoutException te)
        {
            logger.debug(te.getMessage(), te);
            return new JsonArray();
        }
    }
    //-----Runnables/Tasks that execute on the DaemonClientThreadPool-----------
    private class DaemonClientLauncher implements Runnable
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
                        active = true;
                        logger.info("******************************************");
                        logger.info("Number of Partitions: "+topicPartitions.size());
                        logger.info("******************************************");
                    }
                });

                //Post and publish
                while (true) {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Long.MAX_VALUE);
                    records.forEach(record -> process(record));
                    kafkaConsumer.commitAsync();
                }
            }
            finally {
                try {
                    doCommitSync();
                } finally {
                    kafkaConsumer.close();
                    shutdownLatch.countDown();
                }
            }
        }

        private void process(ConsumerRecord<String, String> record) {

            logger.info("CONSUME_DATA");
            logger.info("RECORD_OFFSET: "+record.offset());
            logger.info("RECORD_KEY: "+record.key());
            logger.info("RECORD_VALUE: "+record.value());
            logger.info("....");
        }

        private void doCommitSync() {
            try {
                kafkaConsumer.commitSync();
            } catch (WakeupException e) {
                // we're shutting down, but finish the commit first and then
                // rethrow the exception so that the main loop can exit
                doCommitSync();
                throw e;
            } catch (CommitFailedException e) {
                // the commit failed with an unrecoverable error. if there is any
                // internal state which depended on the commit, you can clean it
                // up here. otherwise it's reasonable to ignore the error and go on
                logger.debug("Commit failed", e);
            }
        }
    }

    //AppLevel component. TODO: move this higher up the stack (@bugs.bunny.shah@gmail.com)
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
            logger.info("********************");
            logger.info("SourceNotificationReader...");
            logger.info("********************");

            kafkaConsumer.seekToBeginning(topicPartitions);
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
