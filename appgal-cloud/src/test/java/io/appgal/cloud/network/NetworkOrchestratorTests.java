package io.appgal.cloud.network;

import com.google.gson.JsonObject;
import io.appgal.cloud.model.Location;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.network.model.FoodRunner;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

@QuarkusTest
public class NetworkOrchestratorTests {
    private static Logger logger = LoggerFactory.getLogger(NetworkOrchestrator.class);

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Test
    public void testBootup() throws Exception
    {
        this.networkOrchestrator.bootUp();

        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", "8675309", "");
        Location location = new Location(startLatitude, startLongitude);
        FoodRunner foodRunner = new FoodRunner(profile, location);

        this.networkOrchestrator.enterNetwork(foodRunner);

        JsonObject activeView = this.networkOrchestrator.getActiveView();
        logger.info("*******");
        logger.info(activeView.toString());
        logger.info("*******");
    }
}
