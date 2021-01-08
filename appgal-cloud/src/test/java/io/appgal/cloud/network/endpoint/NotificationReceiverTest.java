package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonArray;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@QuarkusTest
public class NotificationReceiverTest extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(NotificationReceiverTest.class);

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @BeforeEach
    public void setUp() throws InterruptedException {
        JsonObject jsonObject = new JsonObject();
        List<String> ids = new ArrayList<>();
        for(int i=0; i< 10; i++) {
            jsonObject = new JsonObject();
            String id = UUID.randomUUID().toString();
            ids.add(id);
            jsonObject.addProperty("sourceNotificationId", id);
        }
    }

    //@Test
    public void testReceiveSourceNotification() {
        Response response = given().when().post("/notification/receive/?startTimestamp=1581392859&endTimestamp=1581393459")
                .andReturn();

        response.getBody().prettyPrint();
    }

    //@Test
    public void testReadDestinationNotifications() {
        given().when().post("/notification/receive/?startTimestamp=1581392859&endTimestamp=1581393459")
                .andReturn();
        Response response = given().when().get("/notification/readDestinationNotifications/?startTimestamp=1581392859&endTimestamp=1581393459")
                .andReturn();

        String json = response.getBody().prettyPrint();

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        JsonArray destinationNotifications = jsonObject.getAsJsonArray("destinationNotifications");
        assertNotNull(destinationNotifications);
    }

    //@Test
    public void testReceiveNotificationForPickup() {
        Response response = given().when().post("/notification/receiveNotificationForPickup/92ed655a-99a2-438b-8eeb-05d12a2d8a1b")
                .andReturn();

        String json = response.getBody().prettyPrint();
        //logger.info("****");
        //logger.info(json);
        //logger.info("****");

        //assert the body
        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        String sourceNotificationId = jsonObject.get("sourceNotificationId").getAsString();
        assertEquals("92ed655a-99a2-438b-8eeb-05d12a2d8a1b", sourceNotificationId);
    }

    //@Test
    public void testGetOutstandingFoodRunnerNotification() {
        Response response = given().when().get("/notification/getOutstandingFoodRunnerNotification").andReturn();

        String json = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(json);
        logger.info("****");

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String foodRunnerId = jsonObject.get("foodRunnerId").getAsString();
        assertNotNull(foodRunnerId);
    }

    @Test
    public void testPickUpNotifications() throws Exception{
        Location location = new Location(30.25860595703125d, -97.74873352050781d);
        JsonUtil.print(this.networkOrchestrator.getActiveView());

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

            SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification();
            schedulePickUpNotification.setSourceOrg(sourceOrg);
            schedulePickUpNotification.setFoodRunner(bugsBunny);
            schedulePickUpNotification.setStart(cour);
            logger.info("********************************************");
            //JsonUtil.print(schedulePickUpNotification.toJson());
            logger.info(cour.toString() + ":" + cour.toEpochSecond());

            this.networkOrchestrator.schedulePickUp(schedulePickUpNotification);
        }

        Thread.sleep(45000);


        Response response = given().when().get("/notification/pickup/notifications?email=bugs.bunny.shah@gmail.com")
                .andReturn();
        JsonUtil.print(JsonParser.parseString(response.getBody().asString()));
        JsonArray array = JsonParser.parseString(response.getBody().asString()).getAsJsonArray();
        assertTrue(array.size() > 0);
    }
}