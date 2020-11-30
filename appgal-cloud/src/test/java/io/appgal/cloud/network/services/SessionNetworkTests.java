package io.appgal.cloud.network.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.MessageWindow;
import io.appgal.cloud.model.SourceNotification;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@QuarkusTest
public class SessionNetworkTests {
    private static Logger logger = LoggerFactory.getLogger(SessionNetworkTests.class);

    @Inject
    private SessionNetwork sessionNetwork;

    @Inject
    private ProcessIncomingPackets processIncomingPackets;

    @BeforeEach
    private void setUp()
    {
        this.sessionNetwork.start();
    }

    @AfterEach
    private void tearDown()
    {
        this.sessionNetwork.stop();
    }

    //@Test
    public void testStart() throws Exception
    {
        int counter=0;

        List<String> notificationIds = new ArrayList<>();
        LocalDateTime startOfLocalDateInUtc = LocalDate.now(ZoneOffset.UTC).atStartOfDay();
        OffsetDateTime startTime = OffsetDateTime.of(startOfLocalDateInUtc, ZoneOffset.UTC);
        OffsetDateTime endTime = OffsetDateTime.now(ZoneOffset.UTC);
        MessageWindow messageWindow = new MessageWindow();
        messageWindow.setStart(startTime);
        messageWindow.setEnd(endTime);
        JsonArray jsonArray = new JsonArray();
        for(int i=0; i<1; i++)
        {
            String sourceNotificationId = UUID.randomUUID().toString();
            SourceNotification sourceNotification = new SourceNotification();
            sourceNotification.setSourceNotificationId(sourceNotificationId);
            sourceNotification.setMessageWindow(messageWindow);

            notificationIds.add(sourceNotificationId);

            JsonObject jsonObject = JsonParser.parseString(sourceNotification.toString()).getAsJsonObject();
        }

        for(int i=0; i<1; i++)
        {
            //Receive notifications from the SourceNotification Kafka Channel
            startOfLocalDateInUtc = LocalDate.now(ZoneOffset.UTC).atStartOfDay();
            startTime = OffsetDateTime.of(startOfLocalDateInUtc, ZoneOffset.UTC);
            endTime = OffsetDateTime.now(ZoneOffset.UTC);
            messageWindow = new MessageWindow();
            messageWindow.setStart(startTime);
            messageWindow.setEnd(endTime);

            logger.info(jsonArray.toString());
        }

        Thread.sleep(15000);


        /*Map<String, Map<String, JsonArray>> lookupTable = this.kafkaDaemon.getLookupTable();
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
        }*/
    }
}
