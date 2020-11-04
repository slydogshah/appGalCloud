package io.appgal.cloud.app.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.ProfileType;
import io.appgal.cloud.model.SourceOrg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class RegistrationTests {
    private static Logger logger = LoggerFactory.getLogger(RegistrationTests.class);

    @Test
    public void testRegister() throws Exception{
        JsonObject json = new JsonObject();
        json.addProperty("id", UUID.randomUUID().toString());
        json.addProperty("email", "blah@blah.com");
        json.addProperty("mobile", "8675309");
        json.addProperty("photo", "photu");
        json.addProperty("profileType", ProfileType.FOOD_RUNNER.name());

        Response response = given().body(json.toString()).when().post("/registration/profile").andReturn();

        String jsonString = response.getBody().asString();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        int statusCode = jsonObject.get("statusCode").getAsInt();
        assertEquals(200, statusCode);

        response = given().when().get("/registration/profile?email=blah@blah.com")
                .andReturn();

        jsonString = response.getBody().asString();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");

        //assert the body
        jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        assertNotNull(jsonObject.get("id").getAsString());
        assertEquals(jsonObject.get("email").getAsString(), "blah@blah.com");
        assertEquals(jsonObject.get("mobile").getAsString(), "8675309");
        assertEquals(jsonObject.get("photo").getAsString(), "photu");
        assertEquals(jsonObject.get("profileType").getAsString(), "FOOD_RUNNER");
    }

    @Test
    public void testRegisterResourceExists() throws Exception{
        JsonObject json = new JsonObject();
        String uuid = UUID.randomUUID().toString();
        json.addProperty("id", uuid);
        json.addProperty("email", "resource.exists."+uuid+"@blah.com");
        json.addProperty("mobile", "8675309");
        json.addProperty("photo", "photu");
        json.addProperty("profileType", ProfileType.FOOD_RUNNER.name());

        given().body(json.toString()).when().post("/registration/profile").andReturn();

        Response response = given().body(json.toString()).when().post("/registration/profile").andReturn();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info("****");
        assertEquals(409,response.getStatusCode());
    }

    @Test
    public void testGetProfileNotFound() throws Exception{
        Response response = given().when().get("/registration/profile?email=xyz")
                .andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        assertEquals(404, response.getStatusCode());
        assertEquals(jsonObject.get("email").getAsString(), "xyz");
        assertEquals(jsonObject.get("message").getAsString(), "profile_not_found");
    }

    @Test
    public void testLoginSuccess() {
        JsonObject registrationJson = new JsonObject();
        registrationJson.addProperty("id", UUID.randomUUID().toString());
        registrationJson.addProperty("email", "c@s.com");
        registrationJson.addProperty("mobile", "8675309");
        registrationJson.addProperty("photo", "photu");
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.FOOD_RUNNER.name());
        given().body(registrationJson.toString()).post("/registration/profile");

        JsonObject loginJson = new JsonObject();
        loginJson.addProperty("email", "c@s.com");
        loginJson.addProperty("password", "c");
        Response response = given().body(loginJson.toString()).when().post("/registration/login").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        int statusCode = jsonObject.get("statusCode").getAsInt();
        assertEquals(200, statusCode);
        assertEquals(jsonObject.get("latitude").getAsDouble(), 30.25860595703125d);
        assertEquals(jsonObject.get("longitude").getAsDouble(), -97.74873352050781d);
        Profile profile = Profile.parse(jsonObject.get("profile").toString());
        assertNotNull(profile.getId());
        assertEquals(profile.getEmail(), "c@s.com");
        assertEquals(profile.getMobile(), "8675309");
        assertEquals(profile.getPassword(), "c");
        assertEquals(profile.getProfileType().name(), "FOOD_RUNNER");
        assertEquals(profile.getLocation().getLatitude(), 30.25860595703125d);
        assertEquals(profile.getLocation().getLongitude(), -97.74873352050781d);

    }

    @Test
    public void testLoginSuccessOrg() {
        JsonObject registrationJson = new JsonObject();
        registrationJson.addProperty("id", UUID.randomUUID().toString());
        registrationJson.addProperty("email", "m@s.com");
        registrationJson.addProperty("mobile", "7675309");
        registrationJson.addProperty("photo", "photu");
        registrationJson.addProperty("password", "s");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        given().body(registrationJson.toString()).post("/registration/profile");

        JsonObject loginJson = new JsonObject();
        loginJson.addProperty("email", "m@s.com");
        loginJson.addProperty("password", "s");
        Response response = given().body(loginJson.toString()).when().post("/registration/login").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        int statusCode = jsonObject.get("statusCode").getAsInt();
        assertEquals(200, statusCode);
        assertEquals(jsonObject.get("latitude").getAsDouble(), 30.25860595703125d);
        assertEquals(jsonObject.get("longitude").getAsDouble(), -97.74873352050781d);
        Profile profile = Profile.parse(jsonObject.get("profile").toString());
        assertNotNull(profile.getId());
        assertEquals(profile.getEmail(), "m@s.com");
        assertEquals(profile.getMobile(), "7675309");
        assertEquals(profile.getPassword(), "s");
        assertEquals(profile.getProfileType().name(), "ORG");
        assertEquals(profile.getLocation().getLatitude(), 30.25860595703125d);
        assertEquals(profile.getLocation().getLongitude(), -97.74873352050781d);
    }

    @Test
    public void testLoginEmpty() {
        JsonObject json = new JsonObject();
        json.addProperty("email", "");
        json.addProperty("password", "");

        Response response = given().body(json.toString()).when().post("/registration/login").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        assertEquals(401, response.getStatusCode());
        assertEquals(jsonObject.get("email").getAsString(), "");
    }

    @Test
    public void testRegisterSourceOrg() throws Exception{
        String uuid = UUID.randomUUID().toString();
        SourceOrg sourceOrg = new SourceOrg("church2/"+uuid, "SUBURB_CHURCH", "suburb.church."+uuid+"@gmail.com");
        JsonObject json = sourceOrg.toJson();

        Response response = given().body(json.toString()).when().post("/registration/profileSourceOrg").andReturn();

        String jsonString = response.getBody().asString();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        int statusCode = jsonObject.get("statusCode").getAsInt();
        assertEquals(200, statusCode);

        /*response = given().when().get("/registration/profile?email=blah@blah.com")
                .andReturn();

        jsonString = response.getBody().asString();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");

        //assert the body
        jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        assertNotNull(jsonObject.get("id").getAsString());
        assertEquals(jsonObject.get("email").getAsString(), "blah@blah.com");
        assertEquals(jsonObject.get("mobile").getAsString(), "8675309");
        assertEquals(jsonObject.get("photo").getAsString(), "photu");
        assertEquals(jsonObject.get("profileType").getAsString(), "FOOD_RUNNER");*/
    }
}