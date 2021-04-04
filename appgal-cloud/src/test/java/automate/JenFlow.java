package automate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.appgal.cloud.model.*;
import io.appgal.cloud.util.JsonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class JenFlow {
    private static Logger logger = LoggerFactory.getLogger(JenFlow.class);


    @Test
    public void flow() throws Exception
    {
        //Register a Pickup Org
        SourceOrg pickup = this.registerPickupOrg();

        //Register a DropOff Org
        SourceOrg dropOff = this.registerDropOffOrg();

        //Register a FoodRunner
        FoodRunner foodRunner = this.registerFoodRunner();

        //Send a PickUpRequest
        String pickupNotificationId = this.sendPickUpDetails(pickup.getOrgId(),FoodTypes.VEG.name(),"");
        this.schedulePickup(pickupNotificationId, dropOff.getOrgId(), pickup);

        //Notify a FoodRunner
        JsonObject loginRunner = this.loginFoodRunner(foodRunner.getProfile().getEmail(),
                foodRunner.getProfile().getPassword());
        JsonUtil.print(this.getClass(),loginRunner);

        //FoodRunner accepts

        //Food Runner notfies DropOff org
    }

    private SourceOrg registerPickupOrg()
    {
        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@blah.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "password");
        registrationJson.addProperty("orgId", "blah.com");
        registrationJson.addProperty("orgName", "blah.com");
        registrationJson.addProperty("orgType", true);
        registrationJson.addProperty("orgContactEmail", email);
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("producer", true);

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());
        assertTrue(responseJson.getAsJsonObject().get("success").getAsJsonObject().get("producer").getAsBoolean());

        return SourceOrg.parse(responseJson.getAsJsonObject().get("success").getAsJsonObject().toString());
    }

    private SourceOrg registerDropOffOrg()
    {
        JsonObject registrationJson = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = id+"@church.com";
        registrationJson.addProperty("email", email);
        registrationJson.addProperty("mobile", 8675309l);
        registrationJson.addProperty("password", "password");
        registrationJson.addProperty("orgId", "church.com");
        registrationJson.addProperty("orgName", "church.com");
        registrationJson.addProperty("orgType", false);
        registrationJson.addProperty("orgContactEmail", email);
        registrationJson.addProperty("profileType", ProfileType.ORG.name());
        registrationJson.addProperty("producer", false);

        Response response = given().body(registrationJson.toString()).post("/registration/org");
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        JsonUtil.print(this.getClass(), responseJson);
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
        JsonUtil.print(this.getClass(), responseJson);
        //assertEquals(200, response.getStatusCode());

        if(response.statusCode()==409)
        {
            response = given().get("/registration/profile/?email="+email);
            jsonString = response.getBody().print();
            responseJson = JsonParser.parseString(jsonString);
            JsonUtil.print(this.getClass(), responseJson);
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
        JsonUtil.print(this.getClass(), responseJson);
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
        JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());
    }

    private JsonObject loginFoodRunner(String email,String password)
    {
        JsonObject loginJson = new JsonObject();
        loginJson.addProperty("email", email);
        loginJson.addProperty("password", "password");
        Response response = given().body(loginJson.toString()).when().post("/registration/login").andReturn();
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());
        return responseJson.getAsJsonObject();
    }
}
