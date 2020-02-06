package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

//TODO:Look at removing dependency on KafkaServer (@bugs.bunny.shah@gmail.com)

@QuarkusTest
public class KafkaMessagingTests {
    private static Logger logger = LoggerFactory.getLogger(KafkaMessagingTests.class);

    @Inject
    private KafkaMessageConsumer kafkaMessageConsumer;

    @Inject
    private KafkaMessageProducer kafkaMessageProducer;

    @Inject
    private SourceNotificationsSource sourceNotificationsSource;

    @Test
    public void testProducer() throws InterruptedException, UnknownHostException {
        this.kafkaMessageConsumer.consumeData();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sourceNotificationId", UUID.randomUUID().toString()+"/sourceNotificationId");
        this.kafkaMessageProducer.produceData(jsonObject);

        Thread.sleep(1000);


    }

    @Test
    public void testReadNotifications() throws InterruptedException, UnknownHostException {
        //this.kafkaMessageConsumer.consumeData();

        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC).minusHours(Duration.ofHours(6).toHours());
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());

        JsonArray sourceNotifications = this.sourceNotificationsSource.readNotifications(start, end);

        logger.info("**TIME_TO_ASSERT**");
        logger.info(sourceNotifications.toString());
        logger.info("****");

        Thread.sleep(1000);
    }
}
