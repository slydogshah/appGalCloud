package io.appgal.cloud.messaging;

import com.google.gson.JsonObject;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

//TODO:Look at removing dependency on KafkaServer (@bugs.bunny.shah@gmail.com)

@QuarkusTest
public class KafkaMessagingTests {
    private static Logger logger = LoggerFactory.getLogger(KafkaMessagingTests.class);

    @Inject
    private KafkaMessageConsumer kafkaMessageConsumer;

    @Inject
    private KafkaMessageProducer kafkaMessageProducer;

    @Test
    public void test()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sourceNotificationId", UUID.randomUUID().toString()+"/sourceNotificationId");
        this.kafkaMessageProducer.produceData(jsonObject);
    }
}
