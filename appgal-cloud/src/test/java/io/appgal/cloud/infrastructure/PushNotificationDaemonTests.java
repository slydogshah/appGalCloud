package io.appgal.cloud.infrastructure;

import io.appgal.cloud.model.SchedulePickUpNotification;
import io.bugsbunny.test.components.MockData;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@QuarkusTest
public class PushNotificationDaemonTests {
    private static Logger logger = LoggerFactory.getLogger(PushNotificationDaemonTests.class);

    @Inject
    private PushNotificationDaemon pushNotificationDaemon;

    @Test
    public void sendNotifications() throws Exception
    {
        SchedulePickUpNotification schedulePickUpNotification = MockData.mockSchedulePickupNotification();
        OffsetDateTime startTime = schedulePickUpNotification.getStart();
        startTime = startTime.plus(2,ChronoUnit.SECONDS);
        schedulePickUpNotification.setStart(startTime);
        this.pushNotificationDaemon.sendNotifications(schedulePickUpNotification);
        Thread.sleep(5000);
    }
}
