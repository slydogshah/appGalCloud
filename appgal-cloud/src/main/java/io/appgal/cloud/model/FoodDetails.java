package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class FoodDetails implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(FoodDetails.class);

    private FoodTypes foodTypes;

    public FoodDetails() {
    }

    public FoodDetails(FoodTypes foodTypes) {
        this.foodTypes = foodTypes;
    }

    public FoodTypes getFoodTypes() {
        return foodTypes;
    }

    public void setFoodTypes(FoodTypes foodTypes) {
        this.foodTypes = foodTypes;
    }

    public static FoodDetails parse(String json)
    {
        FoodDetails foodDetails = new FoodDetails();
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        if(jsonObject.has("type"))
        {
            foodDetails.foodTypes = FoodTypes.valueOf(jsonObject.get("type").getAsString());
        }
        return foodDetails;
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();
        if(this.foodTypes != null)
        {
            jsonObject.addProperty("type",this.foodTypes.name());
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
