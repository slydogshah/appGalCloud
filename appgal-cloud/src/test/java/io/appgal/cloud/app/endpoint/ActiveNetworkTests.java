package io.appgal.cloud.app.endpoint;

import com.google.gson.*;

import io.appgal.cloud.model.*;
import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.network.services.NetworkOrchestrator;
import io.appgal.cloud.app.services.ProfileRegistrationService;

import io.appgal.cloud.util.JsonUtil;
import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ActiveNetworkTests extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(ActiveNetworkTests.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Inject
    private ProfileRegistrationService profileRegistrationService;

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
        logger.info(response.asString());
        logger.info("*********");
        assertEquals(200, response.getStatusCode());

        response = given().when().post("/activeNetwork/enterNetwork/?email="+email).andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(response.asString());
        logger.info("****");
        assertEquals(200, response.getStatusCode());
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
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com", true);
        Location location = new Location(30.25860595703125d,-97.74873352050781d);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com",
                8675309l, "", "", ProfileType.FOOD_RUNNER, location);
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

        SourceOrg sourceOrg = new SourceOrg("test", "TEST", "testing@test.com", true);
        for(int i=0; i<2; i++)
        {
            Profile profile = new Profile(UUID.randomUUID().toString(), "test"+i+"@test.com", 8675309l, "",
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
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com", true);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(0.0d, 0.0d);
        FoodRunner bugsBunny = new FoodRunner(profile, location);
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        long epochSecond = start.toEpochSecond();

        SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification(UUID.randomUUID().toString());
        schedulePickUpNotification.setSourceOrg(sourceOrg);
        schedulePickUpNotification.setFoodRunner(bugsBunny);
        schedulePickUpNotification.setStart(start);

        String json = schedulePickUpNotification.toJson().toString();
        logger.info(gson.toJson(schedulePickUpNotification.toJson()));

        Response response = given().body(json).when().post("/activeNetwork/schedulePickUp/")
                .andReturn();

        String responseJson = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(responseJson);
        logger.info("****");
    }

    @Test
    void testFoodPickedUp() throws Exception
    {
        //Register a Pickup Org
        SourceOrg pickup = this.registerPickupOrg();

        //Register a DropOff Org
        SourceOrg dropOff = this.registerDropOffOrg();

        //Register a FoodRunner
        FoodRunner foodRunner = this.registerFoodRunner();

        //Send a PickUpRequest
        String foodPic = IOUtils.toString(Thread.currentThread().getContextClassLoader().
                        getResource("encodedImage"),
                StandardCharsets.UTF_8);
        String pickupNotificationId = this.sendPickUpDetails(pickup.getOrgId(), FoodTypes.VEG.name(), foodPic);
        this.schedulePickup(pickupNotificationId, dropOff.getOrgId(), pickup);

        //Notify a FoodRunner...does not pull my transactions
        this.loginFoodRunner(foodRunner.getProfile().getEmail(), foodRunner.getProfile().getPassword());

        //FoodRunner accepts....this will update to notificationSent=true
        List<FoodRecoveryTransaction> myTransactions = this.getMyTransactions(foodRunner.getProfile().getEmail());
        JsonUtil.print(this.getClass(),JsonParser.parseString(myTransactions.toString()).getAsJsonArray());

        FoodRecoveryTransaction accepted = myTransactions.get(0);
        accepted = this.acceptTransaction(foodRunner.getProfile().getEmail(),dropOff.getOrgId(),accepted);

        List<FoodRecoveryTransaction> orgTransactions = this.getPickedUpTransactions(pickup.getOrgId());
        String txId = orgTransactions.get(0).getId();

        JsonObject json = new JsonObject();
        json.addProperty("txId",txId);
        Response response = given().body(json.toString()).post("/activeNetwork/foodPickedUp");
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());

        response = given().get("/activeNetwork/foodPickedUp/?email="+foodRunner.getProfile().getEmail());
        jsonString = response.getBody().print();
        responseJson = JsonParser.parseString(jsonString);
        JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());

        response = given().get("/tx/recovery/transaction/foodPic/?id="+txId);
        assertEquals(200, response.getStatusCode());

        response = given().get("/tx/recovery/transaction/foodPic/?id=blah");
        logger.info("STATUS: "+response.getStatusCode());
        assertEquals(404, response.getStatusCode());
    }

    private SourceOrg registerPickupOrg()
    {
        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = "pickup@pickup.io";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "password");
        registrationJson.addProperty("orgId", "pickup.io");
        registrationJson.addProperty("orgName", "Pickup Inc");
        registrationJson.addProperty("orgType", true);
        registrationJson.addProperty("orgContactEmail", email);
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("producer", true);
        registrationJson.addProperty("street","506 West Ave");
        registrationJson.addProperty("zip","78701");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        //JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());
        assertTrue(responseJson.getAsJsonObject().get("producer").getAsBoolean());

        return SourceOrg.parse(responseJson.getAsJsonObject().toString());
    }

    private SourceOrg registerDropOffOrg()
    {
        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = "dropoff@dropoff.io";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "password");
        registrationJson.addProperty("orgId", "dropoff.io");
        registrationJson.addProperty("orgName", "DropOff Inc");
        registrationJson.addProperty("orgType", false);
        registrationJson.addProperty("orgContactEmail", email);
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("producer", false);
        registrationJson.addProperty("street","801 West Fifth Street");
        registrationJson.addProperty("zip","78703");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        //JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());
        assertFalse(responseJson.getAsJsonObject().get("producer").getAsBoolean());

        return SourceOrg.parse(responseJson.getAsJsonObject().toString());
    }

    private FoodRunner registerFoodRunner()
    {
        JsonObject json = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = "jen@appgallabs.io";
        json.addProperty("id", id);
        json.addProperty("email", email);
        json.addProperty("password", "password");
        json.addProperty("mobile", "123");
        json.addProperty("profileType", ProfileType.FOOD_RUNNER.name());

        Response response = given().body(json.toString()).post("/registration/profile");
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        //JsonUtil.print(this.getClass(), responseJson);
        //assertEquals(200, response.getStatusCode());

        if(response.statusCode()==409)
        {
            response = given().get("/registration/profile/?email="+email);
            jsonString = response.getBody().print();
            responseJson = JsonParser.parseString(jsonString);
            //JsonUtil.print(this.getClass(), responseJson);
        }

        Profile profile = Profile.parse(jsonString);
        FoodRunner foodRunner = new FoodRunner();
        foodRunner.setProfile(profile);
        return foodRunner;
    }

    private String sendPickUpDetails(String orgId,String foodType,String foodPic)
    {
        JsonObject json = new JsonObject();
        json.addProperty("orgId", orgId);
        json.addProperty("foodType", foodType);
        json.addProperty("foodPic", foodPic);
        json.addProperty("time","0:0");

        Response response = given().body(json.toString()).post("/notification/addPickupDetails/");
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        //JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());

        return responseJson.getAsJsonObject().get("pickupNotificationId").getAsString();
    }

    private void schedulePickup(String pickupNotificationId,String dropOffOrgId,SourceOrg pickupOrg)
    {
        JsonObject json = new JsonObject();
        json.addProperty("pickupNotificationId", pickupNotificationId);
        json.addProperty("dropOffOrgId", dropOffOrgId);
        json.add("sourceOrg", pickupOrg.toJson());

        Response response = given().body(json.toString()).post("/notification/schedulePickup/");
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        logger.info("*************SCHEDULE_PICKUP****************");
        JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());
    }

    private List<FoodRecoveryTransaction> getPickedUpTransactions(String orgId)
    {
        Response response = given().get("/tx/recovery/?orgId="+orgId);
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        //JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());

        JsonArray inProgress = responseJson.getAsJsonObject().get("inProgress").getAsJsonArray();
        List<FoodRecoveryTransaction> myTransactions = new ArrayList<>();
        Iterator<JsonElement> itr = inProgress.iterator();
        while(itr.hasNext())
        {
            FoodRecoveryTransaction cour = FoodRecoveryTransaction.parse(itr.next().getAsJsonObject().toString());
            myTransactions.add(cour);
        }

        return myTransactions;
    }

    private List<FoodRecoveryTransaction> getMyTransactions(String email)
    {
        Response response = given().get("/tx/recovery/foodRunner/?email="+email);
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        //JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());

        JsonArray pending = responseJson.getAsJsonObject().get("pending").getAsJsonArray();
        List<FoodRecoveryTransaction> myTransactions = new ArrayList<>();
        Iterator<JsonElement> itr = pending.iterator();
        while(itr.hasNext())
        {
            FoodRecoveryTransaction cour = FoodRecoveryTransaction.parse(itr.next().getAsJsonObject().toString());
            myTransactions.add(cour);
        }

        return myTransactions;
    }

    private JsonObject loginFoodRunner(String email,String password)
    {
        JsonObject loginJson = new JsonObject();
        loginJson.addProperty("email", email);
        loginJson.addProperty("password", password);
        loginJson.addProperty("latitude", 30.2698104d);
        loginJson.addProperty("longitude",-97.75115579999999);
        Response response = given().header("User-Agent","Dart").body(loginJson.toString()).when().post("/registration/login").andReturn();
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        //JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());
        return responseJson.getAsJsonObject();
    }

    private FoodRecoveryTransaction acceptTransaction(String email,String dropOffOrgId,FoodRecoveryTransaction accepted)
    {
        JsonObject json = new JsonObject();
        json.addProperty("email",email);
        json.addProperty("dropOffOrgId",dropOffOrgId);
        json.addProperty("accepted",accepted.getId());
        Response response = given().body(json.toString()).when().post("/activeNetwork/accept").andReturn();
        String jsonString = response.getBody().print();
        logger.info("**********ACCEPT_RESPONSE*************");
        logger.info(jsonString);
        JsonObject responseJson = JsonParser.parseString(jsonString).getAsJsonObject();
        assertEquals(200, response.getStatusCode());
        String id = responseJson.get("recoveryTransactionId").getAsString();

        response = given().body(json.toString()).when().get("/tx/recovery/transaction?id="+id).andReturn();
        jsonString = response.getBody().print();
        return FoodRecoveryTransaction.parse(jsonString);
    }
}