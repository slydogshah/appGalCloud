package io.appgal.cloud.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.appgal.cloud.model.*;
import io.appgal.cloud.network.services.DeliveryOrchestrator;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class DeliveryOrchestratorTests {
    private static Logger logger = LoggerFactory.getLogger(DeliveryOrchestratorTests.class);

    @Inject
    private DeliveryOrchestrator deliveryOrchestrator;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Test
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

    }

    @Test
    public void testFoodRequestCycle()
    {
        FoodRequest foodRequest = new FoodRequest();
        SourceOrg sourceOrg1 = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        foodRequest.setFoodType(FoodTypes.VEG);
        foodRequest.setSourceOrg(sourceOrg1);

        String requestId = this.deliveryOrchestrator.sendFoodRequest(foodRequest);
        FoodRequest sent = this.deliveryOrchestrator.getFoodRequest(requestId);

        assertEquals(sent.getId(), requestId);

        logger.info("*******");
        logger.info(this.gson.toJson(sent.toJson()));
    }

    @Test
    public void testSchedulePickUp() throws Exception
    {
        this.deliveryOrchestrator.schedulePickUp(null);
    }
}
