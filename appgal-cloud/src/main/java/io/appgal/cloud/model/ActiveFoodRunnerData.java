package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveFoodRunnerData {
    private static Logger logger = LoggerFactory.getLogger(ActiveFoodRunnerData.class);

    public static final String TOPIC = "foodRunnerSyncProtocol_active_food_runner_session";

    private String foodRunnerId;
    private String latitude;
    private String longitude;

    public ActiveFoodRunnerData(String foodRunnerId, String latitude, String longitude) {
        this.foodRunnerId = foodRunnerId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getFoodRunnerId() {
        return foodRunnerId;
    }

    public void setFoodRunnerId(String foodRunnerId) {
        this.foodRunnerId = foodRunnerId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString()
    {
        return this.toJson().toString();
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();

        if(this.foodRunnerId != null) {
            jsonObject.addProperty("foodRunnerId", this.foodRunnerId);
        }
        if(this.latitude != null) {
            jsonObject.addProperty("latitude", this.latitude);
        }
        if(this.longitude != null) {
            jsonObject.addProperty("longitude", this.longitude);
        }

        return jsonObject;
    }
}
