package io.appgal.cloud.network.services;

import com.google.gson.*;
import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class NetworkOrchestratorTests extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(NetworkOrchestratorTests.class);

    @Inject
    private MongoDBJsonStore mongoDBStore;
    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Inject
    private ActiveNetwork activeNetwork;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @AfterEach
    public void tearDown() throws Exception
    {

    }

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

        this.networkOrchestrator.leaveNetwork(foodRunner);
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
        int numberOfFoodRunners = 10;
        double startLatitude = 0.0d;
        double startLongitude = 0.0d;
        Location location = new Location(startLatitude, startLongitude);
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        sourceOrg.setLocation(location);
        PickupRequest pickupRequest = new PickupRequest();
        pickupRequest.setSourceOrg(sourceOrg);
        for(int i=0; i<numberOfFoodRunners; i++) {
            Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com",
                    "8675309", "", "", ProfileType.FOOD_RUNNER);
            FoodRunner foodRunner = new FoodRunner(profile, location);
            logger.info("************************FOODRUNNER***************************************");
            logger.info("FoodRunner: " + foodRunner.getProfile().getId());
            logger.info("***************************************************************");

            this.networkOrchestrator.enterNetwork(foodRunner);
        }

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
        assertEquals(numberOfFoodRunners, latestResults.size());
    }

    @Test
    public void testActiveView() throws Exception {
        JsonObject activeView = this.networkOrchestrator.getActiveView();
        logger.info(gson.toJson(activeView));

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

        activeView = this.networkOrchestrator.getActiveView();
        logger.info("***ACTIVE_VIEW****");
        logger.info(gson.toJson(activeView));
        logger.info("******************");
    }

    @Test
    public void testOrchestration() throws Exception {
        this.activeNetwork.clearActiveNetwork();

        List<FoodRunner> foodRunners = this.mongoDBStore.getAllFoodRunners();
        logger.info(foodRunners.toString());


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
        logger.info("***ACTIVE_VIEW****");
        logger.info(gson.toJson(activeView));
        logger.info("******************");

        List<SourceOrg> sourceOrgs = new ArrayList<>();
        Location dropLocation1 = new Location(30.25860595703125d, -97.74873352050781d);
        Location dropLocation2 = new Location(44.9441d, -93.0852d);

        SourceOrg dropOff1 = new SourceOrg("church1", "DOWNTOWN_CHURCH",
                "downtown.church@gmail.com");
        dropOff1.setLocation(dropLocation1);
        SourceOrg dropOff2 = new SourceOrg("church2", "SUBURB_CHURCH", "suburb.church@gmail.com");
        dropOff2.setLocation(dropLocation2);
        sourceOrgs.add(dropOff1);
        sourceOrgs.add(dropOff2);


        PickupRequest pickupRequest1 = new PickupRequest();
        pickupRequest1.setSourceOrg(dropOff1);

        PickupRequest pickupRequest2 = new PickupRequest();
        pickupRequest2.setSourceOrg(dropOff2);

        String pickupRequestId1 = this.networkOrchestrator.sendPickUpRequest(pickupRequest1);
        String pickupRequestId2 = this.networkOrchestrator.sendPickUpRequest(pickupRequest2);


        JsonArray latestResults1 = this.networkOrchestrator.getLatestResults(pickupRequestId1);
        logger.info("********************RESULTS*******************************************");
        logger.info(latestResults1.toString());
        logger.info("NUMBER: "+ latestResults1.size());
        logger.info("***************************************************************");
        assertEquals(1, latestResults1.size());


        JsonArray latestResults2 = this.networkOrchestrator.getLatestResults(pickupRequestId2);
        logger.info("********************RESULTS*******************************************");
        logger.info(latestResults2.toString());
        logger.info("NUMBER: "+ latestResults2.size());
        logger.info("***************************************************************");
        assertEquals(1, latestResults2.size());
    }

    @Test
    public void testFindBestDestination()
    {
        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(startLatitude, startLongitude);
        FoodRunner bugsBunny = new FoodRunner(profile, location);
        final List<SourceOrg> bestDestination = this.networkOrchestrator.findBestDestination(bugsBunny);
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

            this.networkOrchestrator.sendDeliveryNotification(destinationNotification);
            String newChainId = destinationNotification.getDropOffNotification().getFoodRunner().getProfile().getChainId();
            assertNotNull(newChainId);
            logger.info("*********************");
            logger.info("ChainId: "+newChainId);
            logger.info("*********************");

            this.networkOrchestrator.sendDeliveryNotification(destinationNotification);
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