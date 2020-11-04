package io.appgal.cloud.network.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.appgal.cloud.infrastructure.messaging.MessageWindow;
import io.appgal.cloud.model.SourceNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class FoodRunnerSession {
    private static Logger logger = LoggerFactory.getLogger(FoodRunnerSession.class);

    //private String foodRunnerId;
    //private String foodRunnerSessionId;
    private Map<String, List<SourceNotification>> sourceNotifications; //foodRunnerSessionId -> SourceNotications Map

    public FoodRunnerSession()
    {
        this.sourceNotifications = new HashMap<>();
    }

    public Map<String, List<SourceNotification>> getSourceNotifications() {
        return sourceNotifications;
    }

    /*public String getFoodRunnerId() {
        return foodRunnerId;
    }

    public void setFoodRunnerId(String foodRunnerId) {
        this.foodRunnerId = foodRunnerId;
    }

    public String getFoodRunnerSessionId() {
        return foodRunnerSessionId;
    }

    public void setFoodRunnerSessionId(String foodRunnerSessionId) {
        this.foodRunnerSessionId = foodRunnerSessionId;
    }*/

    public void receiveNotifications(MessageWindow messageWindow)
    {
        List<SourceNotification> sourceNotifications = new ArrayList<>();
        JsonArray jsonArray = messageWindow.getMessages();

        if(jsonArray != null) {
            Iterator<JsonElement> iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                JsonObject jsonObject = (JsonObject) iterator.next();
                SourceNotification sourceNotification = SourceNotification.parse(jsonObject.toString());
                sourceNotification.setMessageWindow(messageWindow);
                sourceNotifications.add(sourceNotification);
            }

            String foodRunnerSessionId = UUID.randomUUID().toString();
            this.sourceNotifications.put(foodRunnerSessionId, sourceNotifications);
        }
    }
}
