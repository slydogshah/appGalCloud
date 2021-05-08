package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bson.internal.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class FoodDetails implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(FoodDetails.class);

    private FoodTypes foodTypes;
    private String foodPic;
    private int quantityInPounds;


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

    public String getFoodPic() {
        return foodPic;
    }

    public void setFoodPic(String foodPic) {
        this.foodPic = foodPic;
    }

    public int getQuantityInPounds() {
        return quantityInPounds;
    }

    public void setQuantityInPounds(int quantityInPounds) {
        this.quantityInPounds = quantityInPounds;
    }

    public static FoodDetails parse(String json)
    {
        FoodDetails foodDetails = new FoodDetails();
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        if(jsonObject.has("type"))
        {
            foodDetails.foodTypes = FoodTypes.valueOf(jsonObject.get("type").getAsString());
        }
        if(jsonObject.has("foodPic"))
        {
            String pic = Base64.encode(jsonObject.get("foodPic").getAsString().getBytes(StandardCharsets.UTF_8));
            foodDetails.foodPic = pic;
        }
        if(jsonObject.has("quantityInPounds"))
        {
            foodDetails.quantityInPounds = jsonObject.get("quantityInPounds").getAsInt();
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
        if(this.foodPic != null)
        {
            jsonObject.addProperty("foodPic",this.foodPic);
        }
        jsonObject.addProperty("quantityInPounds", this.quantityInPounds);
        return jsonObject;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
