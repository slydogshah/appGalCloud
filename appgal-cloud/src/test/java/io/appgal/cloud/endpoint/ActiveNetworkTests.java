package io.appgal.cloud.endpoint;

import com.google.gson.JsonObject;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class ActiveNetworkTests {
    private static Logger logger = LoggerFactory.getLogger(ActiveNetworkTests.class);

    @Test
    public void testGetActiveView() {
        Response response = given().when().get("/activeNetwork/activeView/")
                .andReturn();

        String json = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(json);
        logger.info("****");

        //assert the body
        /*JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String statusCode = jsonObject.get("statusCode").getAsString();
        assertEquals("0", statusCode);*/
    }

    @Test
    public void testEnterNetwork() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "CLOUD_ID");
        json.addProperty("email", "blah@blah.com");
        json.addProperty("mobile", "8675309");
        json.addProperty("photo", "photu");

        Response response = given().body(json.toString()).when().post("/activeNetwork/enterNetwork/").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(jsonString);
        logger.info("****");
    }
}