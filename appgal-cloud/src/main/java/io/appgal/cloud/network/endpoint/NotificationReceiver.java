package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonArray;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
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
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPickupDetails(){
        JsonArray timeOptions = new JsonArray();


        JsonArray today = new JsonArray();
        today.add("0");
        today.add("--Today--");
        timeOptions.add(today);
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        int currentHour = now.getHour();
        for(int i=currentHour;i<24;i++)
        {
            JsonArray cour = new JsonArray();
            cour.add("0:"+i);
            cour.add(i);
            timeOptions.add(cour);
        }

        JsonArray tomorrow = new JsonArray();
        tomorrow.add("1");
        tomorrow.add("--Tomorrow--");
        timeOptions.add(tomorrow);
        for(int i=0; i<24; i++){
            JsonArray cour = new JsonArray();
            cour.add("1:"+i);
            cour.add(i);
            timeOptions.add(cour);
        }

        return Response.ok(timeOptions.toString()).build();
    }


    @Path("/addPickupDetails")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPickupDetails(@RequestBody String payload)
    {
        try {
            JsonObject validationError = new JsonObject();
            JsonObject json = JsonParser.parseString(payload).getAsJsonObject();

            if(!json.has("foodType") || json.get("foodType").getAsString().trim().length()==0
            || json.get("foodType").getAsString().trim().equals("0"))
            {
                validationError.addProperty("foodTypeIsRequired",true);
            }
            if(!json.has("time") || json.get("time").getAsString().trim().length()==0
            || json.get("time").getAsString().trim().equals("0")
                    || json.get("time").getAsString().trim().equals("1")
            )
            {
                validationError.addProperty("timeIsRequired",true);
            }
            if(!validationError.keySet().isEmpty())
            {
                return Response.status(400).entity(validationError.toString()).build();
            }

            String orgId = json.get("orgId").getAsString();

            String pic = null;
            //next iteration
            /*if(json.has("foodPic") && !json.get("foodPic").isJsonNull())
            {
                pic = json.get("foodPic").getAsString();
            }*/
            SourceOrg sourceOrg = this.mongoDBJsonStore.getSourceOrg(orgId);
            FoodDetails foodDetails = FoodDetails.parse(payload);
            SchedulePickUpNotification notification = new SchedulePickUpNotification(UUID.randomUUID().toString());
            notification.setFoodDetails(foodDetails);
            notification.setSourceOrg(sourceOrg);

            //read the time property and add the correct starttime
            OffsetDateTime start = null;
            String scheduledPickupTime = null;
            String time = json.get("time").getAsString();
            String[] timeSplit = time.split(":");
            ZoneId id = ZoneId.of(sourceOrg.getAddress().getTimeZone());
            LocalDateTime localDateTime = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDateTime.atZone(id);
            //ZoneOffset zoneOffset = ZoneOffset.UTC;
            ZoneOffset zoneOffset = zonedDateTime.getOffset();
            if(timeSplit[0].equals("0"))
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(timeSplit[1]));
                calendar.set(Calendar.MINUTE,0);
                LocalDateTime localDate = LocalDateTime.ofInstant(calendar.toInstant(),zoneOffset);
                start = OffsetDateTime.of(localDate,zoneOffset);

                //Calculate start time of the day
                String pattern = "hh:mm a";
                DateFormat dateFormat = new SimpleDateFormat(pattern);
                scheduledPickupTime = dateFormat.format(calendar.getTime());
            }
            else
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(timeSplit[1]));
                calendar.set(Calendar.MINUTE,0);
                LocalDateTime localDate = LocalDateTime.ofInstant(calendar.toInstant(),zoneOffset);
                start = OffsetDateTime.ofInstant(localDate.plus(2, ChronoUnit.DAYS).toInstant(zoneOffset),zoneOffset);

                //Calculate start time of the day
                String pattern = "hh:mm a";
                DateFormat dateFormat = new SimpleDateFormat(pattern);
                scheduledPickupTime = dateFormat.format(calendar.getTime());
            }

            notification.setStart(start);
            notification.setScheduledStartTime(scheduledPickupTime);
            notification.isToday();

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
            String dropOffOrgId = null;
            if(json.has("dropOffOrgId"))
            {
                dropOffOrgId = json.get("dropOffOrgId").getAsString();
            }
            boolean enableOfflineCommunitySupport = false;
            if(json.has("enableOfflineCommunitySupport"))
            {
                enableOfflineCommunitySupport = json.get("enableOfflineCommunitySupport").getAsBoolean();
            }

            System.out.println("******DROPOFF_ORG_ID: "+dropOffOrgId+"********0");
            SchedulePickUpNotification notification = SchedulePickUpNotification.parse(this.mongoDBJsonStore.
                    getScheduledPickUpNotification(pickupNotificationId).toString());

            if(dropOffOrgId != null) {
                SourceOrg dropOffOrg = this.mongoDBJsonStore.getSourceOrg(dropOffOrgId);
                notification.setDropOffOrg(dropOffOrg);
                //JsonUtil.print(this.getClass(), notification.toJson());
            }
            notification.setDropOffDynamic(enableOfflineCommunitySupport);

            System.out.println("******NOTIFICATION: "+notification+"********0");
            this.networkOrchestrator.schedulePickUp(notification);


            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", true);

            System.out.println("*******RESPONSE: "+responseJson+"*******0");

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