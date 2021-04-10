package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.FoodDetails;
import io.appgal.cloud.model.ScheduleDropOffNotification;
import io.appgal.cloud.model.SchedulePickUpNotification;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.network.services.FoodRecoveryOrchestrator;
import io.appgal.cloud.network.services.NetworkOrchestrator;
import io.appgal.cloud.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("notification")
public class NotificationReceiver {
    private static Logger logger = LoggerFactory.getLogger(NotificationReceiver.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Inject
    private FoodRecoveryOrchestrator foodRecoveryOrchestrator;


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

            SourceOrg sourceOrg = this.mongoDBJsonStore.getSourceOrg(orgId);
            FoodDetails foodDetails = FoodDetails.parse(payload);
            SchedulePickUpNotification notification = new SchedulePickUpNotification(UUID.randomUUID().toString());
            notification.setFoodDetails(foodDetails);
            notification.setSourceOrg(sourceOrg);
            this.mongoDBJsonStore.storeScheduledPickUpNotification(notification);

            List<SourceOrg> dropOffOrgs = this.foodRecoveryOrchestrator.findDropOffOrganizations(orgId);

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("pickupNotificationId",notification.getId());
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

    @Path("/schedulePickup")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response schedulePickUp(@RequestBody String jsonBody)
    {
        try {
            JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();
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

    @Path("/dropOff/notifications")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDropOffNotifications(@QueryParam("orgId") String orgId)
    {
        try {
            List<ScheduleDropOffNotification> scheduleDropOffNotificationList = this.mongoDBJsonStore.getScheduledDropOffNotifications(orgId);
            return Response.ok(scheduleDropOffNotificationList.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("/scheduleDropOff")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response scheduleDropOff(@RequestBody String jsonBody)
    {
        try {
            ScheduleDropOffNotification notification = ScheduleDropOffNotification.parse(jsonBody);
            this.networkOrchestrator.scheduleDropOff(notification);

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