package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.ActiveFoodRunnerData;
import io.appgal.cloud.model.DestinationNotification;
import io.appgal.cloud.model.SourceNotification;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class KafkaDaemonTests {
    private static Logger logger = LoggerFactory.getLogger(KafkaDaemonTests.class);

    @Inject
    private KafkaDaemon kafkaDaemon;

    @Test
    public void testAddingDestinationNotifications() throws InterruptedException {
        this.kafkaDaemon.logStartUp();

        int counter=0;
        while(!this.kafkaDaemon.getActive()) {
            Thread.sleep(5000);
            if(counter++ == 3)
            {
                break;
            }
        }

        logger.info("****");
        logger.info("ABOUT_TO_PRODUCE_DATA");
        logger.info("****");

        List<String> notificationIds = new ArrayList<>();
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        MessageWindow messageWindow = new MessageWindow(start, end);
        JsonArray jsonArray = new JsonArray();
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

            this.kafkaDaemon.produceData(DestinationNotification.TOPIC, jsonObject);
        }

        Thread.sleep(30000);

        logger.info("****");
        logger.info("ABOUT_TO_ASSERT_DATA");
        logger.info("****");

        jsonArray = this.kafkaDaemon.readNotifications(DestinationNotification.TOPIC, messageWindow);
        logger.info("TIME_TO_ASSERT_DESTINATION_NOTIFICATION");
        assertNotNull(jsonArray);
        logger.info(jsonArray.toString());
    }

    @Test
    public void testAddingSourceNotifications() throws InterruptedException {
        this.kafkaDaemon.logStartUp();
        int counter=0;
        while(!this.kafkaDaemon.getActive()) {
            Thread.sleep(5000);
            if(counter++ == 15)
            {
                break;
            }
        }

        logger.info("****");
        logger.info("ABOUT_TO_PRODUCE_DATA");
        logger.info("****");

        List<String> notificationIds = new ArrayList<>();
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        MessageWindow messageWindow = new MessageWindow(start, end);
        JsonArray jsonArray = new JsonArray();
        for(int i=0; i<10; i++)
        {
            String sourceNotificationId = UUID.randomUUID().toString();
            SourceNotification sourceNotification = new SourceNotification();
            sourceNotification.setSourceNotificationId(sourceNotificationId);
            sourceNotification.setMessageWindow(messageWindow);

            notificationIds.add(sourceNotificationId);

            JsonObject jsonObject = JsonParser.parseString(sourceNotification.toString()).getAsJsonObject();

            this.kafkaDaemon.produceData(SourceNotification.TOPIC, jsonObject);
        }

        Thread.sleep(30000);

        logger.info("****");
        logger.info("ABOUT_TO_ASSERT_DATA");
        logger.info("****");

        jsonArray = this.kafkaDaemon.readNotifications(SourceNotification.TOPIC, messageWindow);
        logger.info("TIME_TO_ASSERT_SOURCE_NOTIFICATION");
        assertNotNull(jsonArray);
        logger.info(jsonArray.toString());
    }

    @Test
    public void testProduceActiveFoodRunnerData() throws InterruptedException {
        this.kafkaDaemon.logStartUp();
        int counter=0;
        while(!this.kafkaDaemon.getActive()) {
            Thread.sleep(5000);
            if(counter++ == 15)
            {
                break;
            }
        }

        logger.info("****");
        logger.info("ABOUT_TO_PRODUCE_DATA");
        logger.info("****");

        ActiveFoodRunnerData activeFoodRunnerData = new ActiveFoodRunnerData(UUID.randomUUID().toString(), "latitude", "longitude");
        List<ActiveFoodRunnerData> list = new ArrayList<>();
        list.add(activeFoodRunnerData);
        this.kafkaDaemon.produceActiveFoodRunnerData(ActiveFoodRunnerData.TOPIC, list);

        Thread.sleep(30000);

        logger.info("****");
        logger.info("ABOUT_TO_ASSERT_DATA");
        logger.info("****");

        JsonArray jsonArray = this.kafkaDaemon.readActiveFoodRunnerData(ActiveFoodRunnerData.TOPIC, list);
        logger.info("TIME_TO_ASSERT_ACTIVE_FOOD_RUNNER_DATA");
        assertNotNull(jsonArray);
        logger.info(jsonArray.toString());
    }
}
