package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.model.Location;
import io.appgal.cloud.network.services.DynamicDropOffOrchestrator;
import io.appgal.cloud.network.services.LocationService;
import io.appgal.cloud.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("offline")
public class OfflineCommunityAPI {
    private static Logger logger = LoggerFactory.getLogger(OfflineCommunityAPI.class);

    @Inject
    private DynamicDropOffOrchestrator dynamicDropOffOrchestrator;

    @Path("notification")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response notifyAvailability(@RequestBody String jsonBody)
    {
        try {
            JsonObject notification = JsonParser.parseString(jsonBody).getAsJsonObject();

            this.dynamicDropOffOrchestrator.notifyAvailability(notification.get("foodRunnerEmail").getAsString(),
                    notification.get("available").getAsBoolean());

            JsonObject response = new JsonObject();
            response.addProperty("success", true);
            return Response.ok(response.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("community")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response orchestrateOfflineCommunity()
    {
        try {
            JsonObject offlineCommunity = this.dynamicDropOffOrchestrator.orchestrateOfflineCommunity();
            return Response.ok(offlineCommunity.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("pipeline")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOfflineDropOffPipeline()
    {
        try {
            JsonArray pipeline = this.dynamicDropOffOrchestrator.getOfflineDropOffPipeline();
            return Response.ok(pipeline.toString()).build();
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
