package io.appgal.cloud.network.model;

import io.appgal.cloud.model.Location;
import io.appgal.cloud.model.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class FoodRunner implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(FoodRunner.class);

    private Profile profile;
    private Location location;

    public FoodRunner() {
    }

    public FoodRunner(Profile profile, Location location) {
        this.profile = profile;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
