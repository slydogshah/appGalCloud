package io.appgal.cloud.messaging;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@ApplicationScoped
public class KafkaMessageConsumer {
    private static Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);

    private KafkaConsumer<String,String> kafkaConsumer;
    private CountDownLatch shutdownLatch;
    private List<String> topics;

    public KafkaMessageConsumer()
    {

    }

    @PostConstruct
    public void start() throws UnknownHostException {
        Properties config = new Properties();
        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("group.id", "foo");
        config.put("bootstrap.servers", "host1:9092,host2:9092");
        config.put("key.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class);
        config.put("value.deserializer", org.springframework.kafka.support.serializer.JsonDeserializer.class);

        this.topics = Arrays.asList(new String[]{"foodRunnerSyncProtocol_source_notification"});
        this.kafkaConsumer = new KafkaConsumer<String, String>(config);
        this.shutdownLatch = new CountDownLatch(1);
    }

    @PreDestroy
    public void stop()
    {
        this.kafkaConsumer.close();
    }

    public void consumeData()
    {
    }

    private class SubscriptionLifecycle implements Runnable
    {
        @Override
        public void run() {
            try {
                kafkaConsumer.subscribe(topics, new ConsumerRebalanceListener() {
                    @Override
                    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                        doCommitSync();
                    }

                    @Override
                    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {}
                });

                while (true) {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Long.MAX_VALUE);
                    records.forEach(record -> process(record));
                    kafkaConsumer.commitAsync();
                }
            } catch (WakeupException e) {
                // ignore, we're closing
            } catch (Exception e) {
                logger.error("Unexpected error", e);
            } finally {
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
