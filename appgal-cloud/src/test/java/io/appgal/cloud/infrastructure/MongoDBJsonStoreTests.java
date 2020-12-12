package io.appgal.cloud.infrastructure;

import com.google.gson.*;
import io.appgal.cloud.model.*;
import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.model.FoodRunner;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class MongoDBJsonStoreTests {
    private static Logger logger = LoggerFactory.getLogger(MongoDBJsonStoreTests.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @BeforeEach
    public void setUp()
    {

    }

    @AfterEach
    public void tearDown()
    {
        this.mongoDBJsonStore.cleanup();
    }

    @Test
    public void testDropOffNotificationStorageCycle()
    {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        Location location = new Location(30.25860595703125d,-97.74873352050781d);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "","",
                ProfileType.FOOD_RUNNER);
        FoodRunner foodRunner = new FoodRunner(profile, location);
        DropOffNotification dropOffNotification = new DropOffNotification(sourceOrg, location, foodRunner);

        logger.info(dropOffNotification.toJson().toString());

        this.mongoDBJsonStore.storeDropOffNotification(dropOffNotification);

        DropOffNotification stored = this.mongoDBJsonStore.findDropOffNotification("blah");
        JsonObject object = JsonParser.parseString(stored.toString()).getAsJsonObject();
        logger.info("*******");
        logger.info(this.gson.toJson(object));
        logger.info("*******");
    }

    @Test
    public void testStoreProfile()
    {
        Profile profile = new Profile("CLOUD_ID","blah@blah.com","8675309","photu","", ProfileType.FOOD_RUNNER);
        this.mongoDBJsonStore.storeProfile(profile);

        Profile storedProfile = this.mongoDBJsonStore.getProfile("blah@blah.com");
        logger.info("*******");
        logger.info(storedProfile.toString());
        logger.info("*******");
        assertTrue(storedProfile.getEmail().equals(profile.getEmail()));
    }

    @Test
    public void testSourceOrgLifecycle()
    {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        this.mongoDBJsonStore.storeSourceOrg(sourceOrg);
        List<SourceOrg> stored = this.mongoDBJsonStore.getSourceOrgs();

        JsonArray array = JsonParser.parseString(stored.toString()).getAsJsonArray();
        logger.info("*******");
        logger.info(this.gson.toJson(array));
        logger.info("*******");
        assertNotNull(stored);
    }

    @Test
    public void testStoreActiveNetwork()
    {
        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(startLatitude, startLongitude);
        FoodRunner foodRunner = new FoodRunner(profile, location);

        Map<String, FoodRunner> activeFoodRunners = new HashMap<>();
        activeFoodRunners.put(foodRunner.getProfile().getId(), foodRunner);

        this.mongoDBJsonStore.storeActiveNetwork(activeFoodRunners);

        ActiveNetwork activeNetwork = this.mongoDBJsonStore.getActiveNetwork();
        JsonArray array = JsonParser.parseString(activeNetwork.toString()).getAsJsonArray();
        logger.info("*******");
        logger.info(this.gson.toJson(array));
        logger.info("*******");
    }

    @Test
    public void testDeleteFoodRunner()
    {
        ActiveNetwork activeNetwork = this.mongoDBJsonStore.getActiveNetwork();
        JsonArray array = JsonParser.parseString(activeNetwork.toString()).getAsJsonArray();
        logger.info("***BEFORE****");
        logger.info(this.gson.toJson(array));
        logger.info("*******");

        this.mongoDBJsonStore.deleteFoodRunner(null);

        activeNetwork = this.mongoDBJsonStore.getActiveNetwork();
        array = JsonParser.parseString(activeNetwork.toString()).getAsJsonArray();
        logger.info("***AFTER****");
        logger.info(this.gson.toJson(array));
        logger.info("*******");
    }

    @Test
    public void testGetCompletedTrips()
    {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        Location location = new Location(30.25860595703125d,-97.74873352050781d);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "","", ProfileType.FOOD_RUNNER);
        FoodRunner foodRunner = new FoodRunner(profile, location);
        DropOffNotification dropOffNotification = new DropOffNotification(sourceOrg, location, foodRunner);
        CompletedTrip completedTrip = new CompletedTrip(foodRunner, dropOffNotification, null);
        this.mongoDBJsonStore.setCompletedTrip(completedTrip);

        List<CompletedTrip> completedTrips = this.mongoDBJsonStore.getCompletedTrips();
        JsonArray array = JsonParser.parseString(completedTrips.toString()).getAsJsonArray();
        logger.info("*******");
        logger.info(this.gson.toJson(array));
        logger.info("*******");
    }

    @Test
    public void testSourceOrgProfileRelationship()
    {
        SourceOrg sourceOrg = new SourceOrg("test", "TEST", "testing@test.com");
        for(int i=0; i<2; i++)
        {
            Profile profile = new Profile(UUID.randomUUID().toString(), "test"+i+"@test.com", "8675309", "", "test", ProfileType.ORG);
            profile.setSourceOrgId(sourceOrg.getOrgId());
            sourceOrg.getProfiles().add(profile);
        }

        this.mongoDBJsonStore.storeSourceOrg(sourceOrg);

        List<SourceOrg> sourceOrg1 = this.mongoDBJsonStore.getSourceOrgs();
        JsonArray array = JsonParser.parseString(sourceOrg1.toString()).getAsJsonArray();
        logger.info(this.gson.toJson(array));
    }

    @Test
    public void testStoreScheduledPickUpNotification() throws Exception
    {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(0.0d, 0.0d);
        FoodRunner bugsBunny = new FoodRunner(profile, location);
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        long epochSecond = start.toEpochSecond();

        SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification();
        schedulePickUpNotification.setSourceOrg(sourceOrg);
        schedulePickUpNotification.setFoodRunner(bugsBunny);
        schedulePickUpNotification.setStart(start);

        this.mongoDBJsonStore.storeScheduledPickUpNotification(schedulePickUpNotification);
    }
}
