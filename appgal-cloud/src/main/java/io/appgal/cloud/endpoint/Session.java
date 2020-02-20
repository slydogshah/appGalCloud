package io.appgal.cloud.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.foodRunnerSync.protocol.ProcessIncomingPackets;
import io.appgal.cloud.messaging.MessageWindow;
import io.appgal.cloud.session.FoodRunnerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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
        this.foodRunnerSession.start();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("statusCode", "0");
        return jsonObject.toString();
    }
}