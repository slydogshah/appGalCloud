package io.appgal.cloud.app.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
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

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", true);
        return Response.ok(jsonObject.toString()).build();
    }

    @Path("/findBestDestination")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String findBestDestination(@RequestBody String jsonBody)
    {
        Profile profile = this.mongoDBJsonStore.getProfile("bugs.bunny.shah@gmail.com");
        FoodRunner foodRunner = new FoodRunner(profile, new Location(Double.parseDouble("30.25860595703125d"), Double.parseDouble("-97.74873352050781d")));
        List<SourceOrg> sourceOrgs = this.networkOrchestrator.findBestDestination(foodRunner);
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

    @Path("sourceOrgs")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSourceOrgs()
    {
        List<SourceOrg> sourceOrgs = this.mongoDBJsonStore.getSourceOrgs();
        return sourceOrgs.toString();
    }
}
