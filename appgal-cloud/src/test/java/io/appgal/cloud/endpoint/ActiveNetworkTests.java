package io.appgal.cloud.endpoint;

import com.google.gson.*;

import io.appgal.cloud.model.*;
import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.network.services.DeliveryOrchestrator;
import io.appgal.cloud.network.services.NetworkOrchestrator;
import io.appgal.cloud.app.services.ProfileRegistrationService;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class ActiveNetworkTests {
    private static Logger logger = LoggerFactory.getLogger(ActiveNetworkTests.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Inject
    private ProfileRegistrationService profileRegistrationService;

    @Inject
    private DeliveryOrchestrator deliveryOrchestrator;

    @Inject
    private ActiveNetwork activeNetwork;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Test
    public void testGetActiveView() {
        Response response = given().when().get("/activeNetwork/activeView/")
                .andReturn();

        String json = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(json);
        logger.info("****");

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        assertEquals(200, response.getStatusCode());
        assertEquals(true, jsonObject.get("activeFoodRunners").isJsonArray());
        assertEquals(true, jsonObject.get("activeFoodRunnerQueue").isJsonArray());
        assertEquals(true, jsonObject.get("finderResults").isJsonArray());
        assertEquals(true, jsonObject.get("sourceOrgs").isJsonArray());
    }

    @Test
    public void testEnterNetwork() {
        JsonObject registrationJson = new JsonObject();
        registrationJson.addProperty("id", UUID.randomUUID().toString());
        registrationJson.addProperty("email", "c@s.com");
        registrationJson.addProperty("mobile", "8675309");
        registrationJson.addProperty("photo", "photu");
        registrationJson.addProperty("password", "c");
        registrationJson.addProperty("profileType", ProfileType.FOOD_RUNNER.name());
        given().body(registrationJson.toString()).post("/registration/profile");

        Response response = given().when().post("/activeNetwork/enterNetwork/?email=c@s.com").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        assertEquals(200, response.getStatusCode());
        assertEquals(200, jsonObject.get("statusCode").getAsInt());
    }

    @Test
    public void testEnterNetworkBadRequest() {
        Response response = given().when().post("/activeNetwork/enterNetwork/?email=c@blah.booya.com").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        assertEquals(400, response.getStatusCode());
        assertEquals("c@blah.booya.com", jsonObject.get("email").getAsString());
    }

    @Test
    public void testFindBestDestination() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "CLOUD_ID");
        json.addProperty("email", "blah@blah.com");
        json.addProperty("mobile", "8675309");
        json.addProperty("photo", "photu");

        Response response = given().body(json.toString()).when().post("/activeNetwork/findBestDestination/").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(jsonString);
        logger.info("****");
    }

    @Test
    public void testSendDeliveryNotification() {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        Location location = new Location(30.25860595703125d,-97.74873352050781d);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com",
                "8675309", "", "", ProfileType.FOOD_RUNNER, location);
        FoodRunner foodRunner = new FoodRunner(profile, location);
        DropOffNotification dropOffNotification = new DropOffNotification(sourceOrg, location, foodRunner);
        JsonObject json = JsonParser.parseString(dropOffNotification.toString()).getAsJsonObject();

        Response response = given().body(json.toString()).when().post("/activeNetwork/sendDeliveryNotification/").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(jsonString);
        logger.info("****");
    }

    @Test
    public void testSendPickupRequest() {

        SourceOrg sourceOrg = new SourceOrg("test", "TEST", "testing@test.com");
        for(int i=0; i<2; i++)
        {
            Profile profile = new Profile(UUID.randomUUID().toString(), "test"+i+"@test.com", "8675309", "",
                    "test", ProfileType.ORG, sourceOrg.getOrgId());
            sourceOrg.getProfiles().add(profile);
        }

        this.mongoDBJsonStore.storeSourceOrg(sourceOrg);

        SourceOrg sourceOrg1 = this.mongoDBJsonStore.getSourceOrgs().get(0);
        JsonObject json = sourceOrg1.toJson();
        json.addProperty("orgId", "test");
        json.addProperty("orgName", "TEST");
        json.addProperty("orgContactEmail", "testing@test.com");

        Response response = given().body(json.toString()).when().post("/activeNetwork/pickUpRequest/send/").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(jsonString);
        logger.info("****");
    }

    @Test
    public void testFoodRequestCycle()
    {
        FoodRequest foodRequest = new FoodRequest();
        SourceOrg sourceOrg1 = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        foodRequest.setFoodType(FoodTypes.VEG);
        foodRequest.setSourceOrg(sourceOrg1);
        String jsonBody = foodRequest.toJson().toString();

        Response response = given().body(jsonBody.toString()).when().post("/activeNetwork/sendFoodRequest/").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(jsonString);
        logger.info("****");
        String foodRequestId = JsonParser.parseString(jsonString).getAsJsonObject().get("foodRequestId").getAsString();
        FoodRequest storedRequest = this.deliveryOrchestrator.getFoodRequest(foodRequestId);
        logger.info("****");
        logger.info(this.gson.toJson(storedRequest.toJson()));
    }

    @Test
    public void testSourceOrgs() {
        Response response = given().when().get("/activeNetwork/sourceOrgs/")
                .andReturn();

        String json = response.getBody().prettyPrint();
        //logger.info("****");
        //logger.info(json);
        //logger.info("****");

        //assert the body
        /*JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String statusCode = jsonObject.get("statusCode").getAsString();
        assertEquals("0", statusCode);*/
    }

    @Test
    public void testMatchFoodRunner()
    {
       List<SourceOrg> match = this.activeNetwork.matchFoodRunner(new FoodRunner());

       logger.info("****************");
       logger.info(match.toString());
       logger.info("****************");
    }

    @Test
    public void testSchedulePickUp() {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(0.0d, 0.0d);
        FoodRunner bugsBunny = new FoodRunner(profile, location);
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        long epochSecond = start.toEpochSecond();

        SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification();
        schedulePickUpNotification.setSourceOrg(sourceOrg);
        schedulePickUpNotification.setFoodRunner(bugsBunny);
        schedulePickUpNotification.setStart(start);

        String json = schedulePickUpNotification.toJson().toString();

        Response response = given().body(json).when().post("/activeNetwork/schedulePickUp/")
                .andReturn();

        String responseJson = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(responseJson);
        logger.info("****");
    }
}