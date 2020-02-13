package io.appgal.cloud.messaging;

import io.appgal.cloud.model.SourceNotification;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@QuarkusTest
public class SourceNotificationEmitterTests {
    private static Logger logger = LoggerFactory.getLogger(SourceNotificationEmitterTests.class);

    @Inject
    private SourceNotificationEmitter sourceNotificationEmitter;

    @Test
    public void testEmit()
    {
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        MessageWindow messageWindow = new MessageWindow(start, end);
        String sourceNotificationId = UUID.randomUUID().toString();
        SourceNotification sourceNotification = new SourceNotification();
        sourceNotification.setSourceNotificationId(sourceNotificationId);
        sourceNotification.setMessageWindow(messageWindow);

        this.sourceNotificationEmitter.emit(sourceNotification);
    }
}
