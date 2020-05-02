package io.appgal.cloud.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class RegistrationTests {
    private static Logger logger = LoggerFactory.getLogger(RegistrationTests.class);

    @Test
    public void testGetRegistration() {
        Response response = given().when().get("/registration/profile")
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
    public void testRegister() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "CLOUD_ID");
        json.addProperty("email", "blah@blah.com");
        json.addProperty("mobile", "8675309");
        json.addProperty("photo", "photu");

        Response response = given().body(json.toString()).when().post("/registration/profile").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(jsonString);
        logger.info("****");

        //assert the body
        /*JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String statusCode = jsonObject.get("statusCode").getAsString();
        assertEquals("0", statusCode);*/
    }

    @Test
    public void testLoginSuccess() {
        JsonObject registrationJson = new JsonObject();
        registrationJson.addProperty("id", UUID.randomUUID().toString());
        registrationJson.addProperty("email", "blah@blah.com");
        registrationJson.addProperty("mobile", "8675309");
        registrationJson.addProperty("photo", "photu");
        registrationJson.addProperty("password", "blahblah");
        given().body(registrationJson.toString()).post("/registration/profile");

        JsonObject loginJson = new JsonObject();
        loginJson.addProperty("email", "blah@blah.com");
        loginJson.addProperty("password", "blahblah");
        Response response = given().body(loginJson.toString()).when().post("/registration/login").andReturn();

        String json = response.getBody().prettyPrint();

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String statusCode = jsonObject.get("statusCode").getAsString();
        assertEquals("200", statusCode);
    }

    @Test
    public void testLoginEmpty() {
        JsonObject json = new JsonObject();
        json.addProperty("email", "");
        json.addProperty("password", "");

        Response response = given().body(json.toString()).when().post("/registration/login").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(jsonString);
        logger.info("****");

        //assert the body
        /*JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String statusCode = jsonObject.get("statusCode").getAsString();
        assertEquals("0", statusCode);*/
    }
}