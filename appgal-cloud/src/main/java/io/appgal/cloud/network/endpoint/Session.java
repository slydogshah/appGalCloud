package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonObject;
import io.appgal.cloud.network.services.FoodRunnerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("session")
public class Session {
    private static Logger logger = LoggerFactory.getLogger(Session.class);

    @Inject
    private FoodRunnerSession foodRunnerSession;

    @Path("list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listSessions()
    {
        //TODO: Re-Implement this @bugs.bunny.shah@gmail.com
        //this.foodRunnerSession.start();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("statusCode", "0");
        return jsonObject.toString();
    }
}