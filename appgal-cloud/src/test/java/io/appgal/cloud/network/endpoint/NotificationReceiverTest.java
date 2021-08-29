package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.appgal.cloud.model.*;
import io.appgal.cloud.network.services.NetworkOrchestrator;
import io.appgal.cloud.util.JsonUtil;
import io.bugsbunny.test.components.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@QuarkusTest
public class NotificationReceiverTest extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(NotificationReceiverTest.class);

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Test
    public void testPickUpNotifications() throws Exception{
        Location location = new Location(30.25860595703125d, -97.74873352050781d);
        JsonUtil.print(this.getClass(),this.networkOrchestrator.getActiveView());

        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC).withHour(1).withMinute(0).withSecond(0);

        OffsetDateTime middle = OffsetDateTime.now(ZoneOffset.UTC).withHour(12).withMinute(0).withSecond(0);

        OffsetDateTime end = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1).plusHours(1);

        List<OffsetDateTime> schedulePickUpNotificationList = new LinkedList<>();
        schedulePickUpNotificationList.add(middle);
        schedulePickUpNotificationList.add(end);
        schedulePickUpNotificationList.add(start);
        List<String> excluded = new LinkedList<>();
        logger.info(schedulePickUpNotificationList.toString());

        for (int i=0; i<schedulePickUpNotificationList.size();i++) {
            OffsetDateTime cour = schedulePickUpNotificationList.get(i);
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

            this.networkOrchestrator.startPickUpProcess(null,schedulePickUpNotification);
            this.networkOrchestrator.schedulePickUp(schedulePickUpNotification);

            if(cour.toEpochSecond() == end.toEpochSecond())
            {
                excluded.add(schedulePickUpNotification.getId());
            }
        }

        logger.info("EXCLUDED: "+excluded.toString());
        assertTrue(excluded.size()==1);
        Thread.sleep(5000);

        Response response = given().when().get("/tx/recovery/?email=bugs.bunny.shah@gmail.com")
                .andReturn();
        JsonObject object = JsonParser.parseString(response.getBody().asString()).getAsJsonObject();
        JsonArray array = object.getAsJsonArray("pending");
        JsonUtil.print(this.getClass(),array);

        response = given().when().get("/notification/pickup/notifications?email=bugs.bunny.shah@gmail.com")
                .andReturn();
        array = JsonParser.parseString(response.getBody().asString()).getAsJsonArray();
        JsonUtil.print(this.getClass(),array);
        //assertTrue(array.size() > 0);
        Iterator<JsonElement> itr = array.iterator();
        while(itr.hasNext())
        {
            JsonObject cour = itr.next().getAsJsonObject();
            String id = cour.get("id").getAsString();

            logger.info("****************************************");
            logger.info("Exclude: "+excluded.get(0).toString());
            logger.info("Current: "+id);
            logger.info("****************************************");

            assertFalse(excluded.contains(id));
            assertTrue(cour.get("notificationSent").getAsBoolean());
        }
    }

    @Test
    public void addPickupDetails() throws Exception{
        Response response = given().when().get("/notification/addPickupDetails")
                .andReturn();
        response.getBody().prettyPrint();
    }
}