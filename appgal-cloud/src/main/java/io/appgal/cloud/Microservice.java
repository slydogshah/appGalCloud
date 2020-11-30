package io.appgal.cloud;

import com.google.gson.JsonObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

import com.google.gson.JsonParser;

import io.bugsbunny.data.history.service.DataReplayService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Random;

import io.appgal.cloud.model.*;
import io.appgal.cloud.model.MessageWindow;

@Path("/microservice")
public class Microservice {
    private static Logger logger = LoggerFactory.getLogger(Microservice.class);

    @Inject
    private DataReplayService dataReplayService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello()
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("oid", UUID.randomUUID().toString());
        jsonObject.addProperty("message", "HELLO_TO_HUMANITY");

        return jsonObject.toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("test")
    public String test()
    {
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        MessageWindow messageWindow = new MessageWindow();
        messageWindow.setStart(start);
        messageWindow.setEnd(end);
        Random random = new Random();
        for(int i=0; i<10; i++) {
            SourceOrg sourceOrg1 = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");

            String sourceNotificationId = UUID.randomUUID().toString();
            SourceNotification sourceNotification = new SourceNotification();
            sourceNotification.setSourceNotificationId(sourceNotificationId);
            sourceNotification.setMessageWindow(messageWindow);
            sourceNotification.setSourceOrg(sourceOrg1);

            String destinationNotificationId = UUID.randomUUID().toString();
            DestinationNotification destinationNotification = new DestinationNotification();
            destinationNotification.setDestinationNotificationId(destinationNotificationId);
            destinationNotification.setSourceNotification(sourceNotification);
            SourceOrg destinationOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
            Location location = new Location(30.25860595703125d, -97.74873352050781d);
            Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com",
                    "8675309", "", "", ProfileType.FOOD_RUNNER, location);
            FoodRunner foodRunner = new FoodRunner(profile, location);
            DropOffNotification dropOffNotification = new DropOffNotification(destinationOrg, location, foodRunner);
            destinationNotification.setDropOffNotification(dropOffNotification);

            JsonObject jsonObject = JsonParser.parseString(destinationNotification.toString()).getAsJsonObject();

            JsonObject modelChain = new JsonObject();
            modelChain.addProperty("modelId", random.nextLong());
            modelChain.add("payload", jsonObject);
            String oid = this.dataReplayService.generateDiffChain(modelChain);
            logger.info("ChainId: " + oid);
        }
        return "blah";
    }
}