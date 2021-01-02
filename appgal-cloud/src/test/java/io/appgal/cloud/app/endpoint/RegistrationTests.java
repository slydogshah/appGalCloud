package io.appgal.cloud.app.endpoint;

import com.google.gson.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.ProfileType;
import io.appgal.cloud.model.SourceOrg;
import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class RegistrationTests extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(RegistrationTests.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Test
    public void testRegister() throws Exception{
        JsonObject json = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@blah.com";
        json.addProperty("id", id);
        json.addProperty("email", email);
        json.addProperty("password", "c");
        json.addProperty("mobile", 8675309l);
        json.addProperty("photo", "photu");
        json.addProperty("profileType", ProfileType.FOOD_RUNNER.name());

        logger.info("******NEW_PROFILE******");
        logger.info(json.toString());
        logger.info("***********************");

        Response response = given().body(json.toString()).when().post("/registration/profile").andReturn();
        String jsonString = response.getBody().asString();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");
        assertEquals(200, response.getStatusCode());

        response = given().when().get("/registration/profile?email="+email)
                .andReturn();

        jsonString = response.getBody().asString();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        assertNotNull(jsonObject.get("id").getAsString());
        assertEquals(jsonObject.get("email").getAsString(), email);
        assertEquals(jsonObject.get("password").getAsString(), "c");
        assertEquals(jsonObject.get("mobile").getAsLong(), 8675309l);
        assertEquals(jsonObject.get("photo").getAsString(), "photu");
        assertEquals(jsonObject.get("profileType").getAsString(), "FOOD_RUNNER");
    }

    @Test
    public void testRegisterResourceExists() throws Exception{
        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@blah.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("photo", "photu");
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.FOOD_RUNNER.name());
        Response response = given().body(registrationJson.toString()).post("/registration/profile");
        logger.info("*********");
        logger.info(response.getStatusLine());
        logger.info("*********");
        assertEquals(200, response.getStatusCode());

        response = given().body(registrationJson.toString()).when().post("/registration/profile").andReturn();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info("****");
        assertEquals(409,response.getStatusCode());
    }

    @Test
    public void testRegisterValidation() throws Exception{
        JsonObject json = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@blah";
        json.addProperty("email", email);
        json.addProperty("mobile", "8675309");
        json.addProperty("photo", "photu");
        json.addProperty("profileType", ProfileType.FOOD_RUNNER.name());

        logger.info("******NEW_PROFILE******");
        logger.info(json.toString());
        logger.info("***********************");

        Response response = given().body(json.toString()).when().post("/registration/profile").andReturn();
        String jsonString = response.getBody().asString();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");
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
        String id = UUID.randomUUID().toString();
        String email = id+"@blah.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("photo", "photu");
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.FOOD_RUNNER.name());
        Response response = given().body(registrationJson.toString()).post("/registration/profile");
        logger.info("*********");
        logger.info(response.asString());
        logger.info("*********");
        assertEquals(200, response.getStatusCode());

        JsonObject loginJson = new JsonObject();
        loginJson.addProperty("email", email);
        loginJson.addProperty("password", "c");
        response = given().body(loginJson.toString()).when().post("/registration/login").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        assertEquals(jsonObject.get("latitude").getAsDouble(), 30.25860595703125d);
        assertEquals(jsonObject.get("longitude").getAsDouble(), -97.74873352050781d);
        Profile profile = Profile.parse(jsonObject.get("profile").toString());
        assertNotNull(profile.getId());
        assertEquals(profile.getEmail(), email);
        assertEquals(profile.getMobile(), 8675309l);
        assertEquals(profile.getPassword(), "c");
        assertEquals(profile.getProfileType().name(), "FOOD_RUNNER");
        assertEquals(profile.getLocation().getLatitude(), 30.25860595703125d);
        assertEquals(profile.getLocation().getLongitude(), -97.74873352050781d);

    }

    @Test
    public void testLoginSuccessOrg() {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        this.mongoDBJsonStore.storeSourceOrg(sourceOrg);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@blah.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("photo", "photu");
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("sourceOrgId", sourceOrg.getOrgId());
        registrationJson.addProperty("profileType", ProfileType.ORG.name());

        logger.info("******NEW_ORG******");
        logger.info(registrationJson.toString());
        logger.info("***********************");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        logger.info("*********");
        logger.info(response.getStatusLine());
        logger.info("*********");
        assertEquals(200, response.getStatusCode());

        JsonObject loginJson = new JsonObject();
        loginJson.addProperty("email", email);
        loginJson.addProperty("password", "c");
        response = given().body(loginJson.toString()).when().post("/registration/org/login").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
    }

    @Test
    public void testLoginSuccessUnauthorized() {
        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@blah.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("photo", "photu");
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.FOOD_RUNNER.name());
        Response response = given().body(registrationJson.toString()).post("/registration/profile");
        logger.info("*********");
        logger.info(response.asString());
        logger.info("*********");
        assertEquals(200, response.getStatusCode());

        JsonObject loginJson = new JsonObject();
        loginJson.addProperty("email", email);
        loginJson.addProperty("password", "c");
        response = given().body(loginJson.toString()).when().post("/registration/org/login").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void testLoginSuccessOrgUnauthorized() {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        this.mongoDBJsonStore.storeSourceOrg(sourceOrg);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@blah.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("photo", "photu");
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("sourceOrgId", sourceOrg.getOrgId());
        registrationJson.addProperty("profileType", ProfileType.ORG.name());

        logger.info("******NEW_ORG******");
        logger.info(registrationJson.toString());
        logger.info("***********************");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        logger.info("*********");
        logger.info(response.getStatusLine());
        logger.info("*********");
        assertEquals(200, response.getStatusCode());

        JsonObject loginJson = new JsonObject();
        loginJson.addProperty("email", email);
        loginJson.addProperty("password", "c");
        response = given().body(loginJson.toString()).when().post("/registration/login").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");
        assertEquals(403, response.getStatusCode());
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
    public void testLoginValidationFail() {
        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@blah.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("photo", "photu");
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.FOOD_RUNNER.name());
        Response response = given().body(registrationJson.toString()).post("/registration/profile");
        logger.info("*********");
        logger.info(response.asString());
        logger.info("*********");
        assertEquals(200, response.getStatusCode());

        JsonObject loginJson = new JsonObject();
        loginJson.addProperty("email", email);
        loginJson.addProperty("password", "c");
        response = given().body(loginJson.toString()).when().post("/registration/login").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        assertEquals(jsonObject.get("latitude").getAsDouble(), 30.25860595703125d);
        assertEquals(jsonObject.get("longitude").getAsDouble(), -97.74873352050781d);
        Profile profile = Profile.parse(jsonObject.get("profile").toString());
        assertNotNull(profile.getId());
        assertEquals(profile.getEmail(), email);
        assertEquals(profile.getMobile(), 8675309l);
        assertEquals(profile.getPassword(), "c");
        assertEquals(profile.getProfileType().name(), "FOOD_RUNNER");
        assertEquals(profile.getLocation().getLatitude(), 30.25860595703125d);
        assertEquals(profile.getLocation().getLongitude(), -97.74873352050781d);

    }
}