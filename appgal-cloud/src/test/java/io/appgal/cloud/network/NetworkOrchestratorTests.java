package io.appgal.cloud.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.model.Location;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.network.model.FoodRunner;
import io.appgal.cloud.network.model.PickupRequest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
public class NetworkOrchestratorTests {
    private static Logger logger = LoggerFactory.getLogger(NetworkOrchestrator.class);

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Test
    public void testBootup() throws Exception
    {
        this.networkOrchestrator.bootUp();

        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "");
        Location location = new Location(startLatitude, startLongitude);
        FoodRunner foodRunner = new FoodRunner(profile, location);

        this.networkOrchestrator.enterNetwork(foodRunner);

        JsonObject activeView = this.networkOrchestrator.getActiveView();
        logger.info("*******");
        logger.info(activeView.toString());
        logger.info("*******");

        JsonArray activeFoodRunners = activeView.getAsJsonArray("activeFoodRunners");
        JsonObject activeFoodRunner = activeFoodRunners.get(0).getAsJsonObject();
        JsonObject runnerProfile = activeFoodRunner.getAsJsonObject("profile");
        assertEquals("bugs.bunny.shah@gmail.com", runnerProfile.get("email").getAsString());
    }

    @Test
    public void testOrchestration() throws Exception
    {
        this.networkOrchestrator.bootUp();

        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "");
        Location location = new Location(startLatitude, startLongitude);
        FoodRunner bugsBunny = new FoodRunner(profile, location);

        startLatitude = 44.9441d;
        startLongitude = -93.0852d;
        profile = new Profile(UUID.randomUUID().toString(), "ms.dhoni@gmail.com", "8675309", "");
        location = new Location(startLatitude, startLongitude);
        FoodRunner captain = new FoodRunner(profile, location);

        this.networkOrchestrator.enterNetwork(bugsBunny);
        this.networkOrchestrator.enterNetwork(captain);

        JsonObject activeView = this.networkOrchestrator.getActiveView();
        logger.info("*******");
        logger.info(activeView.toString());
        logger.info("*******");

        SourceOrg pickUp1 = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        SourceOrg pickUp2 = new SourceOrg("apple", "Apple", "tim_cook@apple.com");

        SourceOrg dropOff1 = new SourceOrg("church1", "DOWNTOWN_CHURCH", "downtown.church@gmail.com");
        SourceOrg dropOff2 = new SourceOrg("church2", "SUBURB_CHURCH", "suburb.church@gmail.com");

        PickupRequest pickupRequest = new PickupRequest(pickUp1, new Location(30.25860595703125d,-97.74873352050781d));
        this.networkOrchestrator.sendPickUpRequest(pickupRequest);
    }
}
