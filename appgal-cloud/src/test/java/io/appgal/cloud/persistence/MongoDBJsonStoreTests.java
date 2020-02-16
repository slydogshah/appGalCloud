package io.appgal.cloud.persistence;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.appgal.cloud.messaging.MessageWindow;
import io.appgal.cloud.model.DestinationNotification;
import io.appgal.cloud.model.SourceNotification;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class MongoDBJsonStoreTests {
    private static Logger logger = LoggerFactory.getLogger(MongoDBJsonStoreTests.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @BeforeEach
    public void setUp()
    {

    }

    @AfterEach
    public void tearDown()
    {

    }

    @Test
    public void testFindDestinationNotifications()
    {
        List<String> notificationIds = new ArrayList<>();
        for(int i=0; i<10; i++)
        {
            OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
            OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
            MessageWindow messageWindow = new MessageWindow(start, end);

            String sourceNotificationId = UUID.randomUUID().toString();
            SourceNotification sourceNotification = new SourceNotification();
            sourceNotification.setSourceNotificationId(sourceNotificationId);
            sourceNotification.setMessageWindow(messageWindow);

            String destinationNotificationId = UUID.randomUUID().toString();
            DestinationNotification destinationNotification = new DestinationNotification();
            destinationNotification.setDestinationNotificationId(destinationNotificationId);
            destinationNotification.setSourceNotification(sourceNotification);

            notificationIds.add(destinationNotificationId);

            this.mongoDBJsonStore.storeDestinationNotifications(destinationNotification);
        }


        JsonArray jsonArray = this.mongoDBJsonStore.findDestinationNotifications(notificationIds);

        //assert
        assertNotNull(jsonArray);

        logger.info("****");
        logger.info(jsonArray.toString());
        logger.info("****");

        Iterator<JsonElement> iterator = jsonArray.iterator();
        int searchSize = 0;
        while(iterator.hasNext())
        {
            JsonObject local = iterator.next().getAsJsonObject();
            String localId = local.get("destinationNotificationId").getAsString();
            if(notificationIds.contains(localId))
            {
                searchSize++;
            }
        }
        assertEquals(notificationIds.size(), searchSize);
    }
}
