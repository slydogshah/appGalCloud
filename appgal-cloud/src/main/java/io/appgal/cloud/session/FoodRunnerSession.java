package io.appgal.cloud.session;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.appgal.cloud.messaging.KafkaDaemon;
import io.appgal.cloud.messaging.MessageWindow;
import io.appgal.cloud.model.SourceNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@ApplicationScoped
public class FoodRunnerSession {
    private static Logger logger = LoggerFactory.getLogger(FoodRunnerSession.class);

    private String foodRunnerId;
    private String foodRunnerSessionId;
    private Map<String, List<SourceNotification>> sourceNotifications; //foodRunnerSessionId -> SourceNotications Map

    public FoodRunnerSession()
    {
        this.sourceNotifications = new HashMap<>();
    }

    public void receiveNotifications(MessageWindow messageWindow)
    {
        List<SourceNotification> sourceNotifications = new ArrayList<>();
        JsonArray jsonArray = messageWindow.getMessages();

        if(jsonArray != null) {
            Iterator<JsonElement> iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                JsonObject jsonObject = (JsonObject) iterator.next();
                sourceNotifications.add(SourceNotification.fromJson(jsonObject));
            }

            String foodRunnerSessionId = UUID.randomUUID().toString();
            this.sourceNotifications.put(foodRunnerSessionId, sourceNotifications);
        }
    }
}
