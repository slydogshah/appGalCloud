package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("profile", this.profile.toJson());
        jsonObject.add("location", this.location.toJson());

        return jsonObject;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    public static FoodRunner parse(String json)
    {
        logger.info(json);
        FoodRunner foodRunner = new FoodRunner();
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        Profile profile = Profile.parse(jsonObject.get("profile").toString());
        Location location = Location.parse(jsonObject.get("location").toString());
        foodRunner.setProfile(profile);
        foodRunner.setLocation(location);
        return foodRunner;
    }
}
