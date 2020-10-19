package io.appgal.cloud.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.messaging.KafkaDaemon;
import io.appgal.cloud.messaging.MessageWindow;

import io.appgal.cloud.model.SourceNotification;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.inject.Inject;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@QuarkusTest
public class ProcessIncomingPacketsTests {
    private static Logger logger = LoggerFactory.getLogger(ProcessIncomingPacketsTests.class);

    @Inject
    private ProcessIncomingPackets processIncomingPackets;

    @Inject
    private KafkaDaemon kafkaDaemon;

    @BeforeEach
    public void setUp() throws InterruptedException {
        JsonObject jsonObject = new JsonObject();
        List<String> ids = new ArrayList<>();
        for(int i=0; i< 10; i++) {
            jsonObject = new JsonObject();
            String id = UUID.randomUUID().toString();
            ids.add(id);
            jsonObject.addProperty("sourceNotificationId", id);
            this.kafkaDaemon.produceData(SourceNotification.TOPIC, jsonObject);
        }
    }

    @Test
    public void testProcessSourceNotification()
    {
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        MessageWindow messageWindow = new MessageWindow();
        messageWindow.setStart(start);
        messageWindow.setEnd(end);
        this.processIncomingPackets.processSourceNotification(messageWindow);

        JsonArray jsonArray = kafkaDaemon.readNotifications(SourceNotification.TOPIC, messageWindow);
        logger.info(jsonArray.toString());
        assertNotNull(jsonArray);
        logger.info("NUMBER_OF_NOTIFICATIONS: "+jsonArray.size());
    }

    @Test
    public void testProcessSourceNotificationForPickup()
    {
        SourceNotification sourceNotification = new SourceNotification();
        sourceNotification.setSourceNotificationId("92ed655a-99a2-438b-8eeb-05d12a2d8a1b");
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        MessageWindow messageWindow = new MessageWindow();
        messageWindow.setStart(start);
        messageWindow.setEnd(end);
        sourceNotification.setMessageWindow(messageWindow);
        JsonArray json = this.processIncomingPackets.processNotificationForPickup(sourceNotification);

        //assert the body
        JsonArray jsonArray = JsonParser.parseString(json.toString()).getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        String sourceNotificationId = jsonObject.get("sourceNotificationId").getAsString();
        assertEquals("92ed655a-99a2-438b-8eeb-05d12a2d8a1b", sourceNotificationId);
    }
}
