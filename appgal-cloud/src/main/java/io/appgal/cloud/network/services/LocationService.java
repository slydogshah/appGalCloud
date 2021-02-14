package io.appgal.cloud.network.services;

import io.appgal.cloud.model.Location;
import io.appgal.cloud.network.endpoint.LocationAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocationService {
    private static Logger logger = LoggerFactory.getLogger(LocationService.class);

    public void receiveUpdate(Location location)
    {

    }

    public Location getCurrentLocation()
    {
        return new Location(0.0d,0.0d);
    }
}
