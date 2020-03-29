package io.appgal.cloud.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.foodRunnerSync.protocol.ProcessIncomingPackets;
import io.appgal.cloud.messaging.MessageWindow;
import io.appgal.cloud.model.OutstandingFoodRunnerNotification;
import io.appgal.cloud.model.SourceNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Path("registration")
public class Registration {
    private static Logger logger = LoggerFactory.getLogger(Registration.class);

    @Path("profile")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getProfile()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", "CLOUD_ID");
        jsonObject.addProperty("email", "blah@blah.com");
        jsonObject.addProperty("mobile", "8675309");
        jsonObject.addProperty("photo", "photu");

        return jsonObject.toString();
    }
}