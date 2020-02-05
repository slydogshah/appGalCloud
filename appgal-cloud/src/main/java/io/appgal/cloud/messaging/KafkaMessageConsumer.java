package io.appgal.cloud.messaging;

import com.google.gson.JsonDeserializer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

@ApplicationScoped
public class KafkaMessageConsumer {
    private static Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);

    private KafkaConsumer<String,String> kafkaConsumer;

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
        this.kafkaConsumer = new KafkaConsumer<String, String>(config);
    }

    @PreDestroy
    public void stop()
    {
        this.kafkaConsumer.close();
    }

    public void consumeData()
    {
        logger.info("CONSUME_DATA");
    }
}
