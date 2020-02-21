package io.appgal.cloud.session;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.foodRunnerSync.protocol.ProcessIncomingPackets;
import io.appgal.cloud.messaging.KafkaDaemon;
import io.appgal.cloud.messaging.MessageWindow;

import io.appgal.cloud.model.SourceNotification;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.*;
import java.util.*;

@QuarkusTest
public class FoodRunnerSessionTests {
    private static Logger logger = LoggerFactory.getLogger(FoodRunnerSessionTests.class);

    @Inject
    private FoodRunnerSession foodRunnerSession;

    @Inject
    private ProcessIncomingPackets processIncomingPackets;

    @Inject
    private KafkaDaemon kafkaDaemon;

    @BeforeEach
    private void setUp()
    {
    }

    @AfterEach
    private void tearDown()
    {
    }

    @Test
    public void testStart() throws Exception
    {
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

        /*List<String> notificationIds = new ArrayList<>();
        LocalDateTime startOfLocalDateInUtc = LocalDate.now(ZoneOffset.UTC).atStartOfDay();
        OffsetDateTime startTime = OffsetDateTime.of(startOfLocalDateInUtc, ZoneOffset.UTC);
        OffsetDateTime endTime = OffsetDateTime.now(ZoneOffset.UTC);
        MessageWindow messageWindow = new MessageWindow(startTime, endTime);
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

        Thread.sleep(120000);

        this.foodRunnerSession.start();*/

        for(int i=0; i<100; i++)
        {
            //Receive notifications from the SourceNotification Kafka Channel
            LocalDateTime startOfLocalDateInUtc = LocalDate.now(ZoneOffset.UTC).atStartOfDay();
            OffsetDateTime startTime = OffsetDateTime.of(startOfLocalDateInUtc, ZoneOffset.UTC);
            OffsetDateTime endTime = OffsetDateTime.now(ZoneOffset.UTC);
            MessageWindow messageWindow = new MessageWindow(startTime, endTime);

            JsonArray jsonArray = this.kafkaDaemon.readNotifications(SourceNotification.TOPIC, messageWindow);
            logger.info(jsonArray.toString());
        }

        Thread.sleep(120000);

        LocalDateTime startOfLocalDateInUtc = LocalDate.now(ZoneOffset.UTC).atStartOfDay();
        OffsetDateTime startTime = OffsetDateTime.of(startOfLocalDateInUtc, ZoneOffset.UTC);
        OffsetDateTime endTime = OffsetDateTime.now(ZoneOffset.UTC);
        MessageWindow messageWindow = new MessageWindow(startTime, endTime);

        JsonArray jsonArray = this.kafkaDaemon.readNotifications(SourceNotification.TOPIC, messageWindow);
        logger.info("**************");
        logger.info(jsonArray.toString());
        logger.info("**************");

        Map<String, Map<String, JsonArray>> lookupTable = this.kafkaDaemon.getLookupTable();
        Set<Map.Entry<String, Map<String, JsonArray>>> entrySet = lookupTable.entrySet();
        for(Map.Entry<String, Map<String, JsonArray>> entry:entrySet)
        {
            Map<String, JsonArray> value = entry.getValue();
            Set<Map.Entry<String, JsonArray>> valueSet = value.entrySet();
            for(Map.Entry<String, JsonArray> local:valueSet)
            {
                String keyValue = local.getKey();
                JsonArray localValue = local.getValue();
                logger.info("Key: "+keyValue);
                logger.info("Value: "+localValue.toString());
                logger.info("***************");
            }
        }
    }
}
