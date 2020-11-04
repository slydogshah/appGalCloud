package io.appgal.cloud.app.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class DataMapperTests {
    private static Logger logger = LoggerFactory.getLogger(DataMapperTests.class);

    @Test
    public void testMap() {
        Response response = given().when().post("/dataMapper/map")
                .andReturn();

        String json = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(json);
        logger.info("****");

        //assert the body
        //JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        //String statusCode = jsonObject.get("statusCode").getAsString();

        //assertEquals("1", statusCode);
    }
}