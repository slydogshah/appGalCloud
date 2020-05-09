package io.appgal.cloud.endpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.*;
import io.appgal.cloud.persistence.MongoDBJsonStore;
import io.appgal.cloud.services.NetworkOrchestrator;
import io.appgal.cloud.services.ProfileRegistrationService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class ActiveNetworkTests {
    private static Logger logger = LoggerFactory.getLogger(ActiveNetworkTests.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Inject
    private ProfileRegistrationService profileRegistrationService;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Test
    public void testGetActiveView() {
        Response response = given().when().get("/activeNetwork/activeView/")
                .andReturn();

        String json = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(json);
        logger.info("****");

        //assert the body
        /*JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String statusCode = jsonObject.get("statusCode").getAsString();
        assertEquals("0", statusCode);*/
    }

    @Test
    public void testEnterNetwork() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "CLOUD_ID");
        json.addProperty("email", "blah@blah.com");
        json.addProperty("mobile", "8675309");
        json.addProperty("photo", "photu");

        Response response = given().body(json.toString()).when().post("/activeNetwork/enterNetwork/").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(jsonString);
        logger.info("****");
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
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        Location location = new Location(30.25860595703125d,-97.74873352050781d);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "", "");
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

        SourceOrg sourceOrg = new SourceOrg("test", "TEST", "testing@test.com");
        for(int i=0; i<2; i++)
        {
            Profile profile = new Profile(UUID.randomUUID().toString(), "test"+i+"@test.com", "8675309", "", "test");
            profile.setSourceOrgId(sourceOrg.getOrgId());
            sourceOrg.getProfiles().add(profile);
        }

        this.mongoDBJsonStore.storeSourceOrg(sourceOrg);

        SourceOrg sourceOrg1 = this.mongoDBJsonStore.getSourceOrgs().get(0);
        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Location location = new Location(startLatitude, startLongitude);
        FoodRunner foodRunner = new FoodRunner(sourceOrg1.getProfiles().get(0), location);

        JsonObject json = new JsonObject();
        json.addProperty("orgId", "test");
        json.addProperty("orgName", "TEST");
        json.addProperty("orgContactEmail", "testing@test.com");

        Response response = given().body(json.toString()).when().post("/activeNetwork/pickUpRequest/send/").andReturn();

        String jsonString = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(jsonString);
        logger.info("****");
    }
}