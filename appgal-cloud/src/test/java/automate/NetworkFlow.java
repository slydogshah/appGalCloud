package automate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.*;
import io.appgal.cloud.util.JsonUtil;
import io.appgal.cloud.util.MapUtils;
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
public class NetworkFlow {
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

        SourceOrg pickup = this.registerPickupOrg(pickupOrg);
        SourceOrg dropOff = this.registerDropOffOrg(dropOffOrg);

        FoodRunner f1 = this.registerFoodRunner(f1Email);
        FoodRunner f2 = this.registerFoodRunner(f2Email);
        FoodRunner f3 = this.registerFoodRunner(f3Email);

        this.loginFoodRunner(f1.getProfile().getEmail(),
                f1.getProfile().getPassword(),30.2698104d,-97.75115579999999d);
        this.loginFoodRunner(f2.getProfile().getEmail(),
                f2.getProfile().getPassword(),30.2698104d,-97.75115579999999d);
        this.loginFoodRunner(f3.getProfile().getEmail(),
                f3.getProfile().getPassword(),0d,0d);

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

    private SourceOrg registerPickupOrg(String email)
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

    private SourceOrg registerDropOffOrg(String email)
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

    private JsonObject acceptTransaction(String email,String dropOffOrgId,FoodRecoveryTransaction accepted)
    {
        JsonObject json = new JsonObject();
        json.addProperty("email",email);
        json.addProperty("dropOffOrgId",dropOffOrgId);
        json.add("accepted",accepted.toJson());
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
