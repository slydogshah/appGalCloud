package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.ScheduleDropOffNotification;
import io.appgal.cloud.model.SchedulePickUpNotification;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.network.services.FoodRecoveryOrchestrator;
import io.appgal.cloud.network.services.NetworkOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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
        List<SchedulePickUpNotification> schedulePickUpNotificationList = this.mongoDBJsonStore.
                getSchedulePickUpNotifications(email);
        return Response.ok(schedulePickUpNotificationList.toString()).build();
    }

    @Path("/dropOffOrgs")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response dropOffOrgs(@QueryParam("orgId") String orgId)
    {
        List<SourceOrg> dropOffOrgs = this.foodRecoveryOrchestrator.findDropOffOrganizations(orgId);

        JsonObject responseJson = new JsonObject();
        responseJson.add("dropOffOrgs", JsonParser.parseString(dropOffOrgs.toString()));
        return Response.ok(responseJson.toString()).build();
    }

    @Path("/schedulePickup")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response schedulePickUp(@RequestBody String jsonBody)
    {
        SchedulePickUpNotification notification = SchedulePickUpNotification.parse(jsonBody);
        this.networkOrchestrator.schedulePickUp(notification);

        //JsonObject responseJson = new JsonObject();
        //responseJson.addProperty("operationSuccessful",true);
        //return Response.ok(responseJson.toString()).build();
        return Response.ok().build();
    }

    @Path("/dropOff/notifications")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDropOffNotifications(@QueryParam("orgId") String orgId)
    {
        List<ScheduleDropOffNotification> scheduleDropOffNotificationList = this.mongoDBJsonStore.getScheduledDropOffNotifications(orgId);
        return Response.ok(scheduleDropOffNotificationList.toString()).build();
    }

    @Path("/scheduleDropOff")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response scheduleDropOff(@RequestBody String jsonBody)
    {
        ScheduleDropOffNotification notification = ScheduleDropOffNotification.parse(jsonBody);
        this.networkOrchestrator.scheduleDropOff(notification);

        return Response.ok().build();
    }
}