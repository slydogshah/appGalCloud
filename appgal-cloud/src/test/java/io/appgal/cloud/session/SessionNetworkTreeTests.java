package io.appgal.cloud.session;

import com.google.gson.JsonObject;
import io.appgal.cloud.messaging.MessageWindow;
import io.appgal.cloud.model.SourceNotification;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class SessionNetworkTreeTests {
    private static Logger logger = LoggerFactory.getLogger(SessionNetworkTreeTests.class);

    @Test
    public void testSessionNetworkLookup()
    {
        SessionNetwork sessionNetwork = new SessionNetwork();

        TreeMap<String, FoodRunnerSession> foodRunnerSessions = sessionNetwork.getFoodRunnerSessions();

        FoodRunnerSession foodRunnerSession = new FoodRunnerSession();
        LocalDateTime startOfLocalDateInUtc = LocalDate.now(ZoneOffset.UTC).atStartOfDay();
        OffsetDateTime startTime = OffsetDateTime.of(startOfLocalDateInUtc, ZoneOffset.UTC);
        OffsetDateTime endTime = OffsetDateTime.now(ZoneOffset.UTC);
        MessageWindow messageWindow = new MessageWindow(startTime, endTime);
        for(int i=0; i<1; i++)
        {
            String sourceNotificationId = UUID.randomUUID().toString();
            SourceNotification sourceNotification = new SourceNotification();
            sourceNotification.setSourceNotificationId(sourceNotificationId);
            sourceNotification.setMessageWindow(messageWindow);

            JsonObject notification = new JsonObject();
            notification.addProperty("sourceNotificationId", sourceNotificationId);
            messageWindow.addMessage(notification);

            foodRunnerSession.receiveNotifications(messageWindow);
        }

        //Time to assert
        logger.info("MessageWindow: "+messageWindow.toString());
        Map<String, List<SourceNotification>> sourceNotifications = foodRunnerSession.getSourceNotifications();
        Set<Map.Entry<String, List<SourceNotification>>> entrySet = sourceNotifications.entrySet();
        for(Map.Entry<String, List<SourceNotification>> entry:entrySet)
        {
            List<SourceNotification> notifications = entry.getValue();
            logger.info(notifications.toString());
            logger.info(notifications.iterator().next().getMessageWindow().toString());
        }
    }
}
