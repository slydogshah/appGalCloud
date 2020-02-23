package io.appgal.cloud.session;

import com.google.gson.JsonObject;
import io.appgal.cloud.messaging.MessageWindow;
import io.appgal.cloud.model.SourceNotification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.*;

public class SessionNetworkTreeTests {
    private static Logger logger = LoggerFactory.getLogger(SessionNetworkTreeTests.class);

    SessionNetwork sessionNetwork = new SessionNetwork();

    @BeforeEach
    public void setUp() throws Exception
    {
        TreeMap<String, FoodRunnerSession> foodRunnerSessions = sessionNetwork.getFoodRunnerSessions();

        for(int x=0; x<5; x++) {
            FoodRunnerSession foodRunnerSession = new FoodRunnerSession();
            String foodRunnerId = UUID.randomUUID().toString() + "/" + "foodRunnerId";
            String foodRunnerSessionId = UUID.randomUUID().toString() + "/" + "foodRunnerSessionId";
            foodRunnerSession.setFoodRunnerId(foodRunnerId);
            foodRunnerSession.setFoodRunnerSessionId(foodRunnerSessionId);

            for (int i = 0; i < 10; i++) {
                /*LocalDateTime startOfLocalDateInUtc = LocalDate.now(ZoneOffset.UTC).atStartOfDay();
                OffsetDateTime startTime = OffsetDateTime.of(startOfLocalDateInUtc, ZoneOffset.UTC);
                OffsetDateTime endTime = OffsetDateTime.now(ZoneOffset.UTC);*/
                OffsetDateTime startTime = OffsetDateTime.now(ZoneOffset.UTC);
                OffsetDateTime endTime = startTime.plusMinutes(Duration.ofMinutes(10).toMinutes());
                MessageWindow messageWindow = new MessageWindow(startTime, endTime);

                String sourceNotificationId = UUID.randomUUID().toString();
                SourceNotification sourceNotification = new SourceNotification();
                sourceNotification.setSourceNotificationId(sourceNotificationId);
                sourceNotification.setMessageWindow(messageWindow);

                JsonObject notification = new JsonObject();
                notification.addProperty("sourceNotificationId", sourceNotificationId);
                messageWindow.addMessage(notification);

                foodRunnerSession.receiveNotifications(messageWindow);

                foodRunnerSessions.put(foodRunnerSession.getFoodRunnerSessionId(), foodRunnerSession);
            }

            Thread.sleep(1000);
        }

        //Time to assert
        Iterator<FoodRunnerSession> iterator = foodRunnerSessions.values().iterator();
        while(iterator.hasNext()) {
            FoodRunnerSession foodRunnerSession = iterator.next();
            Map<String, List<SourceNotification>> sourceNotifications = foodRunnerSession.getSourceNotifications();
            Set<Map.Entry<String, List<SourceNotification>>> entrySet = sourceNotifications.entrySet();
            for (Map.Entry<String, List<SourceNotification>> entry : entrySet) {
                List<SourceNotification> notifications = entry.getValue();
                logger.info(notifications.toString());
                logger.info(notifications.iterator().next().getMessageWindow().toString());
            }
        }
    }

    @AfterEach
    public void tearDown()
    {
        this.sessionNetwork = null;
    }

    @Test
    public void testFindSessionsAroundSource() throws Exception
    {

    }
}
