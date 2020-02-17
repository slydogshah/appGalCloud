package io.appgal.cloud.foodRunnerSync.protocol;

import com.google.gson.JsonArray;
import io.appgal.cloud.messaging.MessageWindow;
import io.appgal.cloud.messaging.SourceNotificationEmitter;
import io.appgal.cloud.messaging.SourceNotificationReceiver;
import io.appgal.cloud.model.SourceNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class ProcessIncomingPackets {
    private static Logger logger = LoggerFactory.getLogger(ProcessIncomingPackets.class);

    @Inject
    private SourceNotificationReceiver sourceNotificationReceiver;

    @Inject
    private SourceNotificationEmitter sourceNotificationEmitter;

    public void processSourceNotification(MessageWindow messageWindow)
    {
        logger.info("....");
        logger.info("PROCESS_SOURCE_NOTIFICATION");
        logger.info("....");

        SourceNotification sourceNotification = new SourceNotification();
        sourceNotification.setSourceNotificationId(UUID.randomUUID().toString());
        sourceNotification.setMessageWindow(messageWindow);

        this.sourceNotificationReceiver.receive(sourceNotification);

        //Emit this instance of SourceNotification to the FoodRunner Network
        this.sourceNotificationEmitter.emit(sourceNotification);
    }

    public JsonArray readDestinationNotifications()
    {
        logger.info("....");
        logger.info("READ_DESTINATION_NOTIFICATIONS");
        logger.info("....");

        JsonArray destinationNotifications = new JsonArray();

        return destinationNotifications;
    }
}