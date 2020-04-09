package io.appgal.cloud.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.foodRunnerSync.protocol.ProcessIncomingPackets;
import io.appgal.cloud.messaging.MessageWindow;
import io.appgal.cloud.model.OutstandingFoodRunnerNotification;
import io.appgal.cloud.model.SourceNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Path("notification")
public class NotificationReceiver {
    private static Logger logger = LoggerFactory.getLogger(NotificationReceiver.class);

    @Inject
    private ProcessIncomingPackets processIncomingPackets;

    @Path("receive")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String receiveSourceNotification(@QueryParam("startTimestamp") String startTimestamp,@QueryParam("endTimestamp") String endTimestamp)
    {
        OffsetDateTime start = OffsetDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(startTimestamp)), ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(endTimestamp)), ZoneOffset.UTC);
        MessageWindow messageWindow = new MessageWindow(start, end);

        this.processIncomingPackets.processSourceNotification(messageWindow);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("statusCode", "0");
        return jsonObject.toString();
    }

    @Path("readDestinationNotifications")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String readDestinationNotifications(@QueryParam("startTimestamp") String startTimestamp,@QueryParam("endTimestamp") String endTimestamp)
    {
        OffsetDateTime start = OffsetDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(startTimestamp)), ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(endTimestamp)), ZoneOffset.UTC);
        MessageWindow messageWindow = new MessageWindow(start, end);
        JsonArray destinationNotifications = this.processIncomingPackets.readDestinationNotifications(messageWindow);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("statusCode", "0");
        jsonObject.add("destinationNotifications", destinationNotifications);
        return jsonObject.toString();
    }

    @Path("receiveNotificationForPickup/{sourceNotificationId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String receiveNotificationForPickup(@PathParam("sourceNotificationId") String sourceNotificationId)
    {
        SourceNotification sourceNotification = new SourceNotification();
        sourceNotification.setSourceNotificationId(sourceNotificationId);
        JsonArray response = this.processIncomingPackets.processNotificationForPickup(sourceNotification);
        return response.toString();
    }

    @Path("getOutstandingFoodRunnerNotification")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getOutstandingFoodRunnerNotification()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("statusCode", "0");

        OutstandingFoodRunnerNotification outstandingFoodRunnerNotification = new OutstandingFoodRunnerNotification();
        outstandingFoodRunnerNotification.setFoodRunnerId(UUID.randomUUID().toString());
        jsonObject.addProperty("foodRunnerId", outstandingFoodRunnerNotification.getFoodRunnerId());
        return jsonObject.toString();
    }
}