package automate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.*;
import io.appgal.cloud.util.JsonUtil;

import io.appgal.cloud.util.MapUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class JenFlow {
    private static Logger logger = LoggerFactory.getLogger(JenFlow.class);

    @Inject
    private MapUtils mapUtils;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @BeforeEach
    public void setup() throws Exception
    {
        try {
            if (this.mongoDBJsonStore == null) {
                this.mongoDBJsonStore = new MongoDBJsonStore();
            }
            this.mongoDBJsonStore.start();
            this.mongoDBJsonStore.getMongoClient().getDatabase("jennetwork").drop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testImages() throws Exception
    {
        this.halfFlow();
        SourceOrg pickup = this.mongoDBJsonStore.getSourceOrg("pickup.io");
        SourceOrg dropOff = this.mongoDBJsonStore.getSourceOrg("dropoff.io");

        //Send a PickUpRequest
        String foodPic = IOUtils.toString(Thread.currentThread().getContextClassLoader().
                        getResource("encodedImage"),
                StandardCharsets.UTF_8);
        String pickupNotificationId = this.sendPickUpDetails(pickup.getOrgId(),FoodTypes.VEG.name(),foodPic);
        this.schedulePickup(pickupNotificationId, dropOff.getOrgId(), pickup);
    }

    @Test
    public void halfFlow() throws Exception
    {
        //Register a Pickup Org
        SourceOrg pickup = this.registerPickupOrg();

        //Register a DropOff Org
        SourceOrg dropOff = this.registerDropOffOrg();

        //Register a FoodRunner
        FoodRunner foodRunner = this.registerFoodRunner();

        //Send a PickUpRequest
        for(int i=0; i<20; i++) {
            String foodPic = IOUtils.toString(Thread.currentThread().getContextClassLoader().
                            getResource("encodedImage"),
                    StandardCharsets.UTF_8);
            String pickupNotificationId = this.sendPickUpDetails(pickup.getOrgId(), FoodTypes.VEG.name(), foodPic);
            this.schedulePickup(pickupNotificationId, dropOff.getOrgId(), pickup);
        }

        //Notify a FoodRunner...does not pull my transactions
        JsonObject loginRunner = this.loginFoodRunner(foodRunner.getProfile().getEmail(),
                foodRunner.getProfile().getPassword());

        //FoodRunner accepts....this will update to notificationSent=true
        //List<FoodRecoveryTransaction> myTransactions = this.getMyTransactions(foodRunner.getProfile().getEmail());

        //FoodRecoveryTransaction accepted = myTransactions.get(0);
        //this.acceptTransaction(foodRunner.getProfile().getEmail(),dropOff.getOrgId(),accepted);

        //FoodRunner notifies DropOffOrg
    }

    @Test
    public void fullFlow() throws Exception
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
        String pickupNotificationId = this.sendPickUpDetails(pickup.getOrgId(),FoodTypes.VEG.name(),foodPic);
        this.schedulePickup(pickupNotificationId, dropOff.getOrgId(), pickup);

        //Notify a FoodRunner...does not pull my transactions
        JsonObject loginRunner = this.loginFoodRunner(foodRunner.getProfile().getEmail(), foodRunner.getProfile().getPassword());

        //FoodRunner accepts....this will update to notificationSent=true
        List<FoodRecoveryTransaction> myTransactions = this.getMyTransactions(foodRunner.getProfile().getEmail());
        JsonUtil.print(this.getClass(),JsonParser.parseString(myTransactions.toString()).getAsJsonArray());

        FoodRecoveryTransaction accepted = myTransactions.get(0);
        accepted = this.acceptTransaction(foodRunner.getProfile().getEmail(),dropOff.getOrgId(),accepted);

        //FoodRunner notifies DropOffOrg
        this.scheduleDropOff(accepted);

        //FoodRunner notifies deliver
        this.notifyDelivery(accepted);
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
        assertTrue(responseJson.getAsJsonObject().get("success").getAsJsonObject().get("producer").getAsBoolean());

        return SourceOrg.parse(responseJson.getAsJsonObject().get("success").getAsJsonObject().toString());
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
        assertFalse(responseJson.getAsJsonObject().get("success").getAsJsonObject().get("producer").getAsBoolean());

        return SourceOrg.parse(responseJson.getAsJsonObject().get("success").getAsJsonObject().toString());
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
        //JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());
    }

    private JsonObject loginFoodRunner(String email,String password)
    {
        JsonObject loginJson = new JsonObject();
        loginJson.addProperty("email", email);
        loginJson.addProperty("password", "password");
        loginJson.addProperty("latitude", 30.2698104d);
        loginJson.addProperty("longitude",-97.75115579999999);
        Response response = given().body(loginJson.toString()).when().post("/registration/login").andReturn();
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        //JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());
        return responseJson.getAsJsonObject();
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

    private FoodRecoveryTransaction acceptTransaction(String email,String dropOffOrgId,FoodRecoveryTransaction accepted)
    {
        JsonObject json = new JsonObject();
        json.addProperty("email",email);
        json.addProperty("dropOffOrgId",dropOffOrgId);
        json.add("accepted",accepted.toJson());
        Response response = given().body(json.toString()).when().post("/activeNetwork/accept").andReturn();
        String jsonString = response.getBody().print();
        JsonObject responseJson = JsonParser.parseString(jsonString).getAsJsonObject();
        assertEquals(200, response.getStatusCode());
        String id = responseJson.get("recoveryTransactionId").getAsString();

        response = given().body(json.toString()).when().get("/tx/recovery/transaction?id="+id).andReturn();
        jsonString = response.getBody().print();
        return FoodRecoveryTransaction.parse(jsonString);
    }

    private void scheduleDropOff(FoodRecoveryTransaction accepted)
    {
        Response response = given().body(accepted.toString()).when().post("/activeNetwork/scheduleDropOff").andReturn();
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        assertEquals(200, response.getStatusCode());
    }

    private void notifyDelivery(FoodRecoveryTransaction accepted)
    {
        Response response = given().body(accepted.toString()).when().post("/activeNetwork/notifyDelivery").andReturn();
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        assertEquals(200, response.getStatusCode());
    }
}
