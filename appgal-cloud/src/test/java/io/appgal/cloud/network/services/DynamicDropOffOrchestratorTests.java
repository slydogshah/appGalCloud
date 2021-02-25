package io.appgal.cloud.network.services;

import com.google.gson.JsonArray;
import io.appgal.cloud.model.*;
import io.appgal.cloud.util.JsonUtil;
import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
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

    @Inject
    private DropOffPipeline dropOffPipeline;

    @BeforeEach
    public void setUp() throws Exception
    {
        super.setUp();
        this.dropOffPipeline.clear();
    }

    @Test
    public void testOrchestrateOfflineCommunity()
    {
        this.dynamicDropOffOrchestrator.orchestrateOfflineCommunity();
    }

    @Test
    public void testGetOfflineDropOffPipeline()
    {
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC).withHour(1).withMinute(0).withSecond(0);

        OffsetDateTime middle = OffsetDateTime.now(ZoneOffset.UTC).withHour(12).withMinute(0).withSecond(0);

        OffsetDateTime end = OffsetDateTime.now(ZoneOffset.UTC).withHour(20).withMinute(0).withSecond(0);

        List<OffsetDateTime> scheduleDropOffNotificationList = new LinkedList<>();
        scheduleDropOffNotificationList.add(middle);
        scheduleDropOffNotificationList.add(end);
        scheduleDropOffNotificationList.add(start);
        logger.info(scheduleDropOffNotificationList.toString());

        for (OffsetDateTime cour : scheduleDropOffNotificationList) {
            SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com", true);
            sourceOrg.setProducer(true);
            Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "", "", ProfileType.FOOD_RUNNER);
            Location location = new Location(0.0d, 0.0d);
            FoodRunner bugsBunny = new FoodRunner(profile, location);

            ScheduleDropOffNotification scheduleDropOffNotification = new ScheduleDropOffNotification(UUID.randomUUID().toString());
            //scheduleDropOffNotification.setSourceOrg(sourceOrg);
            scheduleDropOffNotification.setFoodRunner(bugsBunny);
            scheduleDropOffNotification.setStart(cour);
            logger.info("********************************************");
            //JsonUtil.print(schedulePickUpNotification.toJson());
            logger.info(cour.toString() + ":" + cour.toEpochSecond());

            this.dropOffPipeline.add(scheduleDropOffNotification);
        }

        ScheduleDropOffNotification top = this.dropOffPipeline.peek();
        assertNotNull(top);
        assertEquals(3, this.dropOffPipeline.size());

        top = this.dropOffPipeline.next();
        assertNotNull(top);
        assertEquals(2, this.dropOffPipeline.size());

        JsonArray pipeline = this.dynamicDropOffOrchestrator.getOfflineDropOffPipeline();
        JsonUtil.print(pipeline);
    }
}
