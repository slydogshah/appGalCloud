package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonObject;
import io.appgal.cloud.network.services.FoodRunnerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("session")
public class Session {
    private static Logger logger = LoggerFactory.getLogger(Session.class);

    @Inject
    private FoodRunnerSession foodRunnerSession;

    @Path("list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listSessions()
    {
        //TODO: Re-Implement this @bugs.bunny.shah@gmail.com
        //this.foodRunnerSession.start();

        return Response.ok().build();
    }
}