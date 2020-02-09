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
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

    private Queue<MessageWindow> readNotificationsQueue;

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
        this.topicPartitions = new ArrayList<>();

        this.active = true;

        this.readNotificationsQueue = new LinkedList<>();

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
        while (!this.active) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                logger.debug(e.getMessage(), e);
            }
        }

        //TODO: make this a synchronized write
        MessageWindow messageWindow = new MessageWindow(start, end);
        this.readNotificationsQueue.add(messageWindow);

        logger.info("*********READ_NOTIFICATIONS***********");
        logger.info("JUST_FINISHED_WRITE");
        logger.info("*******************************");

        /*try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        JsonArray jsonArray = new JsonArray();
        return jsonArray;
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
                this.findNotifications();
            }
            catch (Exception e)
            {
                logger.error(e.getMessage(), e);
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

        private void findNotifications() throws InterruptedException
        {
                do {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Long.MAX_VALUE);
                    records.forEach(record -> process(record));
                    kafkaConsumer.commitAsync();

                    //Thread.sleep(5000);
                    MessageWindow messageWindow = readNotificationsQueue.poll();
                    if(messageWindow == null)
                    {
                        logger.info("*********KAFKA_DAEMON***********");
                        logger.info("SKIP_READ_NOTIFICATIONS");
                        logger.info("********************");
                        //Thread.sleep(5000);
                        continue;
                    }

                    logger.info("*********KAFKA_DAEMON***********");
                    logger.info("START_READ_NOTIFICATIONS");
                    logger.info("********************");

                    JsonArray jsonArray = new JsonArray();
                    try {
                        OffsetDateTime start = messageWindow.getStart();
                        OffsetDateTime end = messageWindow.getEnd();

                        //Construct the parameters to read the Kafka Log
                        Map<TopicPartition, Long> partitionParameter = new HashMap<>();
                        for (TopicPartition topicPartition : topicPartitions) {
                            partitionParameter.put(topicPartition, start.toEpochSecond());
                            partitionParameter.put(topicPartition, end.toEpochSecond());
                        }
                        partitionParameter = kafkaConsumer.endOffsets(topicPartitions);

                        //
                        Map<TopicPartition, OffsetAndTimestamp> topicPartitionOffsetAndTimestampMap = kafkaConsumer.offsetsForTimes(partitionParameter);
                        Set<Map.Entry<TopicPartition, OffsetAndTimestamp>> entrySet = topicPartitionOffsetAndTimestampMap.entrySet();
                        for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : entrySet) {
                            TopicPartition partition = entry.getKey();
                            OffsetAndTimestamp offsetAndTimestamp = entry.getValue();

                            //kafkaConsumer.seek(partition, offsetAndTimestamp.offset());
                            String jsonValue = offsetAndTimestamp.toString();
                            logger.info("***********************************");
                            logger.info("OFFSETDATETIME: " + offsetAndTimestamp.offset());
                            logger.info("JSON: " + jsonValue);
                            logger.info("***********************************");

                            JsonObject jsonObject = JsonParser.parseString(jsonValue).getAsJsonObject();
                            jsonArray.add(jsonObject);
                        }

                        //return jsonArray;
                    }
                    catch (Exception e)
                    {
                        logger.error(e.getMessage(), e);
                        //return jsonArray;
                    }
                    logger.info("***********************************");
                    logger.info("JSONArray: "+jsonArray.toString());
                    logger.info("***********************************");
                }while (true);
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
}
