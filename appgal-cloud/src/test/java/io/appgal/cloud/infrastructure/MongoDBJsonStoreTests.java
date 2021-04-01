package io.appgal.cloud.infrastructure;

import com.google.gson.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.appgal.cloud.model.*;
import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.util.JsonUtil;
import io.bugsbunny.test.components.BaseTest;
import io.bugsbunny.test.components.MockData;
import io.quarkus.test.junit.QuarkusTest;
import org.bson.Document;
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
public class MongoDBJsonStoreTests extends BaseTest {
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
    }

    //TODO: SPEEDUP
    //@Test
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

    //TODO: SPEEDUP
    //@Test
    public void testStoreActiveNetwork()
    {
        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "","", ProfileType.FOOD_RUNNER);
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

    //TODO: SPEEDUP
    //@Test
    public void updateActiveNetwork()
    {
        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "","", ProfileType.FOOD_RUNNER);
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

        this.mongoDBJsonStore.storeActiveNetwork(activeNetwork.getActiveFoodRunners());
    }

    //TODO: SPEEDUP
    //@Test
    public void testGetCompletedTrips()
    {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        Location location = new Location(30.25860595703125d,-97.74873352050781d);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "","", ProfileType.FOOD_RUNNER);
        FoodRunner foodRunner = new FoodRunner(profile, location);
        DropOffNotification dropOffNotification = new DropOffNotification(sourceOrg, location, foodRunner);
        CompletedTrip completedTrip = new CompletedTrip(foodRunner, dropOffNotification);
        this.mongoDBJsonStore.setCompletedTrip(completedTrip);

        List<CompletedTrip> completedTrips = this.mongoDBJsonStore.getCompletedTrips();
        JsonArray array = JsonParser.parseString(completedTrips.toString()).getAsJsonArray();
        logger.info("*******");
        logger.info(this.gson.toJson(array));
        logger.info("*******");
    }


    @Test
    public void testStoreProfile()
    {
        Profile profile = MockData.mockProfile();
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
        SourceOrg sourceOrg = MockData.mockProducerOrg();
        //JsonUtil.print(sourceOrg.toJson());


        this.mongoDBJsonStore.storeSourceOrg(sourceOrg);


        List<SourceOrg> stored = this.mongoDBJsonStore.getSourceOrgs();

        JsonArray array = JsonParser.parseString(stored.toString()).getAsJsonArray();
        JsonUtil.print(this.getClass(),array);
        assertNotNull(stored);
    }

    @Test
    public void testGetAllFoodRunners()
    {
        final List<FoodRunner> allFoodRunners = this.mongoDBJsonStore.getAllFoodRunners();
        logger.info(allFoodRunners.toString());
    }

    @Test
    public void testSourceOrgProfileRelationship()
    {
        for(int i=0; i<2; i++)
        {
            SourceOrg sourceOrg = new SourceOrg("test"+i, "TEST"+i,
                    "testing"+i+"@test.com",true);
            Profile profile = new Profile(UUID.randomUUID().toString(),
                    UUID.randomUUID().toString()+"@test.com", 8675309l, "", "test", ProfileType.ORG);
            profile.setSourceOrgId(sourceOrg.getOrgId());
            sourceOrg.getProfiles().add(profile);
            this.mongoDBJsonStore.storeSourceOrg(sourceOrg);
        }


        List<SourceOrg> sourceOrg1 = this.mongoDBJsonStore.getSourceOrgs();
        JsonArray array = JsonParser.parseString(sourceOrg1.toString()).getAsJsonArray();
        logger.info(this.gson.toJson(array));
    }

    @Test
    public void testStoreScheduledPickUpNotification() throws Exception
    {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(0.0d, 0.0d);
        FoodRunner bugsBunny = new FoodRunner(profile, location);
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        long epochSecond = start.toEpochSecond();

        SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification(UUID.randomUUID().toString());
        schedulePickUpNotification.setSourceOrg(sourceOrg);
        schedulePickUpNotification.setFoodRunner(bugsBunny);
        schedulePickUpNotification.setStart(start);
        schedulePickUpNotification.setNotificationSent(true);
        for(int j=0; j<3; j++)
        {
            schedulePickUpNotification.addPickupNote(new Note("note/"+j));
        }

        this.mongoDBJsonStore.storeScheduledPickUpNotification(schedulePickUpNotification);

        List<SchedulePickUpNotification> notifications = this.mongoDBJsonStore.getSchedulePickUpNotifications(bugsBunny.getProfile().getEmail());
        JsonUtil.print(this.getClass(),JsonParser.parseString(notifications.toString()));
        assertFalse(notifications.isEmpty());

        bugsBunny.getProfile().setEmail("bogus@bogus.com");
        notifications = this.mongoDBJsonStore.getSchedulePickUpNotifications(bugsBunny.getProfile().getEmail());
        JsonUtil.print(this.getClass(),JsonParser.parseString(notifications.toString()));
        assertTrue(notifications.isEmpty());
    }

    @Test
    public void testUpdateScheduledPickUpNotification() throws Exception
    {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(0.0d, 0.0d);
        FoodRunner bugsBunny = new FoodRunner(profile, location);

        SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification(UUID.randomUUID().toString());
        schedulePickUpNotification.setSourceOrg(sourceOrg);
        schedulePickUpNotification.setFoodRunner(bugsBunny);

        this.mongoDBJsonStore.storeScheduledPickUpNotification(schedulePickUpNotification);

        JsonObject stored = this.mongoDBJsonStore.getScheduledPickUpNotification(schedulePickUpNotification.getId());
        JsonUtil.print(this.getClass(),stored);

        SchedulePickUpNotification storedNotification = SchedulePickUpNotification.parse(stored.toString());
        assertFalse(storedNotification.isNotificationSent());
        storedNotification.setNotificationSent(true);
        this.mongoDBJsonStore.updateScheduledPickUpNotification(storedNotification);

        stored = this.mongoDBJsonStore.getScheduledPickUpNotification(schedulePickUpNotification.getId());
        JsonUtil.print(this.getClass(),stored);
        storedNotification = SchedulePickUpNotification.parse(stored.toString());
        assertTrue(storedNotification.isNotificationSent());
    }

    @Test
    public void testStoreScheduledDropOffNotification() throws Exception
    {
        SourceOrg sourceOrg = new SourceOrg("microsoft",
                "Microsoft",
                "melinda_gates@microsoft.com",true);
        Profile profile = new Profile(UUID.randomUUID().toString(),
                "bugs.bunny.shah@gmail.com", 8675309l, "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(0.0d, 0.0d);
        FoodRunner bugsBunny = new FoodRunner(profile, location);

        ScheduleDropOffNotification notification = new ScheduleDropOffNotification(UUID.randomUUID().toString());
        notification.setSourceOrg(sourceOrg);
        notification.setFoodRunner(bugsBunny);

        this.mongoDBJsonStore.storeScheduledDropOffNotification(notification);
        JsonObject stored = this.mongoDBJsonStore.getScheduledDropOffNotification(notification.getId());
        ScheduleDropOffNotification storedNotification = ScheduleDropOffNotification.parse(stored.toString());
        JsonUtil.print(this.getClass(),storedNotification.toJson());
        assertFalse(storedNotification.isNotificationSent());
    }

    @Test
    public void testUpdateScheduledDropOffNotification() throws Exception
    {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(0.0d, 0.0d);
        FoodRunner bugsBunny = new FoodRunner(profile, location);
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        long epochSecond = start.toEpochSecond();

        ScheduleDropOffNotification notification = new ScheduleDropOffNotification(UUID.randomUUID().toString());
        notification.setSourceOrg(sourceOrg);
        notification.setFoodRunner(bugsBunny);
        notification.setStart(start);

        this.mongoDBJsonStore.storeScheduledDropOffNotification(notification);

        JsonObject stored = this.mongoDBJsonStore.getScheduledDropOffNotification(notification.getId());
        JsonUtil.print(this.getClass(),stored);

        ScheduleDropOffNotification storedNotification = ScheduleDropOffNotification.parse(stored.toString());
        assertFalse(storedNotification.isNotificationSent());
        storedNotification.setNotificationSent(true);
        this.mongoDBJsonStore.updateScheduledDropOffNotification(storedNotification);

        stored = this.mongoDBJsonStore.getScheduledDropOffNotification(notification.getId());
        JsonUtil.print(this.getClass(),stored);
        storedNotification = ScheduleDropOffNotification.parse(stored.toString());
        assertTrue(storedNotification.isNotificationSent());
    }

    @Test
    public void testGetScheduledDropOffNotifications() throws Exception
    {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(0.0d, 0.0d);
        FoodRunner bugsBunny = new FoodRunner(profile, location);
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        long epochSecond = start.toEpochSecond();

        for(int i=0; i<10; i++) {
            ScheduleDropOffNotification notification = new ScheduleDropOffNotification(UUID.randomUUID().toString());
            notification.setSourceOrg(sourceOrg);
            notification.setFoodRunner(bugsBunny);
            notification.setStart(start);
            notification.setNotificationSent(true);
            for(int j=0; j<3; j++)
            {
                notification.addDropOffNote(new Note("note/"+j));
            }
            this.mongoDBJsonStore.storeScheduledDropOffNotification(notification);
        }

        List<ScheduleDropOffNotification> stored = this.mongoDBJsonStore.getScheduledDropOffNotifications(sourceOrg.getOrgId());
        JsonUtil.print(this.getClass(),JsonParser.parseString(stored.toString()));
        assertFalse(stored.isEmpty());
        for(ScheduleDropOffNotification cour:stored)
        {
            assertTrue(cour.isNotificationSent());
        }
    }

    //@Test
    public void testFoodRecoveryTransactionLifeCycle()
    {
        FoodRecoveryTransaction tx = MockData.mockFoodRecoveryTransaction();
        JsonUtil.print(this.getClass(),tx.toJson());

        this.mongoDBJsonStore.storeFoodRecoveryTransaction(tx);

        List<FoodRecoveryTransaction> list = this.mongoDBJsonStore.getFoodRecoveryTransactions(tx.getPickUpNotification()
        .getSourceOrg().getOrgContactEmail());
        JsonUtil.print(this.getClass(),JsonParser.parseString(list.toString()));
        assertFalse(list.isEmpty());
    }

    @Test
    public void testFoodRecoveryTransactionHistory()
    {
        FoodRecoveryTransaction tx = MockData.mockFoodRecoveryTransaction();
        tx.setTransactionState(TransactionState.CLOSED);
        JsonUtil.print(this.getClass(),tx.toJson());

        this.mongoDBJsonStore.storeFoodRecoveryTransaction(tx);

        List<FoodRecoveryTransaction> list = this.mongoDBJsonStore.getFoodRecoveryTransactionHistory(tx.
                getPickUpNotification().getSourceOrg().getOrgId());
        JsonUtil.print(this.getClass(),JsonParser.parseString(list.toString()));
        assertFalse(list.isEmpty());
    }

    @Test
    public void testFoodRecoveryDropOffHistory()
    {
        FoodRecoveryTransaction tx = MockData.mockFoodRecoveryTransaction();
        tx.setTransactionState(TransactionState.CLOSED);
        JsonUtil.print(this.getClass(),tx.toJson());

        this.mongoDBJsonStore.storeFoodRecoveryTransaction(tx);

        List<FoodRecoveryTransaction> list = this.mongoDBJsonStore.getFoodRecoveryDropOffHistory(tx.getDropOffNotification().getSourceOrg().getOrgId());
        JsonUtil.print(this.getClass(),JsonParser.parseString(list.toString()));
        assertFalse(list.isEmpty());
    }
}
