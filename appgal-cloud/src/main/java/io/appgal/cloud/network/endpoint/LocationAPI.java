package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.network.services.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
        FoodRunner foodRunner = FoodRunner.parse(jsonBody);
        this.locationService.receiveUpdate(foodRunner);

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success",true);
        return Response.ok(responseJson.toString()).build();
    }
}
