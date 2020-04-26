package io.appgal.cloud.services;

import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.model.Location;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.SourceOrg;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@QuarkusTest
public class DeliveryOrchestratorTests {
    private static Logger logger = LoggerFactory.getLogger(DeliveryOrchestratorTests.class);

    @Inject
    private DeliveryOrchestrator deliveryOrchestrator;

    @Test
    public void testFindBestDestination()
    {
        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "");
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

    }
}
