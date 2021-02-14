package io.appgal.cloud.network.services;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class LocationServiceTests {
    private static Logger logger = LoggerFactory.getLogger(LocationServiceTests.class);

    @Inject
    private LocationService locationService;

    @Test
    public void testCurrentLocation()
    {
        logger.info(this.locationService.getCurrentLocation().toString());
    }
}