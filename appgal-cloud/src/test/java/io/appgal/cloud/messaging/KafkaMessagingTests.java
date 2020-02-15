package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.appgal.cloud.model.SourceNotification;
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
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

//TODO:Look at removing dependency on KafkaServer (@bugs.bunny.shah@gmail.com)

@QuarkusTest
public class KafkaMessagingTests {
    private static Logger logger = LoggerFactory.getLogger(KafkaMessagingTests.class);

    @Inject
    private KafkaDaemonClient kafkaDaemonClient;

    @Test
    public void testReadNotifications() throws InterruptedException, UnknownHostException {
        JsonObject jsonObject = new JsonObject();
        List<String> ids = new ArrayList<>();
        for(int i=0; i< 10; i++) {
            jsonObject = new JsonObject();
            String id = UUID.randomUUID().toString();
            ids.add(id);
            jsonObject.addProperty("sourceNotificationId", id);
            this.kafkaDaemonClient.produceData(jsonObject);
        }

        for(int i=0; i<10; i++) {
            System.out.println("***WAITING****("+i+")");
            Thread.sleep(10);
        }

        System.out.println("****About to read the notifications back****");

        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        MessageWindow messageWindow = new MessageWindow(start, end);
        this.kafkaDaemonClient.readNotifications(SourceNotification.TOPIC, messageWindow);

        System.out.println("****Waiting for results****");
        for(int i=0; i<30; i++)
        {
            System.out.println("***WAITING_ON_RESULTS****("+i+")");
            if(messageWindow.getMessages() != null)
            {
                break;
            }
            Thread.sleep(1000);
        }

        System.out.println("TIME_TO_ASSERT");

        JsonArray messages = messageWindow.getMessages();
        assertNotNull(messages);
        //logger.info("***************TIME_TO_ASSERT***************************");
        //logger.info(messages.toString());
        //logger.info("********************************************************");

        //assert the size
        int idMatchCount = 0;
        Iterator<JsonElement> iterator = messages.iterator();
        while(iterator.hasNext())
        {
            JsonObject local = iterator.next().getAsJsonObject();
            String localId = local.get("sourceNotificationId").getAsString();
            if(ids.contains(localId))
            {
                idMatchCount++;
            }
        }
        assertEquals(ids.size(), idMatchCount);
    }

    //@Test
    public void testEmitNotifications() throws InterruptedException, UnknownHostException {
        JsonObject jsonObject = new JsonObject();
        List<String> ids = new ArrayList<>();
        for(int i=0; i< 10; i++) {
            jsonObject = new JsonObject();
            String id = UUID.randomUUID().toString();
            ids.add(id);
            jsonObject.addProperty("sourceNotificationId", id);

            String destinationNotificationId = UUID.randomUUID().toString();
            jsonObject.addProperty("destinationNotificationId", destinationNotificationId);

            this.kafkaDaemonClient.produceData(jsonObject);
        }

        //Thread.sleep(30000);
    }
}
