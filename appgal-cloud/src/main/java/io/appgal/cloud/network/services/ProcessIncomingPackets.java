package io.appgal.cloud.network.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.model.MessageWindow;
import io.appgal.cloud.model.SourceNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@ApplicationScoped
public class ProcessIncomingPackets {
    private static Logger logger = LoggerFactory.getLogger(ProcessIncomingPackets.class);

    public void processSourceNotification(MessageWindow messageWindow)
    {
        SourceNotification sourceNotification = new SourceNotification();
        sourceNotification.setSourceNotificationId(UUID.randomUUID().toString());
        messageWindow.setTopic(SourceNotification.TOPIC);
        sourceNotification.setMessageWindow(messageWindow);

        //this.sourceNotificationReceiver.receive(sourceNotification);

        //Emit this instance of SourceNotification to the FoodRunner Network
        //this.sourceNotificationEmitter.emit(sourceNotification);
    }

    public JsonArray readDestinationNotifications(MessageWindow messageWindow)
    {
        //JsonArray destinationNotifications = this.kafkaDaemon.readNotifications(DestinationNotification.TOPIC, messageWindow);

        //return destinationNotifications;

        return null;
    }

    public JsonArray processNotificationForPickup(SourceNotification sourceNotification)
    {
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        MessageWindow messageWindow = new MessageWindow();
        messageWindow.setStart(start);
        messageWindow.setEnd(end);
        sourceNotification.setMessageWindow(messageWindow);
        JsonObject json = sourceNotification.toJson();
        //this.kafkaDaemon.produceData(SourceNotification.TOPIC, json);

        JsonArray response = new JsonArray();
        response.add(json);
        return response;
    }
}
