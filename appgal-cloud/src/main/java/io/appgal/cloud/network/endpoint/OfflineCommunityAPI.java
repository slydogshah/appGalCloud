package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.model.Location;
import io.appgal.cloud.network.services.DynamicDropOffOrchestrator;
import io.appgal.cloud.network.services.LocationService;
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

    @Path("community")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response orchestrateOfflineCommunity()
    {
        JsonObject offlineCommunity = this.dynamicDropOffOrchestrator.orchestrateOfflineCommunity();
        return Response.ok(offlineCommunity.toString()).build();
    }
}
