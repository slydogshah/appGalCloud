package io.appgal.cloud.network.services;

import com.google.gson.*;
import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class NetworkOrchestratorTests {
    private static Logger logger = LoggerFactory.getLogger(NetworkOrchestratorTests.class);

    @Inject
    private MongoDBJsonStore mongoDBStore;
    @Inject
    private NetworkOrchestrator networkOrchestrator;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void testEnterNetwork() throws Exception {
        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "", "", ProfileType.FOOD_RUNNER);
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
        assertNotNull(runnerProfile);
        logger.info(runnerProfile.toString());
    }

    @Test
    public void testLeaveNetwork() throws Exception {
        try {
            //logger.info(this.networkOrchestrator.getActiveView().toString());

            double startLatitude = 30.25860595703125d;
            double startLongitude = -97.74873352050781d;
            String profileId = UUID.randomUUID().toString();
            Profile profile = new Profile(profileId, "bugs.bunny.shah@gmail.com", "8675309", "", "", ProfileType.FOOD_RUNNER);
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
            assertNotNull(runnerProfile);
            logger.info(runnerProfile.toString());

            this.networkOrchestrator.leaveNetwork(foodRunner);
            activeView = this.networkOrchestrator.getActiveView();
            logger.info("****************************************************");
            logger.info(activeView.toString());
            JsonArray allRunners = activeView.get("activeFoodRunners").getAsJsonArray();
            Iterator<JsonElement> iterator = allRunners.iterator();
            boolean found = false;
            while (iterator.hasNext()) {
                JsonObject cour = iterator.next().getAsJsonObject();
                FoodRunner local = FoodRunner.parse(cour.toString());
                if (local.getProfile().getId().equals(profileId)) {
                    found = true;
                }
            }
            assertFalse(found);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSendPickUpRequestLifeCycle() throws Exception {
        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "", "", ProfileType.FOOD_RUNNER);
        Location location = new Location(startLatitude, startLongitude);
        FoodRunner foodRunner = new FoodRunner(profile, location);

        this.networkOrchestrator.enterNetwork(foodRunner);


        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        sourceOrg.setLocation(location);
        PickupRequest pickupRequest = new PickupRequest();
        pickupRequest.setSourceOrg(sourceOrg);


        String pickupRequestId = this.networkOrchestrator.sendPickUpRequest(pickupRequest);

        logger.info("************************REQUEST_ID***************************************");
        logger.info("RequestId: " + pickupRequestId);
        logger.info("***************************************************************");

        JsonObject request = this.networkOrchestrator.getPickRequestResult(pickupRequestId);
        logger.info("*********************PICKUP_REQUEST******************************************");
        logger.info(request.toString());
        logger.info("***************************************************************");

        JsonArray latestResults = this.networkOrchestrator.getLatestResults(pickupRequestId);
        logger.info("********************RESULTS*******************************************");
        logger.info(latestResults.toString());
        logger.info("NUMBER: "+ latestResults.size());
        logger.info("***************************************************************");
    }

    @Test
    public void testOrchestration() throws Exception {
        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "", "",
                ProfileType.FOOD_RUNNER);
        Location location = new Location(startLatitude, startLongitude);
        FoodRunner bugsBunny = new FoodRunner(profile, location);

        startLatitude = 44.9441d;
        startLongitude = -93.0852d;
        profile = new Profile(UUID.randomUUID().toString(), "ms.dhoni@gmail.com", "8675309", "", "",
                ProfileType.FOOD_RUNNER);
        location = new Location(startLatitude, startLongitude);
        FoodRunner captain = new FoodRunner(profile, location);

        this.networkOrchestrator.enterNetwork(bugsBunny);
        this.networkOrchestrator.enterNetwork(captain);

        JsonObject activeView = this.networkOrchestrator.getActiveView();
        logger.info("*******");
        logger.info(activeView.toString());
        logger.info("*******");

        List<SourceOrg> sourceOrgs = new ArrayList<>();

        SourceOrg dropOff1 = new SourceOrg("church1", "DOWNTOWN_CHURCH", "downtown.church@gmail.com");
        SourceOrg dropOff2 = new SourceOrg("church2", "SUBURB_CHURCH", "suburb.church@gmail.com");


        JsonArray result = this.networkOrchestrator.getRegistered(
                dropOff1);
        assertNotNull(result);
        logger.info("*******");
        logger.info(result.toString());
        logger.info("*******");
    }
}