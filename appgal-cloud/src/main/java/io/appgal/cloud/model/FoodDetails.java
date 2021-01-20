package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class FoodDetails implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(FoodDetails.class);

    public static FoodDetails parse(String json)
    {
        FoodDetails foodDetails = new FoodDetails();
        return foodDetails;
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();
        return jsonObject;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
