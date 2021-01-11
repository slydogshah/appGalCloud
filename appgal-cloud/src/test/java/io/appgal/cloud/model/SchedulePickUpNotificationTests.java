package io.appgal.cloud.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class SchedulePickUpNotificationTests extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(SchedulePickUpNotificationTests.class);

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Test
    public void testJson() throws Exception
    {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        sourceOrg.setProducer(true);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(0.0d, 0.0d);
        FoodRunner bugsBunny = new FoodRunner(profile, location);
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        long epochSecond = start.toEpochSecond();

        SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification(UUID.randomUUID().toString());
        schedulePickUpNotification.setSourceOrg(sourceOrg);
        schedulePickUpNotification.setFoodRunner(bugsBunny);
        schedulePickUpNotification.setStart(start);

        String ser = schedulePickUpNotification.toString();
        logger.info("*******");
        logger.info(schedulePickUpNotification.toJson().toString());

        SchedulePickUpNotification deser = SchedulePickUpNotification.parse(ser);
        assertEquals(0.0d, deser.getFoodRunner().getLocation().getLatitude());
        assertEquals(epochSecond, deser.getStart().toEpochSecond());
        assertEquals("microsoft", deser.getSourceOrg().getOrgId());
        assertEquals("bugs.bunny.shah@gmail.com", deser.getFoodRunner().getProfile().getEmail());
    }

    @Test
    public void testActivateNotification() throws Exception
    {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        sourceOrg.setProducer(true);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(0.0d, 0.0d);
        FoodRunner bugsBunny = new FoodRunner(profile, location);
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);

        SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification(UUID.randomUUID().toString());
        schedulePickUpNotification.setSourceOrg(sourceOrg);
        schedulePickUpNotification.setFoodRunner(bugsBunny);
        schedulePickUpNotification.setStart(start);

        assertTrue(schedulePickUpNotification.activateNotification());

        schedulePickUpNotification.setStart(OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(60));
        assertFalse(schedulePickUpNotification.activateNotification());
    }
}
