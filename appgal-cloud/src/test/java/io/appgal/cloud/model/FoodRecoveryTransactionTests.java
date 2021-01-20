package io.appgal.cloud.model;

import io.appgal.cloud.util.JsonUtil;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
public class FoodRecoveryTransactionTests {
    private static Logger logger = LoggerFactory.getLogger(FoodRecoveryTransactionTests.class);

    @Test
    public void testJson()
    {
        //pickup
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        sourceOrg.setProducer(true);
        SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification(UUID.randomUUID().toString());
        schedulePickUpNotification.setSourceOrg(sourceOrg);
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        schedulePickUpNotification.setStart(start);

        //dropoff
        SourceOrg church = new SourceOrg("church", "Church", "mrchrist@church.com",false);
        ScheduleDropOffNotification dropOffNotification = new ScheduleDropOffNotification(UUID.randomUUID().toString());
        dropOffNotification.setSourceOrg(church);


        FoodRecoveryTransaction tx = new FoodRecoveryTransaction(schedulePickUpNotification,dropOffNotification);
        JsonUtil.print(tx.toJson());
    }
}
