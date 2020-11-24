package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.appgal.cloud.infrastructure.messaging.KafkaDaemon;
import io.appgal.cloud.model.SourceNotification;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@QuarkusTest
public class NotificationReceiverTest {
    private static Logger logger = LoggerFactory.getLogger(NotificationReceiverTest.class);

    @Inject
    private KafkaDaemon kafkaDaemon;

    @BeforeEach
    public void setUp() throws InterruptedException {
        JsonObject jsonObject = new JsonObject();
        List<String> ids = new ArrayList<>();
        for(int i=0; i< 10; i++) {
            jsonObject = new JsonObject();
            String id = UUID.randomUUID().toString();
            ids.add(id);
            jsonObject.addProperty("sourceNotificationId", id);
            this.kafkaDaemon.produceData(SourceNotification.TOPIC, jsonObject);
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
}