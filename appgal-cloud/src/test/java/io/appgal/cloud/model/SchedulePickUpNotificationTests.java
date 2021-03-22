package io.appgal.cloud.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.appgal.cloud.util.JsonUtil;
import io.bugsbunny.test.components.BaseTest;
import io.bugsbunny.test.components.MockData;
import io.quarkus.test.Mock;
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
        SchedulePickUpNotification schedulePickUpNotification = MockData.mockSchedulePickupNotification();
        long epochSecond = schedulePickUpNotification.start.toEpochSecond();

        String ser = schedulePickUpNotification.toString();
        JsonUtil.print(this.getClass(),schedulePickUpNotification.toJson());

        SchedulePickUpNotification deser = SchedulePickUpNotification.parse(ser);
        assertEquals(0.0d, deser.getFoodRunner().getLocation().getLatitude());
        assertEquals(epochSecond, deser.getStart().toEpochSecond());
        assertEquals("microsoft", deser.getSourceOrg().getOrgId());
        assertEquals("bugs.bunny.shah@gmail.com", deser.getFoodRunner().getProfile().getEmail());
        JsonUtil.print(this.getClass(),deser.toJson());
    }

    @Test
    public void testActivateNotification() throws Exception
    {
        SchedulePickUpNotification schedulePickUpNotification = MockData.mockSchedulePickupNotification();

        assertTrue(schedulePickUpNotification.activateNotification());

        schedulePickUpNotification.setStart(OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(60));
        assertFalse(schedulePickUpNotification.activateNotification());
    }
}
