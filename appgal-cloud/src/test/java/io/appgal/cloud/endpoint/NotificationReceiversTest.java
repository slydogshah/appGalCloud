package io.appgal.cloud.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

@QuarkusTest
public class NotificationReceiversTest {
    private static Logger logger = LoggerFactory.getLogger(NotificationReceiversTest.class);

    @Test
    public void testReceiveSourceNotification() {
        Response response = given().when().post("/receive/?startTimestamp=1581392859&endTimestamp=1581393459")
                .andReturn();

        String json = response.getBody().prettyPrint();
        //logger.info("****");
        //logger.info(json);
        //logger.info("****");

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String statusCode = jsonObject.get("statusCode").getAsString();

        assertEquals("0", statusCode);
    }
}