package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.appgal.cloud.messaging.MessageWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class SourceNotificationTests {
    private static Logger logger = LoggerFactory.getLogger(SourceNotificationTests.class);

    @Test
    public void testToString()
    {
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        long startTimestamp = start.toEpochSecond();
        long endTimestamp = end.toEpochSecond();
        MessageWindow messageWindow = new MessageWindow(start, end);

        String sourceNotificationId = UUID.randomUUID().toString();
        SourceNotification sourceNotification = new SourceNotification();
        sourceNotification.setSourceNotificationId(sourceNotificationId);
        sourceNotification.setMessageWindow(messageWindow);

        JsonObject json = JsonParser.parseString(sourceNotification.toString()).getAsJsonObject();
        logger.info("****");
        logger.info(sourceNotification.toString());
        logger.info("****");

        //assert the state
        assertEquals(sourceNotificationId, json.get("sourceNotificationId").getAsString());
        assertEquals(startTimestamp, json.get("startTimestamp").getAsLong());
        assertEquals(endTimestamp, json.get("endTimestamp").getAsLong());
    }

}
