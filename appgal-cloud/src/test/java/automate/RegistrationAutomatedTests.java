package automate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.ProfileType;
import io.appgal.cloud.model.SourceOrg;
import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class RegistrationAutomatedTests extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(RegistrationAutomatedTests.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Test
    public void testRegisterAutomation() throws Exception{
        JsonObject json = new JsonObject();
        String id = UUID.randomUUID().toString();
        String email = "b@z.com";
        json.addProperty("id", id);
        json.addProperty("email", email);
        json.addProperty("password", "by");
        json.addProperty("mobile", 8675309l);
        json.addProperty("photo", "photu");
        json.addProperty("profileType", ProfileType.FOOD_RUNNER.name());

        logger.info("******NEW_PROFILE******");
        logger.info(json.toString());
        logger.info("***********************");

        Response response = given().body(json.toString()).when().post("/registration/profile").andReturn();
        String jsonString = response.getBody().asString();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");
        assertEquals(200, response.getStatusCode());

        response = given().when().get("/registration/profile?email="+email)
                .andReturn();

        jsonString = response.getBody().asString();
        logger.info("****");
        logger.info(response.getStatusLine());
        logger.info(jsonString);
        logger.info("****");

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        assertNotNull(jsonObject.get("id").getAsString());
        assertEquals(jsonObject.get("email").getAsString(), email);
        assertEquals(jsonObject.get("password").getAsString(), "by");
        assertEquals(jsonObject.get("mobile").getAsLong(), 8675309l);
        assertEquals(jsonObject.get("photo").getAsString(), "photu");
        assertEquals(jsonObject.get("profileType").getAsString(), "FOOD_RUNNER");
    }
}