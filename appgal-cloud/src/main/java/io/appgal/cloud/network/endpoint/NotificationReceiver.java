package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.SchedulePickUpNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Path("notification")
public class NotificationReceiver {
    private static Logger logger = LoggerFactory.getLogger(NotificationReceiver.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;


    @Path("/pickup/notifications")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPickUpNotifications(@QueryParam("email") String email)
    {
        List<SchedulePickUpNotification> schedulePickUpNotificationList = this.mongoDBJsonStore.
                getSchedulePickUpNotifications(email);
        return Response.ok(schedulePickUpNotificationList.toString()).build();
    }
}