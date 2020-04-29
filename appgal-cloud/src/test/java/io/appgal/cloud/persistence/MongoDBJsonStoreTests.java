package io.appgal.cloud.persistence;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.appgal.cloud.messaging.MessageWindow;
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

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class MongoDBJsonStoreTests {
    private static Logger logger = LoggerFactory.getLogger(MongoDBJsonStoreTests.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

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
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "");
        FoodRunner foodRunner = new FoodRunner(profile, location);
        DropOffNotification dropOffNotification = new DropOffNotification(sourceOrg, location, foodRunner);
        this.mongoDBJsonStore.storeDropOffNotification(dropOffNotification);

        DropOffNotification stored = this.mongoDBJsonStore.findDropOffNotification("blah");
        logger.info("*******");
        logger.info(stored.toString());
        logger.info("*******");
    }

    @Test
    public void testFindKafakaDaemonBootstrapData()
    {
        List<String> topics = this.mongoDBJsonStore.findKafakaDaemonBootstrapData();

        assertTrue(topics.contains(SourceNotification.TOPIC));
        assertTrue(topics.contains(DestinationNotification.TOPIC));
    }


    @Test
    public void testStoreProfile()
    {
        Profile profile = new Profile("CLOUD_ID","blah@blah.com","8675309","photu");
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

        logger.info("*******");
        logger.info(stored.toString());
        logger.info("*******");
        assertNotNull(stored);
    }

    @Test
    public void testStoreActiveNetwork()
    {
        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "");
        Location location = new Location(startLatitude, startLongitude);
        FoodRunner foodRunner = new FoodRunner(profile, location);

        Map<String, FoodRunner> activeFoodRunners = new HashMap<>();
        activeFoodRunners.put(foodRunner.getProfile().getId(), foodRunner);

        this.mongoDBJsonStore.storeActiveNetwork(activeFoodRunners);

        ActiveNetwork activeNetwork = this.mongoDBJsonStore.getActiveNetwork();
        logger.info("*******");
        logger.info(activeNetwork.toString());
        logger.info("*******");
    }

    @Test
    public void testDeleteFoodRunner()
    {
        ActiveNetwork activeNetwork = this.mongoDBJsonStore.getActiveNetwork();
        logger.info("***BEFORE****");
        logger.info(activeNetwork.toString());
        logger.info("*******");

        this.mongoDBJsonStore.deleteFoodRunner(null);

        activeNetwork = this.mongoDBJsonStore.getActiveNetwork();
        logger.info("***AFTER****");
        logger.info(activeNetwork.toString());
        logger.info("*******");
    }
}
