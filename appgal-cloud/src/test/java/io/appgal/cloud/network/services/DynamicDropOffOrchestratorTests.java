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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class DynamicDropOffOrchestratorTests extends BaseTest {
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
        List<FoodRunner> helpers = this.dynamicDropOffOrchestrator.getOfflineDropOffHelpers();
        JsonUtil.print(this.getClass(),JsonParser.parseString(helpers.toString()));
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
}
