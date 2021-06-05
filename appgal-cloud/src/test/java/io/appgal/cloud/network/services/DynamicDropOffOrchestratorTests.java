package io.appgal.cloud.network.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.model.*;
import io.appgal.cloud.util.JsonUtil;
import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class DynamicDropOffOrchestratorTests extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(DynamicDropOffOrchestratorTests.class);

    @Inject
    private DynamicDropOffOrchestrator dynamicDropOffOrchestrator;

    @BeforeEach
    public void setUp() throws Exception
    {
        super.setUp();

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
            Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "", "", ProfileType.FOOD_RUNNER);
            Location location = new Location(0.0d, 0.0d);
            FoodRunner bugsBunny = new FoodRunner(profile, location);

            SchedulePickUpNotification notification = new SchedulePickUpNotification(UUID.randomUUID().toString());
            notification.setDropOffDynamic(true);
            notification.setFoodRunner(bugsBunny);
            notification.setStart(cour);
            logger.info("********************************************");
            JsonUtil.print(this.getClass(),notification.toJson());
            logger.info(cour.toString() + ":" + cour.toEpochSecond());
        }
        Thread.sleep(5000);
    }

    @Test
    public void notifyAvailability()
    {
        this.dynamicDropOffOrchestrator.notifyAvailability("123");
    }

    @Test
    public void testOrchestrateOfflineCommunity()
    {
        JsonObject json = this.dynamicDropOffOrchestrator.orchestrateOfflineCommunity();
        JsonUtil.print(this.getClass(),json);
    }

    @Test
    public void testGetOfflineDropOffPipeline() throws Exception
    {
        JsonArray pipeline = this.dynamicDropOffOrchestrator.getOfflineDropOffPipeline();
        JsonUtil.print(this.getClass(),pipeline);
    }
}
