package automate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.*;
import io.appgal.cloud.util.JsonUtil;
import io.appgal.cloud.util.MapUtils;
import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class NetworkFlow extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(NetworkFlow.class);

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
    public void flowExclusiveAccept() throws Exception
    {
        String pickupOrg = "pickup@pickup.io";
        String dropOffOrg = "dropoff@dropoff.io";
        String f1Email = "f1@app.io";
        String f2Email = "f2@app.io";
        String f3Email = "f3@app.io";

        SourceOrg pickup = this.registerPickupOrg(pickupOrg,"506 West Ave","78701");
        SourceOrg dropOff = this.registerDropOffOrg(dropOffOrg,"506 West Ave","78701");

        FoodRunner f1 = this.registerFoodRunner(f1Email);
        FoodRunner f2 = this.registerFoodRunner(f2Email);
        FoodRunner f3 = this.registerFoodRunner(f3Email);

        this.loginFoodRunner(f1.getProfile().getEmail(),
                f1.getProfile().getPassword(),30.2698104d,-97.75115579999999d);
        this.loginFoodRunner(f2.getProfile().getEmail(),
                f2.getProfile().getPassword(),30.2698104d,-97.75115579999999d);
        this.loginFoodRunner(f3.getProfile().getEmail(),
                f3.getProfile().getPassword(),30.546084d,-97.873948d);

        //Send a PickupRequest
        for(int i=0; i<1; i++) {
            String foodPic = IOUtils.toString(Thread.currentThread().getContextClassLoader().
                            getResource("encodedImage"),
                    StandardCharsets.UTF_8);
            String pickupNotificationId = this.sendPickUpDetails(pickup.getOrgId(), FoodTypes.VEG.name(), foodPic);
            this.schedulePickup(pickupNotificationId, dropOff.getOrgId(), pickup);
        }

        //Accept a PickupRequest
        List<FoodRecoveryTransaction> f1Transactions = this.getMyTransactions(f1.getProfile().getEmail());
        JsonUtil.print(this.getClass(),JsonParser.parseString(f1Transactions.toString()).getAsJsonArray());
        assertFalse(f1Transactions.isEmpty());

        List<FoodRecoveryTransaction> f2Transactions = this.getMyTransactions(f2.getProfile().getEmail());
        JsonUtil.print(this.getClass(),JsonParser.parseString(f2Transactions.toString()).getAsJsonArray());
        assertFalse(f2Transactions.isEmpty());

        List<FoodRecoveryTransaction> f3Transactions = this.getMyTransactions(f3.getProfile().getEmail());
        JsonUtil.print(this.getClass(),JsonParser.parseString(f3Transactions.toString()).getAsJsonArray());
        assertTrue(f3Transactions.isEmpty());

        //Let f1 accept a request
        FoodRecoveryTransaction accepted = f1Transactions.get(0);
        this.acceptTransaction(f1.getProfile().getEmail(),dropOff.getOrgId(),accepted);

        //Let f2 check and they should not see this request
        f2Transactions = this.getMyTransactions(f2.getProfile().getEmail());
        JsonUtil.print(this.getClass(),JsonParser.parseString(f2Transactions.toString()).getAsJsonArray());
        assertTrue(f2Transactions.isEmpty());

        JsonObject error = this.acceptTransaction(f2.getProfile().getEmail(),dropOff.getOrgId(),accepted);
        JsonUtil.print(this.getClass(),error);
        assertEquals("TRANSACTION_IN_PROGRESS",error.get("exception").getAsString());
    }

    @Test
    public void flowDifferentCities() throws Exception
    {
        String p1Org = "p1@pickup.io";
        String d1Org = "d1@dropoff.io";
        String p2Org = "p2@pickup.io";
        String d2Org = "d2@dropoff.io";
        String f1Email = "f1@app.io";
        String f2Email = "f2@app.io";
        String f3Email = "f3@app.io";
        String f4Email = "f4@app.io";

        SourceOrg p1 = this.registerPickupOrg(p1Org,"506 West Ave","78701");
        SourceOrg d1 = this.registerPickupOrg(d1Org,"506 West Ave","78701");
        SourceOrg p2 = this.registerDropOffOrg(p2Org, "2220 Ambush Canyon","78641");
        SourceOrg d2 = this.registerDropOffOrg(d2Org, "2220 Ambush Canyon","78641");

        FoodRunner f1 = this.registerFoodRunner(f1Email);
        FoodRunner f2 = this.registerFoodRunner(f2Email);
        FoodRunner f3 = this.registerFoodRunner(f3Email);
        FoodRunner f4 = this.registerFoodRunner(f4Email);

        this.loginFoodRunner(f1.getProfile().getEmail(),
                f1.getProfile().getPassword(),30.2698104d,-97.75115579999999d);
        this.loginFoodRunner(f2.getProfile().getEmail(),
                f2.getProfile().getPassword(),30.2698104d,-97.75115579999999d);
        this.loginFoodRunner(f3.getProfile().getEmail(),
                f3.getProfile().getPassword(),30.546084d,-97.873948d);

        //Send a PickupRequest
        for(int i=0; i<1; i++) {
            String foodPic = IOUtils.toString(Thread.currentThread().getContextClassLoader().
                            getResource("encodedImage"),
                    StandardCharsets.UTF_8);
            String pickupNotificationId = this.sendPickUpDetails(p1.getOrgId(), FoodTypes.VEG.name(), foodPic);
            this.schedulePickup(pickupNotificationId, d1.getOrgId(), p1);

            pickupNotificationId = this.sendPickUpDetails(p2.getOrgId(), FoodTypes.VEG.name(), foodPic);
            this.schedulePickup(pickupNotificationId, d2.getOrgId(), p2);
        }

        //Accept a PickupRequest
        List<FoodRecoveryTransaction> f1Transactions = this.getMyTransactions(f1.getProfile().getEmail());
        JsonUtil.print(this.getClass(),JsonParser.parseString(f1Transactions.toString()).getAsJsonArray());
        assertFalse(f1Transactions.isEmpty());

        List<FoodRecoveryTransaction> f2Transactions = this.getMyTransactions(f2.getProfile().getEmail());
        JsonUtil.print(this.getClass(),JsonParser.parseString(f2Transactions.toString()).getAsJsonArray());
        assertFalse(f2Transactions.isEmpty());

        List<FoodRecoveryTransaction> f3Transactions = this.getMyTransactions(f3.getProfile().getEmail());
        JsonUtil.print(this.getClass(),JsonParser.parseString(f3Transactions.toString()).getAsJsonArray());
        assertFalse(f3Transactions.isEmpty());

        //Let f1 accept a request
        FoodRecoveryTransaction accepted = f1Transactions.get(0);
        this.acceptTransaction(f1.getProfile().getEmail(),accepted.getPickUpNotification().getDropOffOrg().getOrgId(),accepted);

        //Let f2 check and they should not see this request
        f2Transactions = this.getMyTransactions(f2.getProfile().getEmail());
        JsonUtil.print(this.getClass(),JsonParser.parseString(f2Transactions.toString()).getAsJsonArray());
        assertTrue(f2Transactions.isEmpty());

        accepted = f3Transactions.get(0);
        this.acceptTransaction(f3.getProfile().getEmail(),accepted.getPickUpNotification().getDropOffOrg().getOrgId(),accepted);
        f3Transactions = this.getMyTransactions(f3.getProfile().getEmail());
        JsonUtil.print(this.getClass(),JsonParser.parseString(f3Transactions.toString()).getAsJsonArray());
        assertTrue(f3Transactions.isEmpty());
    }

    private SourceOrg registerPickupOrg(String email, String street, String zip)
    {
        JsonObject registrationJson = new JsonObject();
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "password");
        registrationJson.addProperty("orgId", "pickup.io");
        registrationJson.addProperty("orgName", "Pickup Inc");
        registrationJson.addProperty("orgType", true);
        registrationJson.addProperty("orgContactEmail", email);
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("producer", true);
        registrationJson.addProperty("street",street);
        registrationJson.addProperty("zip",zip);
        registrationJson.addProperty("timeZone","US/Central");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        //JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());
        assertTrue(responseJson.getAsJsonObject().get("producer").getAsBoolean());

        return SourceOrg.parse(responseJson.getAsJsonObject().toString());
    }

    private SourceOrg registerDropOffOrg(String email, String street, String zip)
    {
        JsonObject registrationJson = new JsonObject();
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "password");
        registrationJson.addProperty("orgId", "dropoff.io");
        registrationJson.addProperty("orgName", "DropOff Inc");
        registrationJson.addProperty("orgType", false);
        registrationJson.addProperty("orgContactEmail", email);
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("producer", false);
        registrationJson.addProperty("street",street);
        registrationJson.addProperty("zip",zip);
        registrationJson.addProperty("timeZone","US/Central");

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        //JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());
        assertFalse(responseJson.getAsJsonObject().get("producer").getAsBoolean());

        return SourceOrg.parse(responseJson.getAsJsonObject().toString());
    }

    private FoodRunner registerFoodRunner(String email)
    {
        JsonObject json = new JsonObject();
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
        //JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());
    }

    private JsonObject loginFoodRunner(String email,String password,double lat,double lon)
    {
        JsonObject loginJson = new JsonObject();
        loginJson.addProperty("email", email);
        loginJson.addProperty("password", password);
        loginJson.addProperty("latitude", lat);
        loginJson.addProperty("longitude",lon);
        Response response = given().header("User-Agent","Dart").body(loginJson.toString()).when().post("/registration/login").andReturn();
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

    private JsonObject acceptTransaction(String email,String dropOffOrgId,FoodRecoveryTransaction accepted)
    {
        JsonObject json = new JsonObject();
        json.addProperty("email",email);
        json.addProperty("dropOffOrgId",dropOffOrgId);
        json.addProperty("accepted",accepted.getId());
        Response response = given().body(json.toString()).when().post("/activeNetwork/accept").andReturn();
        String jsonString = response.getBody().print();
        JsonObject responseJson = JsonParser.parseString(jsonString).getAsJsonObject();
        if(response.statusCode() == 500)
        {
            return JsonParser.parseString(jsonString).getAsJsonObject();
        }
        assertEquals(200, response.getStatusCode());
        String id = responseJson.get("recoveryTransactionId").getAsString();

        response = given().body(json.toString()).when().get("/tx/recovery/transaction?id="+id).andReturn();
        jsonString = response.getBody().print();
        return JsonParser.parseString(jsonString).getAsJsonObject();
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
