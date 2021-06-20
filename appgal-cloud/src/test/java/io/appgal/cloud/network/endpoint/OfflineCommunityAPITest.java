package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.*;
import io.appgal.cloud.network.services.LocationService;
import io.appgal.cloud.network.services.NetworkOrchestrator;
import io.appgal.cloud.util.JsonUtil;
import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class OfflineCommunityAPITest extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(OfflineCommunityAPITest.class);

    private FoodRunner foodRunner;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @BeforeEach
    public void setUp()
    {
        this.foodRunner = this.registerFoodRunner();
        JsonObject loginRunner = this.loginFoodRunner(foodRunner.getProfile().getEmail(),
                foodRunner.getProfile().getPassword());
    }

    @AfterEach
    public void tearDown()
    {
        this.foodRunner = null;
    }

    @Test
    public void notifyAvailability() throws Exception
    {
        JsonObject payload = new JsonObject();
        payload.addProperty("foodRunnerEmail","jen@appgallabs.io");
        payload.addProperty("available",true);

        Response response = given().body(payload.toString()).post("/offline/notification/").andReturn();
        JsonUtil.print(this.getClass(), JsonParser.parseString(response.getBody().prettyPrint()));

        response = given().when().get("/activeNetwork/activeView/")
                .andReturn();
        JsonUtil.print(this.getClass(), JsonParser.parseString(response.getBody().prettyPrint()));

        Profile profile = this.mongoDBJsonStore.getProfile(foodRunner.getProfile().getEmail());

        JsonObject loginRunner = this.loginFoodRunner(foodRunner.getProfile().getEmail(),
                foodRunner.getProfile().getPassword());
        JsonUtil.print(this.getClass(),loginRunner);
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

    private JsonObject loginFoodRunner(String email,String password)
    {
        JsonObject loginJson = new JsonObject();
        loginJson.addProperty("email", email);
        loginJson.addProperty("password", "password");
        loginJson.addProperty("latitude", 30.2698104d);
        loginJson.addProperty("longitude",-97.75115579999999);
        Response response = given().header("User-Agent","Dart").body(loginJson.toString()).when().post("/registration/login").andReturn();
        String jsonString = response.getBody().print();
        JsonElement responseJson = JsonParser.parseString(jsonString);
        //JsonUtil.print(this.getClass(), responseJson);
        assertEquals(200, response.getStatusCode());
        return responseJson.getAsJsonObject();
    }
}