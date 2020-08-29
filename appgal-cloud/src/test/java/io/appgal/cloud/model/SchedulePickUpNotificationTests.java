package io.appgal.cloud.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class SchedulePickUpNotificationTests {
    private static Logger logger = LoggerFactory.getLogger(SchedulePickUpNotificationTests.class);

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Test
    public void testJson() throws Exception
    {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification();
        schedulePickUpNotification.setSourceOrg(sourceOrg);

        logger.info("*******");
        logger.info(schedulePickUpNotification.toJson().toString());
    }
}
