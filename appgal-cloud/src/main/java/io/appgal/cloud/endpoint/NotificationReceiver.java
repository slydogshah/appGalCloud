package io.appgal.cloud.endpoint;

import com.google.gson.JsonObject;
import io.appgal.cloud.foodRunnerSync.protocol.ProcessIncomingPackets;
import io.appgal.cloud.messaging.MessageWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.OffsetDateTime;
import java.util.UUID;

@Path("/receive")
public class NotificationReceiver {
    private static Logger logger = LoggerFactory.getLogger(NotificationReceiver.class);

    @Inject
    private ProcessIncomingPackets processIncomingPackets;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String receiveSourceNotification(String startTimestamp, String endTimestamp)
    {
        OffsetDateTime start = OffsetDateTime.parse(startTimestamp);
        OffsetDateTime end = OffsetDateTime.parse(endTimestamp);
        MessageWindow messageWindow = new MessageWindow(start, end);

        this.processIncomingPackets.processSourceNotification(messageWindow);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("statusCode", "0");
        return jsonObject.toString();
    }
}