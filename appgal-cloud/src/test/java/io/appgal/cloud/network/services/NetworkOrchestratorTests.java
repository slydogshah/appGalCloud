package io.appgal.cloud.network.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@QuarkusTest
public class NetworkOrchestratorTests{

    @Inject
    private MongoDBJsonStore mongoDBStore;
    @Inject
    private NetworkOrchestrator networkOrchestrator;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();



        private Logger logger = LoggerFactory.getLogger(DeliveryOrchestratorTests.class);

        //@Inject
        //private MongoDBJsonStore mongoDBJsonStore;
        //@Inject
        //private NetworkOrchestrator networkOrchestrator;

        @Test
        public void testBootup() throws Exception {
            double startLatitude = 30.25860595703125d;
            double startLongitude = -97.74873352050781d;
            Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "", "", ProfileType.FOOD_RUNNER);
            Location location = new Location(startLatitude, startLongitude);
            FoodRunner foodRunner = new FoodRunner(profile, location);

            this.networkOrchestrator.enterNetwork(foodRunner);

            JsonObject activeView = this.networkOrchestrator.getActiveView();
            logger.info("*******");
            logger.info(activeView.toString());
            logger.info("*******");

            JsonArray activeFoodRunners = activeView.getAsJsonArray("activeFoodRunners");
            JsonObject activeFoodRunner = activeFoodRunners.get(0).getAsJsonObject();
            JsonObject runnerProfile = activeFoodRunner.getAsJsonObject("profile");
            assertNotNull(runnerProfile);
            logger.info(runnerProfile.toString());
        }

        @Test
        public void testOrchestration() throws Exception {
            double startLatitude = 30.25860595703125d;
            double startLongitude = -97.74873352050781d;
            Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "", "",
                    ProfileType.FOOD_RUNNER);
            Location location = new Location(startLatitude, startLongitude);
            FoodRunner bugsBunny = new FoodRunner(profile, location);

            startLatitude = 44.9441d;
            startLongitude = -93.0852d;
            profile = new Profile(UUID.randomUUID().toString(), "ms.dhoni@gmail.com", "8675309", "", "",
                    ProfileType.FOOD_RUNNER);
            location = new Location(startLatitude, startLongitude);
            FoodRunner captain = new FoodRunner(profile, location);

            this.networkOrchestrator.enterNetwork(bugsBunny);
            this.networkOrchestrator.enterNetwork(captain);

            JsonObject activeView = this.networkOrchestrator.getActiveView();
            logger.info("*******");
            logger.info(activeView.toString());
            logger.info("*******");

            List<SourceOrg> sourceOrgs = new ArrayList<>();

            SourceOrg dropOff1 = new SourceOrg("church1", "DOWNTOWN_CHURCH", "downtown.church@gmail.com");
            SourceOrg dropOff2 = new SourceOrg("church2", "SUBURB_CHURCH", "suburb.church@gmail.com");


            JsonArray result = this.networkOrchestrator.getRegistered(
                    dropOff1);
            assertNotNull(result);
            logger.info("*******");
            logger.info(result.toString());
            logger.info("*******");
        }

        @Test
        public void testGetRequest() {
            JsonObject registrationJson = new JsonObject();
            registrationJson.addProperty("id", UUID.randomUUID().toString());
            registrationJson.addProperty("email", "c@s.com");
            registrationJson.addProperty("mobile", "8675309");
            registrationJson.addProperty("photo", "photu");
            registrationJson.addProperty("password", "c");
            registrationJson.addProperty("profileType", ProfileType.FOOD_RUNNER.name());
            given().body(registrationJson.toString()).post("/registration/profile");
            given().when().post("/activeNetwork/enterNetwork/?email=c@s.com").andReturn();

            SourceOrg pickUp1 = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
            pickUp1.setLocation(new Location(30.25860595703125d, -97.74873352050781d));
            SourceOrg pickUp2 = new SourceOrg("apple", "Apple", "tim_cook@apple.com");
            pickUp2.setLocation(new Location(30.25860595703125d, -97.74873352050781d));

            SourceOrg dropOff1 = new SourceOrg("church1", "DOWNTOWN_CHURCH", "downtown.church@gmail.com");
            dropOff1.setLocation(new Location(30.25860595703125d, -97.74873352050781d));
            SourceOrg dropOff2 = new SourceOrg("church2", "SUBURB_CHURCH", "suburb.church@gmail.com");
            dropOff2.setLocation(new Location(30.25860595703125d, -97.74873352050781d));

            PickupRequest pickupRequest = new PickupRequest();
            pickupRequest.setRequestId(UUID.randomUUID().toString());
            pickupRequest.setSourceOrg(pickUp1);
            this.networkOrchestrator.sendPickUpRequest(pickupRequest);

            logger.info("*******");

            JsonArray result = this.networkOrchestrator.getPickRequestResult(pickupRequest.getRequestId());
            assertNotNull(result);
            logger.info("*******");
            logger.info(this.gson.toJson(result));
            logger.info("*******");
        }

        @Test
        public void testSendPickUpRequest() throws Exception
        {
            SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");

            PickupRequest pickupRequest = new PickupRequest();
            pickupRequest.setSourceOrg(sourceOrg);

            String result = this.networkOrchestrator.sendPickUpRequest(pickupRequest);

            logger.info("***************************************************************");
            logger.info("RequestId: "+result);
            logger.info("***************************************************************");
        }
}