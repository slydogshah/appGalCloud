package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.google.gson.JsonParser;
import io.appgal.cloud.model.DestinationNotification;
import io.appgal.cloud.model.SourceNotification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.inject.Inject;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//TODO:Look at removing dependency on KafkaServer (@bugs.bunny.shah@gmail.com)

@QuarkusTest
public class KafkaMessagingTests {
    private static Logger logger = LoggerFactory.getLogger(KafkaMessagingTests.class);

    @Inject
    private KafkaDaemonClient kafkaDaemonClient;

    private List<String> ids = null;

    @BeforeEach
    public void setUp() throws InterruptedException {
        /*ids = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        for(int i=0; i< 10; i++) {
            jsonObject = new JsonObject();
            String id = UUID.randomUUID().toString();
            ids.add(id);
            jsonObject.addProperty("sourceNotificationId", id);
            this.kafkaDaemonClient.produceData(SourceNotification.TOPIC, jsonObject);
        }*/
    }

    @AfterEach
    public void tearDown()
    {
        this.ids = null;
    }

    @Test
    public void testReadNotifications() throws InterruptedException, UnknownHostException {
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        MessageWindow messageWindow = new MessageWindow(start, end);
        JsonArray jsonArray = this.kafkaDaemonClient.readNotifications(SourceNotification.TOPIC, messageWindow);

        logger.info("TIME_TO_ASSERT");
        assertNotNull(jsonArray);

        //assert
        /*Iterator<JsonElement> iterator = jsonArray.iterator();
        boolean searchSuccess = true;
        while(iterator.hasNext())
        {
            JsonObject local = iterator.next().getAsJsonObject();
            String localId = local.get("sourceNotificationId").getAsString();
            if(!ids.contains(localId))
            {
                searchSuccess = false;
                break;
            }
        }
        assertTrue(searchSuccess);*/
    }

    @Test
    public void testEmitNotifications() throws InterruptedException, UnknownHostException {
        List<String> notificationIds = new ArrayList<>();
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        MessageWindow messageWindow = new MessageWindow(start, end);
        for(int i=0; i<10; i++)
        {
            String sourceNotificationId = UUID.randomUUID().toString();
            SourceNotification sourceNotification = new SourceNotification();
            sourceNotification.setSourceNotificationId(sourceNotificationId);
            sourceNotification.setMessageWindow(messageWindow);

            String destinationNotificationId = UUID.randomUUID().toString();
            DestinationNotification destinationNotification = new DestinationNotification();
            destinationNotification.setDestinationNotificationId(destinationNotificationId);
            destinationNotification.setSourceNotification(sourceNotification);

            notificationIds.add(destinationNotificationId);

            JsonObject jsonObject = JsonParser.parseString(destinationNotification.toString()).getAsJsonObject();

            this.kafkaDaemonClient.produceData(DestinationNotification.TOPIC, jsonObject);
        }
        //Thread.sleep(30000);

        JsonArray jsonArray = this.kafkaDaemonClient.readNotifications(DestinationNotification.TOPIC, messageWindow);

        logger.info("TIME_TO_ASSERT");
        assertNotNull(jsonArray);
        logger.info(jsonArray.toString());
    }
}
