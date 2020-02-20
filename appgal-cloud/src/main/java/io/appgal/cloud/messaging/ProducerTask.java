package io.appgal.cloud.messaging;

import com.google.gson.JsonObject;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Properties;
import java.util.concurrent.RecursiveAction;

public class ProducerTask extends RecursiveAction {
    private static Logger logger = LoggerFactory.getLogger(ProducerTask.class);

    private KafkaProducer<String,String> kafkaProducer;
    private String topic;
    private JsonObject jsonObject;

    public ProducerTask(String topic, JsonObject jsonObject)
    {
        this.topic = topic;
        this.jsonObject = jsonObject;
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
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void compute() {

        final ProducerRecord<String, String> record = new ProducerRecord<>(topic,
                topic, jsonObject.toString());

        this.kafkaProducer.send(record, new Callback() {
            public void onCompletion(RecordMetadata metadata, Exception e) {
                if (e != null) {
                    logger.error("Send failed for record {}", record, e);
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
}
