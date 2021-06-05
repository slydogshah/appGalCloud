package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.*;
import io.appgal.cloud.network.services.DynamicDropOffOrchestrator;
import io.appgal.cloud.network.services.FoodRecoveryOrchestrator;
import io.appgal.cloud.network.services.NetworkOrchestrator;
import io.appgal.cloud.util.JsonUtil;
import org.bson.internal.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Path("notification")
public class NotificationReceiver {
    private static Logger logger = LoggerFactory.getLogger(NotificationReceiver.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    private FoodRecoveryOrchestrator foodRecoveryOrchestrator;

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Inject
    private DynamicDropOffOrchestrator dynamicDropOffOrchestrator;


    @Path("/pickup/notifications")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPickUpNotifications(@QueryParam("email") String email)
    {
        try {
            List<SchedulePickUpNotification> schedulePickUpNotificationList = this.mongoDBJsonStore.
                    getSchedulePickUpNotifications(email);
            return Response.ok(schedulePickUpNotificationList.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("/dropOffOrgs")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response dropOffOrgs(@QueryParam("orgId") String orgId)
    {
        try {
            List<SourceOrg> dropOffOrgs = this.foodRecoveryOrchestrator.findDropOffOrganizations(orgId);

            JsonObject responseJson = new JsonObject();
            responseJson.add("dropOffOrgs", JsonParser.parseString(dropOffOrgs.toString()));
            return Response.ok(responseJson.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("/addPickupDetails")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPickupDetails(@RequestBody String payload)
    {
        try {
            JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
            String orgId = json.get("orgId").getAsString();

            String pic = null;
            if(json.has("foodPic") && !json.get("foodPic").isJsonNull())
            {
                pic = json.get("foodPic").getAsString();
            }
            SourceOrg sourceOrg = this.mongoDBJsonStore.getSourceOrg(orgId);
            FoodDetails foodDetails = FoodDetails.parse(payload);
            SchedulePickUpNotification notification = new SchedulePickUpNotification(UUID.randomUUID().toString());
            notification.setFoodDetails(foodDetails);
            notification.setSourceOrg(sourceOrg);

            //read the time property and add the correct starttime
            OffsetDateTime start = null;
            String time = json.get("time").getAsString();
            String[] timeSplit = time.split(":");
            if(timeSplit[0].equals("0"))
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(timeSplit[1]));
                LocalDateTime localDate = LocalDateTime.ofInstant(calendar.toInstant(),ZoneOffset.UTC);
                start = OffsetDateTime.of(localDate,ZoneOffset.UTC);
            }
            else
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(timeSplit[1]));
                LocalDateTime localDate = LocalDateTime.ofInstant(calendar.toInstant(),ZoneOffset.UTC);
                start = OffsetDateTime.ofInstant(localDate.plus(1, ChronoUnit.DAYS).toInstant(ZoneOffset.UTC),ZoneOffset.UTC);
            }

            logger.info("START: "+start.toEpochSecond());
            notification.setStart(start);

            this.networkOrchestrator.startPickUpProcess(pic,notification);

            List<SourceOrg> dropOffOrgs = this.foodRecoveryOrchestrator.findDropOffOrganizations(orgId);
            List<FoodRunner> foodRunners = this.dynamicDropOffOrchestrator.getOfflineDropOffHelpers(notification.getSourceOrg());

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("pickupNotificationId",notification.getId());
            responseJson.add("dropOffOrgs", JsonParser.parseString(dropOffOrgs.toString()));
            responseJson.add("offlineCommunityHelpers",JsonParser.parseString(foodRunners.toString()));
            return Response.ok(responseJson.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("/schedulePickup")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response schedulePickUp(@RequestBody String payload)
    {
        try {
            JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
            String pickupNotificationId = json.get("pickupNotificationId").getAsString();
            String dropOffOrgId = json.get("dropOffOrgId").getAsString();

            SchedulePickUpNotification notification = SchedulePickUpNotification.parse(this.mongoDBJsonStore.
                    getScheduledPickUpNotification(pickupNotificationId).toString());
            SourceOrg dropOffOrg = this.mongoDBJsonStore.getSourceOrg(dropOffOrgId);
            notification.setDropOffOrg(dropOffOrg);
            JsonUtil.print(this.getClass(), notification.toJson());

            this.networkOrchestrator.schedulePickUp(notification);


            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", true);
            return Response.ok(responseJson.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }
}