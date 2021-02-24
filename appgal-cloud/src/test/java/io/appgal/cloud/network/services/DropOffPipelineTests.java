package io.appgal.cloud.network.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.appgal.cloud.model.*;
import io.bugsbunny.test.components.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DropOffPipelineTests extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(DropOffPipelineTests.class);

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private RequestPipeline requestPipeline = new RequestPipeline();

    private DropOffPipeline dropOffPipeline = new DropOffPipeline();

    @BeforeEach
    public void setUp() throws Exception
    {
        super.setUp();
        this.requestPipeline.clear();
        this.dropOffPipeline.clear();
    }

    //@Test
    public void testOrdering() throws Exception
    {
        for(int loop=0; loop < 10; loop++) {
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

                SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification(UUID.randomUUID().toString());
                schedulePickUpNotification.setSourceOrg(sourceOrg);
                schedulePickUpNotification.setFoodRunner(bugsBunny);
                schedulePickUpNotification.setStart(cour);
                logger.info("********************************************");
                //JsonUtil.print(schedulePickUpNotification.toJson());
                logger.info(cour.toString() + ":" + cour.toEpochSecond());

                this.requestPipeline.add(schedulePickUpNotification);
            }

            //Assert
            int size = schedulePickUpNotificationList.size();
            for (int i = 0; i < size; i++) {
                OffsetDateTime current = schedulePickUpNotificationList.get(i);
                if (current == null) {
                    break;
                }

                if (i == 0) {
                    assertEquals(middle, current);
                } else if (i == 1) {
                    assertEquals(end, current);
                } else if (i == 2) {
                    assertEquals(start, current);
                }
            }

            int counter = 0;
            while (true) {
                SchedulePickUpNotification current = this.requestPipeline.next();
                if (current == null) {
                    break;
                }

                if (counter == 0) {
                    assertEquals(start, current.getStart());
                } else if (counter == 1) {
                    assertEquals(middle, current.getStart());
                } else if (counter == 2) {
                    assertEquals(end, current.getStart());
                }

                counter++;
            }
        }
    }

    //@Test
    public void testPeek() throws Exception
    {
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

            SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification(UUID.randomUUID().toString());
            schedulePickUpNotification.setSourceOrg(sourceOrg);
            schedulePickUpNotification.setFoodRunner(bugsBunny);
            schedulePickUpNotification.setStart(cour);
            logger.info("********************************************");
            //JsonUtil.print(schedulePickUpNotification.toJson());
            logger.info(cour.toString() + ":" + cour.toEpochSecond());

            this.requestPipeline.add(schedulePickUpNotification);
        }

        SchedulePickUpNotification top = this.requestPipeline.peek();
        assertNotNull(top);
        assertEquals(3, this.requestPipeline.size());

        top = this.requestPipeline.next();
        assertNotNull(top);
        assertEquals(2, this.requestPipeline.size());
    }

    @Test
    public void testPickUpWithDropOffInFuture() throws Exception
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
    }
}
