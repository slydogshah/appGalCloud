package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import io.appgal.cloud.messaging.MessageWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceNotification {
    private static Logger logger = LoggerFactory.getLogger(SourceNotification.class);

    private String sourceNotificationId;
    private MessageWindow messageWindow;

    public String getSourceNotificationId() {
        return sourceNotificationId;
    }

    public void setSourceNotificationId(String sourceNotificationId) {
        this.sourceNotificationId = sourceNotificationId;
    }

    public MessageWindow getMessageWindow() {
        return messageWindow;
    }

    public void setMessageWindow(MessageWindow messageWindow) {
        this.messageWindow = messageWindow;
    }

    @Override
    public String toString()
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("sourceNotificationId", this.sourceNotificationId);
        jsonObject.addProperty("startTimestamp", messageWindow.getStart().toEpochSecond());
        jsonObject.addProperty("endTimestamp", messageWindow.getEnd().toEpochSecond());

        return jsonObject.toString();
    }
}
