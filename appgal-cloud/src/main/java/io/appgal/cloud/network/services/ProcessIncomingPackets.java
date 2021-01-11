package io.appgal.cloud.network.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.model.MessageWindow;
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

    public JsonArray readDestinationNotifications(MessageWindow messageWindow)
    {
        //JsonArray destinationNotifications = this.kafkaDaemon.readNotifications(DestinationNotification.TOPIC, messageWindow);

        //return destinationNotifications;

        return null;
    }
}
