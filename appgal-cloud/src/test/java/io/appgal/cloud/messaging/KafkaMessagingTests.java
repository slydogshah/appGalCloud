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
    private SourceNotificationsSource sourceNotificationsSource;

    @Inject
    private KafkaDaemonClient kafkaDaemonClient;

    @Test
    public void testReadNotifications() throws InterruptedException, UnknownHostException {

        //JsonObject jsonObject = new JsonObject();
        //jsonObject.addProperty("sourceNotificationId", UUID.randomUUID().toString()+"/sourceNotificationId");
        //this.kafkaDaemonClient.produceData(jsonObject);
        //Thread.sleep(10000);

        //OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC).minusHours(Duration.ofHours(6).toHours());
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        JsonArray sourceNotifications = this.kafkaDaemonClient.readNotifications(start, end);

        logger.info("***************TIME_TO_ASSERT***************************");
        logger.info(sourceNotifications.toString());
        logger.info("********************************************************");

        Thread.sleep(120000);
    }
}
