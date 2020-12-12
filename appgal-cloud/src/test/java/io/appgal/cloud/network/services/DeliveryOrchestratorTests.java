package io.appgal.cloud.network.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.appgal.cloud.model.*;
import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class DeliveryOrchestratorTests extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(DeliveryOrchestratorTests.class);

    @Inject
    private DeliveryOrchestrator deliveryOrchestrator;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    //@Test
    public void testFindBestDestination()
    {
        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(startLatitude, startLongitude);
        FoodRunner bugsBunny = new FoodRunner(profile, location);
        final List<SourceOrg> bestDestination = this.deliveryOrchestrator.findBestDestination(bugsBunny);
        logger.info("*******");
        logger.info(bestDestination.toString());
        logger.info("*******");
    }

    @Test
    public void testSendDeliveryNotification()
    {
        try {
            OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
            OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
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
            Location location = new Location(30.25860595703125d, -97.74873352050781d);
            Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com",
                    "8675309", "", "", ProfileType.FOOD_RUNNER, location);
            FoodRunner foodRunner = new FoodRunner(profile, location);
            foodRunner.setPickUpOrg(sourceOrg1);
            DropOffNotification dropOffNotification = new DropOffNotification(destinationOrg, location, foodRunner);
            destinationNotification.setDropOffNotification(dropOffNotification);

            assertNull(destinationNotification.getDropOffNotification().getFoodRunner().getProfile().getChainId());

            this.deliveryOrchestrator.sendDeliveryNotification(destinationNotification);
            String newChainId = destinationNotification.getDropOffNotification().getFoodRunner().getProfile().getChainId();
            assertNotNull(newChainId);
            logger.info("*********************");
            logger.info("ChainId: "+newChainId);
            logger.info("*********************");

            this.deliveryOrchestrator.sendDeliveryNotification(destinationNotification);
            String sameChainId = destinationNotification.getDropOffNotification().getFoodRunner().getProfile().getChainId();
            logger.info("*********************");
            logger.info("ChainId: "+destinationNotification.getDropOffNotification().getFoodRunner().getProfile().getChainId());
            logger.info("*********************");

            assertEquals(newChainId, sameChainId);
        }
        catch(Exception e)
        {
            logger.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
