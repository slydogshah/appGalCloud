package io.appgal.cloud.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class DropOffNotification implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(DropOffNotification.class);

    private SourceOrg sourceOrg;
    private Location location;
    private FoodRunner foodRunner;

    public DropOffNotification() {
    }

    public DropOffNotification(SourceOrg sourceOrg, Location location, FoodRunner foodRunner) {
        this.sourceOrg = sourceOrg;
        this.location = location;
        this.foodRunner = foodRunner;
    }

    public SourceOrg getSourceOrg() {
        return sourceOrg;
    }

    public void setSourceOrg(SourceOrg sourceOrg) {
        this.sourceOrg = sourceOrg;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public FoodRunner getFoodRunner() {
        return foodRunner;
    }

    public void setFoodRunner(FoodRunner foodRunner) {
        this.foodRunner = foodRunner;
    }
}
