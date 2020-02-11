package io.appgal.cloud.model;

import io.appgal.cloud.messaging.MessageWindow;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class SourceNotificationsTest {
    private static Logger logger = LoggerFactory.getLogger(SourceNotificationsTest.class);

    @Test
    public void testToString()
    {
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        MessageWindow messageWindow = new MessageWindow(start, end);

        SourceNotification sourceNotification = new SourceNotification();
        sourceNotification.setSourceNotificationId(UUID.randomUUID().toString());
        sourceNotification.setMessageWindow(messageWindow);

        logger.info(sourceNotification.toString());
    }

}
