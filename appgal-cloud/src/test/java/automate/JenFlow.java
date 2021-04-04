package automate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.*;
import io.appgal.cloud.network.services.NetworkOrchestrator;
import io.appgal.cloud.util.JsonUtil;
import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class JenFlow extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(JenFlow.class);


    @Test
    public void flow() throws Exception
    {
        //Register a Pickup Org
        this.registerPickupOrg();

        //Register a DropOff Org

        //Register a FoodRunner

        //Send a PickUpRequest

        //Notify a FoodRunner

        //FoodRunner accepts

        //Food Runner notfies DropOff org
    }

    private  void registerPickupOrg()
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
    }
}
