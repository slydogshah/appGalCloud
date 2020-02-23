package io.appgal.cloud.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.messaging.MessageWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Iterator;

public class SourceNotification {
    private static Logger logger = LoggerFactory.getLogger(SourceNotification.class);

    public static final String TOPIC = "foodRunnerSyncProtocol_source_notification";

    private String sourceNotificationId;
    private String latitude;
    private String longitude;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString()
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("sourceNotificationId", this.sourceNotificationId);
        jsonObject.addProperty("startTimestamp", messageWindow.getStart().toEpochSecond());
        jsonObject.addProperty("endTimestamp", messageWindow.getEnd().toEpochSecond());
        jsonObject.addProperty("latitude", this.latitude);
        jsonObject.addProperty("longitude", this.longitude);

        return jsonObject.toString();
    }

    public static SourceNotification fromJson(JsonObject jsonObject)
    {
        SourceNotification sourceNotification = new SourceNotification();

        sourceNotification.sourceNotificationId = jsonObject.get("sourceNotificationId").getAsString();
        sourceNotification.latitude = jsonObject.get("latitude").getAsString();
        sourceNotification.longitude = jsonObject.get("longitude").getAsString();

        Object messageWindowJson = jsonObject.get("messageWindow");
        if(messageWindowJson != null) {
            long start = jsonObject.get("start").getAsLong();
            long end = jsonObject.get("end").getAsLong();
            JsonArray messages = jsonObject.get("messages").getAsJsonArray();

            MessageWindow messageWindow = new MessageWindow(OffsetDateTime.parse("" + start), OffsetDateTime.parse("" + end));
            Iterator<JsonElement> iterator = messages.iterator();
            while (iterator.hasNext()) {
                messageWindow.addMessage((JsonObject) iterator.next());
            }
            sourceNotification.messageWindow = messageWindow;
        }

        return sourceNotification;
    }
}
