package io.appgal.cloud.app.endpoint;

import com.google.gson.*;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.ProfileType;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.util.JsonUtil;
import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.appgal.cloud.infrastructure.MongoDBJsonStore;

@QuarkusTest
public class RegistrationTests  extends BaseTest {
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
        json.addProperty("mobile", "ui");
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
        JsonObject json = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@blah.com";
        json.addProperty("id", id);
        json.addProperty("email", email);
        json.addProperty("password", "password");
        json.addProperty("mobile", "123");
        json.addProperty("profileType", ProfileType.FOOD_RUNNER.name());

        Response response = given().body(json.toString()).post("/registration/profile");
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());

        JsonObject loginJson = new JsonObject();
        loginJson.addProperty("email", email);
        loginJson.addProperty("password", "password");
        response = given().header("User-Agent","Dart").body(loginJson.toString()).when().post("/registration/login").andReturn();

        jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        JsonUtil.print(this.getClass(),JsonParser.parseString(jsonString));
        logger.info("****");

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject().get("profile").getAsJsonObject();
        Profile profile = Profile.parse(jsonObject.toString());
        assertNotNull(profile.getId());
        assertEquals(profile.getEmail(), email);
        assertEquals(profile.getMobile(), 123);
        assertEquals(profile.getPassword(), "password");
        assertEquals(profile.getProfileType().name(), "FOOD_RUNNER");

    }

    @Test
    public void testRegisterOrgSuccess() {
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());
        registrationJson.addProperty("timeZone","US/Central");
        registrationJson.addProperty("street","801 West 5th Street");
        registrationJson.addProperty("zip","78703");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        assertEquals(200, response.getStatusCode());
        response.getBody().prettyPrint();

        //TODO: assert
    }

    @Test
    public void testRegisterOrgDuplicate() {
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());
        registrationJson.addProperty("timeZone","US/Central");
        registrationJson.addProperty("street","801 West 5th Street");
        registrationJson.addProperty("zip","78703");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        response.getBody().prettyPrint();
        assertEquals(200, response.getStatusCode());
        //TODO: assert

        response = given().body(registrationJson.toString()).post("/registration/org");
        response.getBody().prettyPrint();
        assertEquals(409, response.getStatusCode());
        //TODO: assert
    }

    @Test
    public void testRegisterOrgDuplicateDifferentOrg() {
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());
        registrationJson.addProperty("timeZone","US/Central");
        registrationJson.addProperty("street","801 West 5th Street");
        registrationJson.addProperty("zip","78703");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        response.getBody().prettyPrint();
        assertEquals(200, response.getStatusCode());

        registrationJson.addProperty("orgId", "blah");
        response = given().body(registrationJson.toString()).post("/registration/org");
        response.getBody().prettyPrint();
        assertEquals(409, response.getStatusCode());

        //TODO: assert
    }

    @Test
    public void testRegisterOrgValidationFails() {
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        //registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        //registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());
        registrationJson.addProperty("timeZone","  ");
        registrationJson.addProperty("street","  ");
        registrationJson.addProperty("zip","      ");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        response.getBody().prettyPrint();
        assertEquals(200, response.getStatusCode());
        //TODO assert
    }

    @Test
    public void testLoginSuccessOrg() {
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());


        logger.info("******NEW_ORG******");
        logger.info(registrationJson.toString());
        logger.info("***********************");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        JsonUtil.print(this.getClass(),sourceOrg.toJson());
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
        assertEquals(200, response.getStatusCode());

        //assert the body
        //JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
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
        response = given().header("User-Agent","Dart").body(loginJson.toString()).when().post("/registration/login").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        JsonUtil.print(this.getClass(),JsonParser.parseString(jsonString));
        logger.info("****");

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject().get("profile").getAsJsonObject();
        Profile profile = Profile.parse(jsonObject.toString());
        assertNotNull(profile.getId());
        assertEquals(profile.getEmail(), email);
        assertEquals(profile.getMobile(), 8675309l);
        assertEquals(profile.getPassword(), "c");
        assertEquals(profile.getProfileType().name(), "FOOD_RUNNER");
    }

    @Test
    public void testSendResetCode() throws Exception{
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());


        logger.info("******NEW_ORG******");
        logger.info(registrationJson.toString());
        logger.info("***********************");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        JsonUtil.print(this.getClass(),sourceOrg.toJson());
        assertEquals(200, response.getStatusCode());

        JsonObject json = new JsonObject();
        json.addProperty("email",email);
        json.addProperty("mobileNumber","+15129151162");
        response = given().body(json.toString()).when().post("/registration/sendResetCode").andReturn();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(response.body().prettyPrint());
        logger.info("****");
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testSendResetCodeNotRegistered() throws Exception{
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());


        logger.info("******NEW_ORG******");
        logger.info(registrationJson.toString());
        logger.info("***********************");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        JsonUtil.print(this.getClass(),sourceOrg.toJson());
        assertEquals(200, response.getStatusCode());

        JsonObject json = new JsonObject();
        json.addProperty("email","random@random.com");
        json.addProperty("mobileNumber","+15129151162");
        response = given().body(json.toString()).when().post("/registration/sendResetCode").andReturn();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(response.body().prettyPrint());
        logger.info("****");
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void testSendResetCodeFoodRunnerNotAllowed() throws Exception{
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

        json = new JsonObject();
        json.addProperty("email",email);
        json.addProperty("mobileNumber","+15129151162");
        response = given().body(json.toString()).when().post("/registration/sendResetCode").andReturn();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(response.body().prettyPrint());
        logger.info("****");
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void testVerifyResetCodeInvalid() throws Exception{
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());


        logger.info("******NEW_ORG******");
        logger.info(registrationJson.toString());
        logger.info("***********************");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        JsonUtil.print(this.getClass(),sourceOrg.toJson());
        assertEquals(200, response.getStatusCode());

        JsonObject json = new JsonObject();
        json.addProperty("email",email);
        json.addProperty("mobileNumber","+15129151162");
        response = given().body(json.toString()).when().post("/registration/sendResetCode").andReturn();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(response.body().prettyPrint());
        logger.info("****");
        assertEquals(200, response.getStatusCode());

        json = new JsonObject();
        json.addProperty("email",email);
        json.addProperty("resetCode","123456");
        response = given().body(json.toString()).when().post("/registration/verifyResetCode").andReturn();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(response.body().prettyPrint());
        logger.info("****");
        assertEquals(401, response.getStatusCode());
    }

    @Test
    public void testVerifyResetCodeValid() throws Exception{
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());


        logger.info("******NEW_ORG******");
        logger.info(registrationJson.toString());
        logger.info("***********************");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        JsonUtil.print(this.getClass(),sourceOrg.toJson());
        assertEquals(200, response.getStatusCode());

        JsonObject json = new JsonObject();
        json.addProperty("email",email);
        json.addProperty("mobileNumber","+15129151162");
        response = given().body(json.toString()).when().post("/registration/sendResetCode").andReturn();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(response.body().prettyPrint());
        logger.info("****");
        assertEquals(200, response.getStatusCode());

        Profile profile = this.mongoDBJsonStore.getProfile(email);
        json = new JsonObject();
        json.addProperty("email",email);
        json.addProperty("resetCode",profile.getResetCode());
        response = given().body(json.toString()).when().post("/registration/verifyResetCode").andReturn();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(response.body().prettyPrint());
        logger.info("****");
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testResetPasswordSuccess() throws Exception{
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());


        logger.info("******NEW_ORG******");
        logger.info(registrationJson.toString());
        logger.info("***********************");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        JsonUtil.print(this.getClass(),sourceOrg.toJson());
        assertEquals(200, response.getStatusCode());

        JsonObject json = new JsonObject();
        json.addProperty("email",email);
        json.addProperty("newPassword","blah");
        json.addProperty("confirmNewPassword","blah");
        response = given().body(json.toString()).when().post("/registration/resetPassword").andReturn();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(response.body().prettyPrint());
        logger.info("****");
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testResetPasswordFail() throws Exception{
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());


        logger.info("******NEW_ORG******");
        logger.info(registrationJson.toString());
        logger.info("***********************");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        JsonUtil.print(this.getClass(),sourceOrg.toJson());
        assertEquals(200, response.getStatusCode());

        JsonObject json = new JsonObject();
        json.addProperty("email",email);
        json.addProperty("newPassword","blah");
        json.addProperty("confirmNewPassword","blahb");
        response = given().body(json.toString()).when().post("/registration/resetPassword").andReturn();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(response.body().prettyPrint());
        logger.info("****");
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void registerStaff() throws Exception{
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());
        registrationJson.addProperty("street","506 West Ave");
        registrationJson.addProperty("zip","78701");
        registrationJson.addProperty("timeZone","US/Central");


        logger.info("******NEW_ORG******");
        logger.info(registrationJson.toString());
        logger.info("***********************");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        JsonUtil.print(this.getClass(),sourceOrg.toJson());
        assertEquals(200, response.getStatusCode());


        response = given().body(registrationJson.toString()).get("/registration/profile/?email="+email);
        response.getBody().prettyPrint();
        assertEquals(200, response.getStatusCode());




        JsonObject json = new JsonObject();
        String staffEmail = id+".staff@microsoft.com";
        json.addProperty("email", staffEmail);
        json.addProperty("password", "c");
        json.addProperty("orgId",sourceOrg.getOrgId());
        json.addProperty("caller",email);


        response = given().body(json.toString()).post("/registration/staff");
        JsonUtil.print(this.getClass(),JsonParser.parseString(response.getBody().print()));
        assertEquals(200, response.getStatusCode());

        response = given().get("/registration/staff/?orgId="+sourceOrg.getOrgId());
        JsonUtil.print(this.getClass(),JsonParser.parseString(response.getBody().print()));
    }

    @Test
    public void registerStaffResourceExists() throws Exception{
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());
        registrationJson.addProperty("street","506 West Ave");
        registrationJson.addProperty("zip","78701");
        registrationJson.addProperty("timeZone","US/Central");


        logger.info("******NEW_ORG******");
        logger.info(registrationJson.toString());
        logger.info("***********************");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        JsonUtil.print(this.getClass(),sourceOrg.toJson());
        assertEquals(200, response.getStatusCode());




        JsonObject json = new JsonObject();
        String staffEmail = id+".staff@microsoft.com";
        json.addProperty("email", staffEmail);
        json.addProperty("password", "c");
        json.addProperty("orgId",sourceOrg.getOrgId());
        json.addProperty("caller",email);


        response = given().body(json.toString()).post("/registration/staff");
        JsonUtil.print(this.getClass(),JsonParser.parseString(response.getBody().print()));
        assertEquals(200, response.getStatusCode());

        response = given().body(json.toString()).post("/registration/staff");
        JsonUtil.print(this.getClass(),JsonParser.parseString(response.getBody().print()));
        assertEquals(409, response.getStatusCode());
    }

    @Test
    public void registerStaffValidationError() throws Exception{
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());
        registrationJson.addProperty("street","506 West Ave");
        registrationJson.addProperty("zip","78701");
        registrationJson.addProperty("timeZone","US/Central");


        logger.info("******NEW_ORG******");
        logger.info(registrationJson.toString());
        logger.info("***********************");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        JsonUtil.print(this.getClass(),sourceOrg.toJson());
        assertEquals(200, response.getStatusCode());


        response = given().body(registrationJson.toString()).get("/registration/profile/?email="+email);
        response.getBody().prettyPrint();
        assertEquals(200, response.getStatusCode());



        JsonObject json = new JsonObject();
        json.addProperty("orgId",sourceOrg.getOrgId());
        json.addProperty("caller",email);
        response = given().body(json.toString()).post("/registration/staff");
        response.getBody().prettyPrint();
        assertEquals(400, response.getStatusCode());

        json = new JsonObject();
        json.addProperty("email","test");
        json.addProperty("password","test");
        json.addProperty("orgId",sourceOrg.getOrgId());
        json.addProperty("caller",email);
        response = given().body(json.toString()).post("/registration/staff");
        response.getBody().prettyPrint();
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void registerStaffOrgNotFound() throws Exception{
        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";


        JsonObject json = new JsonObject();
        String staffEmail = id+".staff@microsoft.com";
        json.addProperty("email", staffEmail);
        json.addProperty("password", "c");
        json.addProperty("orgId",id);
        json.addProperty("caller",email);


        Response response = given().body(json.toString()).post("/registration/staff");
        JsonUtil.print(this.getClass(),JsonParser.parseString(response.getBody().print()));
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void deleteStaff() throws Exception{
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());
        registrationJson.addProperty("street","506 West Ave");
        registrationJson.addProperty("zip","78701");
        registrationJson.addProperty("timeZone","US/Central");


        logger.info("******NEW_ORG******");
        logger.info(registrationJson.toString());
        logger.info("***********************");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        JsonUtil.print(this.getClass(),sourceOrg.toJson());
        assertEquals(200, response.getStatusCode());
        response = given().body(registrationJson.toString()).get("/registration/profile/?email="+email);
        response.getBody().prettyPrint();
        assertEquals(200, response.getStatusCode());


        JsonObject json = new JsonObject();
        String staffEmail = id+".staff@microsoft.com";
        json.addProperty("email", staffEmail);
        json.addProperty("password", "c");
        json.addProperty("orgId",sourceOrg.getOrgId());
        json.addProperty("caller",email);
        response = given().body(json.toString()).post("/registration/staff");
        JsonUtil.print(this.getClass(),JsonParser.parseString(response.getBody().print()));
        assertEquals(200, response.getStatusCode());

        for(int i=0; i<3; i++) {
            json = new JsonObject();
            json.addProperty("email", staffEmail);
            json.addProperty("orgId", sourceOrg.getOrgId());
            json.addProperty("caller", email);
            response = given().body(json.toString()).post("/registration/deleteStaff");
            response.getBody().prettyPrint();
            assertEquals(200, response.getStatusCode());
        }
    }

    @Test
    public void deleteStaffOrgNotFound() throws Exception{
        SourceOrg sourceOrg = new SourceOrg();
        sourceOrg.setOrgId("Microsoft");
        sourceOrg.setOrgName("Microsoft");
        sourceOrg.setOrgContactEmail("sly.dog.shah@gmail.com");
        sourceOrg.setProducer(true);

        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@microsoft.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("orgName",sourceOrg.getOrgName());
        registrationJson.addProperty("orgId",sourceOrg.getOrgName());
        registrationJson.addProperty("orgContactEmail",sourceOrg.getOrgContactEmail());
        registrationJson.addProperty("producer",sourceOrg.isProducer());
        registrationJson.addProperty("street","506 West Ave");
        registrationJson.addProperty("zip","78701");
        registrationJson.addProperty("timeZone","US/Central");


        logger.info("******NEW_ORG******");
        logger.info(registrationJson.toString());
        logger.info("***********************");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        JsonUtil.print(this.getClass(),sourceOrg.toJson());
        assertEquals(200, response.getStatusCode());
        response = given().body(registrationJson.toString()).get("/registration/profile/?email="+email);
        response.getBody().prettyPrint();
        assertEquals(200, response.getStatusCode());


        JsonObject json = new JsonObject();
        String staffEmail = id+".staff@microsoft.com";
        json.addProperty("email", staffEmail);
        json.addProperty("password", "c");
        json.addProperty("orgId",sourceOrg.getOrgId());
        json.addProperty("caller",email);
        response = given().body(json.toString()).post("/registration/staff");
        JsonUtil.print(this.getClass(),JsonParser.parseString(response.getBody().print()));
        assertEquals(200, response.getStatusCode());

        json = new JsonObject();
        json.addProperty("email", staffEmail);
        json.addProperty("orgId", "blah");
        json.addProperty("caller", email);
        response = given().body(json.toString()).post("/registration/deleteStaff");
        response.getBody().prettyPrint();
        assertEquals(500, response.getStatusCode());
    }
}