package io.appgal.cloud.network.geospatial;

import org.locationtech.spatial4j.distance.DistanceUtils;

import io.appgal.cloud.model.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistanceCalculator {
    private static Logger logger = LoggerFactory.getLogger(DistanceCalculator.class);

    public Double calculateDistance(Location startLocation, Location endLocation)
    {
        double distance = DistanceUtils.distLawOfCosinesRAD(
                DistanceUtils.toRadians(startLocation.getLatitude()),
                DistanceUtils.toRadians(startLocation.getLongitude()),
                DistanceUtils.toRadians(endLocation.getLatitude()),
                DistanceUtils.toRadians(endLocation.getLongitude()));
        distance = DistanceUtils.radians2Dist(distance, DistanceUtils.EARTH_MEAN_RADIUS_MI);
        return distance;
    }
}
