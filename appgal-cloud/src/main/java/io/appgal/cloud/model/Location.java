package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class Location implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(Location.class);

    private double latitude;
    private double longitude;

    public Location() {
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("latitude", this.latitude);
        jsonObject.addProperty("longitude", this.longitude);

        return jsonObject;
    }

    public static Location parse(String json)
    {
        Location location = new Location();

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        location.latitude = jsonObject.get("latitude").getAsDouble();
        location.longitude = jsonObject.get("longitude").getAsDouble();

        return location;
    }
}
