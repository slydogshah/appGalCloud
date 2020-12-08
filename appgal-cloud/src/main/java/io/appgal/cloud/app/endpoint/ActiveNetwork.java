package io.appgal.cloud.app.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.network.services.DeliveryOrchestrator;
import io.appgal.cloud.network.services.NetworkOrchestrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("activeNetwork")
public class ActiveNetwork {
    private static Logger logger = LoggerFactory.getLogger(ActiveNetwork.class);

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Inject
    private DeliveryOrchestrator deliveryOrchestrator;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Path("activeView")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActiveView()
    {
        JsonObject activeView = this.networkOrchestrator.getActiveView();
        return Response.ok(activeView.toString()).build();
    }

    @Path("/enterNetwork")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response enterNetwork(@QueryParam("email") String email)
    {
        Profile profile = this.mongoDBJsonStore.getProfile(email);
        if(profile == null)
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", "BAD_REQUEST: \"+email+\" is not a registered user");
            jsonObject.addProperty("email", email);
            return Response.status(400).entity(jsonObject.toString()).build();
        }

        //TODO: PLEASE_REMOVE_THIS
        Location location = new Location(30.25860595703125d, -97.74873352050781d);

        FoodRunner foodRunner = new FoodRunner(profile, location);
        this.networkOrchestrator.enterNetwork(foodRunner);

        return Response.ok().build();
    }

    @Path("pickUpRequest/send")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String sendPickRequest(@RequestBody String jsonBody)
    {
        PickupRequest pickupRequest = PickupRequest.parse(jsonBody);
        String requestId = this.networkOrchestrator.sendPickUpRequest(pickupRequest);

        JsonObject pickRequestResult = this.networkOrchestrator.getPickRequestResult(requestId);

        return pickRequestResult.toString();
    }

    @Path("pickUpRequest/result")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPickRequestResult(@PathParam("") String requestId)
    {
        JsonObject pickRequestResult = this.networkOrchestrator.getPickRequestResult(requestId);
        return pickRequestResult.toString();
    }

    @Path("/findBestDestination")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String findBestDestination(@RequestBody String jsonBody)
    {
        Profile profile = this.mongoDBJsonStore.getProfile("bugs.bunny.shah@gmail.com");
        FoodRunner foodRunner = new FoodRunner(profile, new Location(Double.parseDouble("30.25860595703125d"), Double.parseDouble("-97.74873352050781d")));
        List<SourceOrg> sourceOrgs = this.deliveryOrchestrator.findBestDestination(foodRunner);
        return sourceOrgs.toString();
    }

    @Path("/sendDeliveryNotification")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendDeliveryNotification(@RequestBody String jsonBody)
    {
        //TODO
        //DropOffNotification dropOffNotification = DropOffNotification.parse(jsonBody);
        //this.deliveryOrchestrator.sendDeliveryNotification(dropOffNotification);

        return Response.ok().build();
    }

    @Path("/sendFoodRequest")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendFoodRequest(@RequestBody String jsonBody)
    {
        logger.info(jsonBody.toString());
        FoodRequest foodRequest = FoodRequest.parse(jsonBody);
        String requestId = this.deliveryOrchestrator.sendFoodRequest(foodRequest);

        JsonArray results = this.networkOrchestrator.getLatestResults(requestId);

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("foodRequestId", requestId);
        responseJson.add("results", results);
        return Response.ok(responseJson.toString()).build();
    }

    @Path("sourceOrgs")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSourceOrgs()
    {
        List<SourceOrg> sourceOrgs = this.networkOrchestrator.getSourceOrgs();
        return sourceOrgs.toString();
    }

    @Path("/schedulePickUp")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response schedulePickUp(@RequestBody String jsonBody)
    {
        SchedulePickUpNotification schedulePickUpNotification = SchedulePickUpNotification.parse(jsonBody);


        JsonObject responseJson = schedulePickUpNotification.toJson();
        return Response.ok(responseJson.toString()).build();
    }
}
