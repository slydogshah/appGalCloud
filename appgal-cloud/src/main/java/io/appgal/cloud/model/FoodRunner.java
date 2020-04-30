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

        if(this.profile != null) {
            jsonObject.add("profile", this.profile.toJson());
        }
        if(this.location != null) {
            jsonObject.add("location", this.location.toJson());
        }

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
        if(jsonObject.has("profile")) {
            Profile profile = Profile.parse(jsonObject.get("profile").toString());
            foodRunner.setProfile(profile);
        }
        if(jsonObject.has("location")) {
            Location location = Location.parse(jsonObject.get("location").toString());
            foodRunner.setLocation(location);
        }
        return foodRunner;
    }
}
