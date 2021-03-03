package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonObject;
import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.model.Location;
import io.appgal.cloud.network.services.LocationService;
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

    @Path("update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response receiveUpdate(@RequestBody String jsonBody)
    {
        try {
            FoodRunner foodRunner = FoodRunner.parse(jsonBody);
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
