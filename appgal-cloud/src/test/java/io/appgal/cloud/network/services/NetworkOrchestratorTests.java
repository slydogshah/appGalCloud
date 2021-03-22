package io.appgal.cloud.network.services;

import com.google.gson.*;
import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.util.JsonUtil;
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
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "", "", ProfileType.FOOD_RUNNER);
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
            Profile profile = new Profile(profileId, "bugs.bunny.shah@gmail.com", 8675309l, "", "", ProfileType.FOOD_RUNNER);
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
    public void testActiveView() throws Exception {
        JsonObject activeView = this.networkOrchestrator.getActiveView();
        logger.info(gson.toJson(activeView));

        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "", "",
                ProfileType.FOOD_RUNNER);
        Location location = new Location(startLatitude, startLongitude);
        FoodRunner bugsBunny = new FoodRunner(profile, location);

        startLatitude = 44.9441d;
        startLongitude = -93.0852d;
        profile = new Profile(UUID.randomUUID().toString(), "ms.dhoni@gmail.com", 8675309l, "", "",
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
    public void testFindBestDestination()
    {
        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(startLatitude, startLongitude);
        FoodRunner bugsBunny = new FoodRunner(profile, location);
        final List<SourceOrg> bestDestination = this.networkOrchestrator.findBestDestination(bugsBunny);
        logger.info("*******");
        logger.info(bestDestination.toString());
        logger.info("*******");
    }

    @Test
    public void testSchedulePickUp() throws Exception
    {
        Location location = new Location(30.25860595703125d, -97.74873352050781d);
        JsonUtil.print(this.getClass(),this.networkOrchestrator.getActiveView());

        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC).withHour(1).withMinute(0).withSecond(0);

        OffsetDateTime middle = OffsetDateTime.now(ZoneOffset.UTC).withHour(12).withMinute(0).withSecond(0);

        OffsetDateTime end = OffsetDateTime.now(ZoneOffset.UTC).withHour(20).withMinute(0).withSecond(0);

        List<OffsetDateTime> schedulePickUpNotificationList = new LinkedList<>();
        schedulePickUpNotificationList.add(middle);
        schedulePickUpNotificationList.add(end);
        schedulePickUpNotificationList.add(start);
        logger.info(schedulePickUpNotificationList.toString());

        for (OffsetDateTime cour : schedulePickUpNotificationList) {
            SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com", true);
            sourceOrg.setProducer(true);
            sourceOrg.setLocation(location);
            Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "", "", ProfileType.FOOD_RUNNER);
            FoodRunner bugsBunny = new FoodRunner(profile, location);

            SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification(UUID.randomUUID().toString());
            schedulePickUpNotification.setSourceOrg(sourceOrg);
            schedulePickUpNotification.setFoodRunner(bugsBunny);
            schedulePickUpNotification.setStart(cour);
            logger.info("********************************************");
            JsonUtil.print(this.getClass(),schedulePickUpNotification.toJson());
            logger.info(cour.toString() + ":" + cour.toEpochSecond());

            this.networkOrchestrator.schedulePickUp(schedulePickUpNotification);
        }

        Thread.sleep(5000);
    }

    /*@Test
    public void testFoodRunnerMatchingProcess() throws Exception
    {
        int numberOfFoodRunners = 10;
        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Location location = new Location(startLatitude, startLongitude);
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        sourceOrg.setProducer(true);
        sourceOrg.setLocation(location);
        PickupRequest pickupRequest = new PickupRequest();
        pickupRequest.setSourceOrg(sourceOrg);
        for(int i=0; i<numberOfFoodRunners; i++) {
            Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com",
                    8675309l, "", "", ProfileType.FOOD_RUNNER);
            FoodRunner foodRunner;
            if(i % 2 == 0)
            {
                foodRunner = new FoodRunner(profile, location);
            }
            else
            {
                Location away = new Location(0d, 0d);
                foodRunner = new FoodRunner(profile, away);
            }
            logger.info("************************FOODRUNNER***************************************");
            logger.info("FoodRunner: " + foodRunner.getProfile().getId());
            logger.info("***************************************************************");

            this.networkOrchestrator.enterNetwork(foodRunner);
        }

        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        this.networkOrchestrator.schedulePickUp(new SchedulePickUpNotification(sourceOrg,start));

        Thread.sleep(5000);
    }*/
}