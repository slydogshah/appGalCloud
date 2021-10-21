package io.appgal.cloud.network.services;

import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.*;
import io.appgal.cloud.util.JsonUtil;
import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
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

@QuarkusTest
public class FoodRecoveryOrchestratorTests extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(FoodRecoveryOrchestrator.class);

    @Inject
    private FoodRecoveryOrchestrator foodRecoveryOrchestrator;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Test
    public void testFindDropOffOrganizations() {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        sourceOrg.setLocation(new Location(30.269090d, -97.751830d));
        this.mongoDBJsonStore.storeSourceOrg(sourceOrg);

        for(int i=0; i<2; i++){
            SourceOrg dropOrg = new SourceOrg("church"+i, "Church", UUID.randomUUID().toString()+"@church.com",
                    false);
            if(i==0) {
                dropOrg.setLocation(new Location(30.259590d, -97.747800d));
            }
            else{
                dropOrg.setLocation(new Location(39.253770d, -76.789370d));
            }
            this.mongoDBJsonStore.storeSourceOrg(dropOrg);
        }

        List<SourceOrg> sourceOrgList = this.foodRecoveryOrchestrator.findDropOffOrganizations("microsoft");
        JsonUtil.print(this.getClass(),JsonParser.parseString(sourceOrgList.toString()));
        assertEquals(1,sourceOrgList.size());
        assertEquals("church0",sourceOrgList.get(0).getOrgId());
    }

    @Test
    public void testDropOffRecommender()
    {
        Location location = new Location(30.25860595703125d, -97.74873352050781d);
        JsonUtil.print(this.getClass(),this.networkOrchestrator.getActiveView());

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
            sourceOrg.setLocation(location);
            Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "", "", ProfileType.FOOD_RUNNER);
            FoodRunner bugsBunny = new FoodRunner(profile, location);
            Address address = new Address();
            address.setTimeZone("US/Central");
            sourceOrg.setAddress(address);

            SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification(UUID.randomUUID().toString());
            schedulePickUpNotification.setSourceOrg(sourceOrg);
            schedulePickUpNotification.setFoodRunner(bugsBunny);
            schedulePickUpNotification.setStart(cour);
            logger.info("********************************************");
            JsonUtil.print(this.getClass(),schedulePickUpNotification.toJson());
            logger.info(cour.toString() + ":" + cour.toEpochSecond());

            this.networkOrchestrator.startPickUpProcess(null, schedulePickUpNotification);
            this.networkOrchestrator.schedulePickUp(schedulePickUpNotification);
        }
    }
}