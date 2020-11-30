package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.network.services.ProcessIncomingPackets;
import io.appgal.cloud.model.MessageWindow;
import io.appgal.cloud.model.OutstandingFoodRunnerNotification;
import io.appgal.cloud.model.SourceNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    public Response receiveSourceNotification(@QueryParam("startTimestamp") String startTimestamp, @QueryParam("endTimestamp") String endTimestamp)
    {
        OffsetDateTime start = OffsetDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(startTimestamp)), ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(endTimestamp)), ZoneOffset.UTC);
        MessageWindow messageWindow = new MessageWindow();
        messageWindow.setStart(start);
        messageWindow.setEnd(end);

        this.processIncomingPackets.processSourceNotification(messageWindow);

        return Response.ok().build();
    }

    @Path("readDestinationNotifications")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response readDestinationNotifications(@QueryParam("startTimestamp") String startTimestamp,@QueryParam("endTimestamp") String endTimestamp)
    {
        OffsetDateTime start = OffsetDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(startTimestamp)), ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(endTimestamp)), ZoneOffset.UTC);
        MessageWindow messageWindow = new MessageWindow();
        messageWindow.setStart(start);
        messageWindow.setEnd(end);
        JsonArray destinationNotifications = this.processIncomingPackets.readDestinationNotifications(messageWindow);

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("destinationNotifications", destinationNotifications);
        return Response.ok(jsonObject.toString()).build();
    }

    @Path("receiveNotificationForPickup/{sourceNotificationId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response receiveNotificationForPickup(@PathParam("sourceNotificationId") String sourceNotificationId)
    {
        SourceNotification sourceNotification = new SourceNotification();
        sourceNotification.setSourceNotificationId(sourceNotificationId);
        JsonArray response = this.processIncomingPackets.processNotificationForPickup(sourceNotification);
        return Response.ok(response.toString()).build();
    }

    @Path("getOutstandingFoodRunnerNotification")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOutstandingFoodRunnerNotification()
    {
        JsonObject jsonObject = new JsonObject();

        OutstandingFoodRunnerNotification outstandingFoodRunnerNotification = new OutstandingFoodRunnerNotification();
        outstandingFoodRunnerNotification.setFoodRunnerId(UUID.randomUUID().toString());
        jsonObject.addProperty("foodRunnerId", outstandingFoodRunnerNotification.getFoodRunnerId());
        return Response.ok(jsonObject.toString()).build();
    }
}