package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.model.Location;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.network.services.LocationService;
import io.appgal.cloud.network.services.NetworkOrchestrator;
import io.appgal.cloud.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("location")
public class LocationAPI {
    private static Logger logger = LoggerFactory.getLogger(LocationAPI.class);

    @Inject
    private LocationService locationService;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    private ActiveNetwork activeNetwork;

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    //TODO
    @Path("update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response receiveUpdate(@RequestBody String jsonBody)
    {
        try {
            JsonObject inputJson = JsonParser.parseString(jsonBody).getAsJsonObject();
            //System.out.println(inputJson);
            String email = inputJson.get("email").getAsString();
            double latitude = inputJson.get("latitude").getAsDouble();
            double longitude = inputJson.get("longitude").getAsDouble();
            Location location = new Location(latitude, longitude);
            FoodRunner foodRunner = this.activeNetwork.findFoodRunnerByEmail(email);
            if(foodRunner != null) {
                foodRunner.setLocation(location);
            }
            else
            {
                foodRunner = new FoodRunner();
                Profile profile = this.mongoDBJsonStore.getProfile(email);
                foodRunner.setProfile(profile);
                foodRunner.setLocation(location);
                this.networkOrchestrator.enterNetwork(foodRunner);
                foodRunner = this.activeNetwork.findFoodRunnerByEmail(email);
            }
            this.locationService.receiveUpdate(foodRunner);

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

    @Path("current")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentLocation(@QueryParam("email") String email)
    {
        try {
            Location location = this.locationService.getCurrentLocation(email);

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("active", location == null ? false : true);
            if (location != null) {
                responseJson.add("location", location.toJson());
            }
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
