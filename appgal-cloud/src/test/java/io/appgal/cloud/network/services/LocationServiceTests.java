package io.appgal.cloud.network.services;

import io.appgal.cloud.model.*;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class LocationServiceTests {
    private static Logger logger = LoggerFactory.getLogger(LocationServiceTests.class);

    @Inject
    private LocationService locationService;

    @Inject
    private ActiveNetwork activeNetwork;

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Test
    public void testCurrentLocation()
    {
        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "", "", ProfileType.FOOD_RUNNER);
        Location location = new Location(startLatitude, startLongitude);
        FoodRunner foodRunner = new FoodRunner(profile, location);

        this.networkOrchestrator.enterNetwork(foodRunner);

        logger.info(this.locationService.getCurrentLocation(foodRunner.getProfile().getEmail()).toString());
    }
}