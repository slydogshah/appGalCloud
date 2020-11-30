package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DestinationNotificationTests {
    private static Logger logger = LoggerFactory.getLogger(DestinationNotificationTests.class);

    @Test
    public void testToString()
    {
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        long startTimestamp = start.toEpochSecond();
        long endTimestamp = end.toEpochSecond();
        MessageWindow messageWindow = new MessageWindow();
        messageWindow.setStart(start);
        messageWindow.setEnd(end);

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
        Location location = new Location(30.25860595703125d,-97.74873352050781d);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com",
                "8675309", "", "", ProfileType.FOOD_RUNNER, location);
        FoodRunner foodRunner = new FoodRunner(profile, location);
        DropOffNotification dropOffNotification = new DropOffNotification(destinationOrg, location, foodRunner);
        destinationNotification.setDropOffNotification(dropOffNotification);

        JsonObject json = JsonParser.parseString(destinationNotification.toString()).getAsJsonObject();
        logger.info("****");
        logger.info(destinationNotification.toString());
        logger.info("****");
    }
}
