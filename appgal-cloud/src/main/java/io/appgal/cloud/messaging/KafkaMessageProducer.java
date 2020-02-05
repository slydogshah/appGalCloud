package io.appgal.cloud.messaging;

import com.google.gson.JsonObject;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@ApplicationScoped
public class KafkaMessageProducer {
    private static Logger logger = LoggerFactory.getLogger(KafkaMessageProducer.class);

    private KafkaProducer<String,String> kafkaProducer;
    private List<String> topics;

    public KafkaMessageProducer()
    {

    }

    @PostConstruct
    public void start() throws UnknownHostException {
        Properties config = new Properties();
        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("bootstrap.servers", "localhost:9092");
        config.put("acks", "all");
        config.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
        config.put("value.serializer", org.springframework.kafka.support.serializer.JsonSerializer.class);

        this.kafkaProducer = new KafkaProducer<>(config);
        this.topics = Arrays.asList(new String[]{"foodRunnerSyncProtocol_source_notification"});
    }

    @PreDestroy
    public void stop()
    {
        this.kafkaProducer.close();
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
                    logger.info("PRODUCE_DATA");
                }
            }
        });
    }
}
