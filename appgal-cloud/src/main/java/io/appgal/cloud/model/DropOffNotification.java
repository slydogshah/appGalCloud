package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    @Override
    public String toString()
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("sourceOrg", this.sourceOrg.toJson());
        jsonObject.add("location", this.location.toJson());
        jsonObject.add("foodRunner", this.foodRunner.toJson());

        return jsonObject.toString();
    }

    public static DropOffNotification parseJson(JsonObject jsonObject)
    {
        DropOffNotification dropOffNotification = new DropOffNotification();

        dropOffNotification.sourceOrg = SourceOrg.parseJson(jsonObject.get("sourceOrg").getAsJsonObject());
        dropOffNotification.location = Location.parse(jsonObject.get("location").toString());
        dropOffNotification.foodRunner = FoodRunner.parse(jsonObject.get("foodRunner").toString());

        return dropOffNotification;
    }
}
