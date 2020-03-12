package io.appgal.cloud.util;

import org.locationtech.spatial4j.distance.DistanceUtils;

public class MapUtils {
    public static double calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude)
    {
        double distance = DistanceUtils.distLawOfCosinesRAD(
                DistanceUtils.toRadians(startLatitude),
                DistanceUtils.toRadians(startLongitude),
                DistanceUtils.toRadians(endLatitude),
                DistanceUtils.toRadians(endLongitude));
        distance = DistanceUtils.radians2Dist(distance, DistanceUtils.EARTH_MEAN_RADIUS_MI);
        return distance;
    }
}
