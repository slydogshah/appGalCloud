package io.appgal.cloud.util;

import com.google.gson.JsonObject;
import io.appgal.cloud.model.Address;
import io.appgal.cloud.model.Location;
import io.appgal.cloud.restclient.GoogleApiClient;

import org.locationtech.spatial4j.distance.DistanceUtils;
import net.iakovlev.timeshape.TimeZoneEngine;

import java.util.List;
import java.util.Optional;
import java.time.ZoneId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MapUtils {
    @Inject
    private GoogleApiClient googleApiClient;

    public double calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude)
    {
        double distance = DistanceUtils.distLawOfCosinesRAD(
                DistanceUtils.toRadians(startLatitude),
                DistanceUtils.toRadians(startLongitude),
                DistanceUtils.toRadians(endLatitude),
                DistanceUtils.toRadians(endLongitude));
        distance = DistanceUtils.radians2Dist(distance, DistanceUtils.EARTH_MEAN_RADIUS_MI);
        return distance;
    }

    public Location calculateCoordinates(Address address)
    {
        if(address == null || address.getStreet() == null || address.getZip() == null ||
        address.getStreet().trim().length()==0 || address.getZip().trim().length()==0){
            throw new IllegalStateException("INVALID_ADDRESS");
        }

        Location location = new Location();

        JsonObject coordinates = this.googleApiClient.convertAddressToCoordinates(address);

        double latitude = coordinates.get("location").getAsJsonObject().get("lat").getAsDouble();
        double longitude = coordinates.get("location").getAsJsonObject().get("lng").getAsDouble();

        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return location;
    }

    public ZoneId determineTimeZone(double latitude, double longitude)
    {
        TimeZoneEngine engine = TimeZoneEngine.initialize();
        List<ZoneId> allZones = engine.queryAll(latitude, longitude);
        return allZones.get(0);
    }
}
