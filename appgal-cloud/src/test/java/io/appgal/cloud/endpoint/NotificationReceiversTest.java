package io.appgal.cloud.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class NotificationReceiversTest {
    private static Logger logger = LoggerFactory.getLogger(NotificationReceiversTest.class);

    @Test
    public void testReceiveSourceNotification() {
       Response response =  given().when().post("/receive/?startTimestamp=1581392859&endTimestamp=1581393459")
          .andReturn();

       logger.info("****");
       logger.info(response.getBody().prettyPrint());
       logger.info("****");
    }
}