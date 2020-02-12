package io.appgal.cloud.foodRunnerSync.protocol;

import io.appgal.cloud.messaging.MessageWindow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.inject.Inject;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@QuarkusTest
public class ProcessIncomingPacketsTests {
    private static Logger logger = LoggerFactory.getLogger(ProcessIncomingPacketsTests.class);

    @Inject
    private ProcessIncomingPackets processIncomingPackets;

    @Test
    public void testProcessSourceNotification()
    {
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        MessageWindow messageWindow = new MessageWindow(start, end);
        this.processIncomingPackets.processSourceNotification(messageWindow);

        assertNull(messageWindow.getMessages());
    }
}
