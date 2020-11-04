package io.appgal.cloud.network.geospatial;


import io.quarkus.test.junit.QuarkusTest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import io.appgal.cloud.model.Location;

@QuarkusTest
public class DistanceCalculatorTests {
    private static Logger logger = LoggerFactory.getLogger(DistanceCalculatorTests.class);

    @Test
    public void testCalculateDistance() throws Exception
    {
        DistanceCalculator distanceCalculator = new DistanceCalculator();

        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;

        double endLatitude = 30.26068878173828d;
        double endLongitude = -97.7466812133789d;

        Location startLocation = new Location(startLatitude, startLongitude);
        Location endLocation = new Location(endLatitude, endLongitude);

        Double distance = distanceCalculator.calculateDistance(startLocation, endLocation);

        logger.info("*******");
        logger.info("Distance: "+distance+"(miles)");
        logger.info("*******");
    }
}
