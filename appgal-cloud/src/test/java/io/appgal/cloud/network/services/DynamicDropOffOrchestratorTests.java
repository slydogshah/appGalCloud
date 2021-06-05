package io.appgal.cloud.network.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.app.services.ProfileRegistrationService;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.*;
import io.appgal.cloud.util.JsonUtil;
import io.bugsbunny.test.components.BaseTest;
import io.bugsbunny.test.components.MockData;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class DynamicDropOffOrchestratorTests extends BaseTest{
    private static Logger logger = LoggerFactory.getLogger(DynamicDropOffOrchestratorTests.class);

    @Inject
    private DynamicDropOffOrchestrator dynamicDropOffOrchestrator;

    @Inject
    private ProfileRegistrationService profileRegistrationService;

    private FoodRunner foodRunner;

    @BeforeEach
    public void setUp() throws Exception
    {
        this.foodRunner = MockData.mockFoodRunner();
        this.foodRunner.getProfile().setEmail("jen@appgallabs.io");
        this.profileRegistrationService.register(foodRunner.getProfile());
        this.loginFoodRunner(foodRunner.getProfile().getEmail(),foodRunner.getProfile().getPassword());
    }

    @Test
    public void notifyAvailability() throws Exception
    {
        this.dynamicDropOffOrchestrator.notifyAvailability(this.foodRunner.getProfile().getEmail());
    }

    @Test
    public void getOfflineDropOffHelpers() throws Exception
    {
        this.dynamicDropOffOrchestrator.notifyAvailability(this.foodRunner.getProfile().getEmail());

        //Register a Pickup Org
        SourceOrg pickup = this.registerPickupOrg();

        List<FoodRunner> helpers = this.dynamicDropOffOrchestrator.getOfflineDropOffHelpers(pickup);
        JsonUtil.print(this.getClass(),JsonParser.parseString(helpers.toString()));

        //Send a PickUpRequest
        String foodPic = IOUtils.toString(Thread.currentThread().getContextClassLoader().
                        getResource("encodedImage"),
                StandardCharsets.UTF_8);
        String pickupNotificationId = this.sendPickUpDetails(pickup.getOrgId(), FoodTypes.VEG.name(), foodPic);
        this.schedulePickup(pickupNotificationId, pickup);

        //FoodRunner accepts....this will update to notificationSent=true
        List<FoodRecoveryTransaction> myTransactions = this.getMyTransactions(foodRunner.getProfile().getEmail());
        JsonUtil.print(this.getClass(),JsonParser.parseString(myTransactions.toString()).getAsJsonArray());

        FoodRecoveryTransaction accepted = myTransactions.get(0);
        accepted = this.acceptTransaction(foodRunner.getProfile().getEmail(),accepted);
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

    private void schedulePickup(String pickupNotificationId,SourceOrg pickupOrg)
    {
        JsonObject json = new JsonObject();
        json.addProperty("pickupNotificationId", pickupNotificationId);
        json.addProperty("enableOfflineCommunitySupport", true);
        json.add("sourceOrg", pickupOrg.toJson());

        Response response = given().body(json.toString()).post("/notification/schedulePickup/");
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        logger.info("*************SCHEDULE_PICKUP****************");
        JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());
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

    private FoodRecoveryTransaction acceptTransaction(String email,FoodRecoveryTransaction accepted)
    {
        JsonObject json = new JsonObject();
        json.addProperty("email",email);
        json.addProperty("enableOfflineCommunitySupport",true);
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
