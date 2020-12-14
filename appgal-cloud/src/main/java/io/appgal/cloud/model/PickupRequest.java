package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class PickupRequest implements Serializable,Comparable {
    private static Logger logger = LoggerFactory.getLogger(PickupRequest.class);

    private String requestId;
    private SourceOrg sourceOrg;
    private Enum<FoodTypes> foodType;

    public PickupRequest() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }

    public SourceOrg getSourceOrg() {
        return sourceOrg;
    }

    public void setSourceOrg(SourceOrg sourceOrg) {
        this.sourceOrg = sourceOrg;
    }

    public Enum<FoodTypes> getFoodType() {
        return foodType;
    }

    public void setFoodType(Enum<FoodTypes> foodType) {
        this.foodType = foodType;
    }

    @Override
    public String toString()
    {
        return this.toJson().toString();
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("requestId", this.requestId);
        if(this.sourceOrg != null) {
            jsonObject.add("sourceOrg", this.sourceOrg.toJson());
        }
        if(this.foodType != null) {
            jsonObject.addProperty("foodType", this.foodType.name());
        }

        return jsonObject;
    }

    public static PickupRequest parse(String jsonBody)
    {
        PickupRequest pickupRequest = new PickupRequest();
        JsonObject jsonObject = JsonParser.parseString(jsonBody).getAsJsonObject();

        pickupRequest.setRequestId(jsonObject.get("requestId").getAsString());

        SourceOrg sourceOrg = SourceOrg.parse(jsonObject.get("sourceOrg").getAsJsonObject().toString());
        pickupRequest.setSourceOrg(sourceOrg);

        if(jsonObject.has("foodType")) {
            String foodTypeValue = jsonObject.get("foodType").getAsString();
            pickupRequest.foodType = FoodTypes.valueOf(foodTypeValue);
        }

        return pickupRequest;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
