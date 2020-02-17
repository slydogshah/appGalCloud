package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import io.appgal.cloud.model.SourceNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SourceNotificationEmitter {
    private static Logger logger = LoggerFactory.getLogger(SourceNotificationEmitter.class);

    @Inject
    private KafkaDaemon kafkaDaemon;

    public void emit(SourceNotification sourceNotification)
    {
        MessageWindow messageWindow = sourceNotification.getMessageWindow();

        //find the SourceNotifications with this time window
        JsonArray jsonArray = this.kafkaDaemon.readNotifications(SourceNotification.TOPIC, messageWindow);

        //Broadcast each SourceNotification to the FoodRunner Network
    }
}
