package io.appgal.cloud.foodRunnerSync.protocol;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.messaging.KafkaDaemonClient;
import io.appgal.cloud.messaging.MessageWindow;

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
    private KafkaDaemonClient kafkaDaemonClient;

    @BeforeEach
    public void setUp() throws InterruptedException {
        while(!this.kafkaDaemonClient.isActive())
        {
            Thread.sleep(100);
        }

        JsonObject jsonObject = new JsonObject();
        List<String> ids = new ArrayList<>();
        for(int i=0; i< 10; i++) {
            jsonObject = new JsonObject();
            String id = UUID.randomUUID().toString();
            ids.add(id);
            jsonObject.addProperty("sourceNotificationId", id);
            this.kafkaDaemonClient.produceData(jsonObject);
        }
    }

    @Test
    public void testProcessSourceNotification()
    {
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        MessageWindow messageWindow = new MessageWindow(start, end);
        this.processIncomingPackets.processSourceNotification(messageWindow);

        JsonArray jsonArray = messageWindow.getMessages();
        assertNotNull(jsonArray);
        logger.info("NUMBER_OF_NOTIFICATIONS: "+jsonArray.size());
    }
}
