package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class FoodRecoveryTransaction implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(FoodRecoveryTransaction.class);

    private SchedulePickUpNotification pickUpNotification;
    private ScheduleDropOffNotification dropOffNotification;
    private FoodRunner foodRunner;
    private TransactionState state;

    public FoodRecoveryTransaction()
    {
    }

    public FoodRecoveryTransaction(SchedulePickUpNotification pickUpNotification, ScheduleDropOffNotification dropOffNotification) {
        this.pickUpNotification = pickUpNotification;
        this.dropOffNotification = dropOffNotification;
    }

    public SchedulePickUpNotification getPickUpNotification() {
        return pickUpNotification;
    }

    public void setPickUpNotification(SchedulePickUpNotification pickUpNotification) {
        this.pickUpNotification = pickUpNotification;
    }

    public ScheduleDropOffNotification getDropOffNotification() {
        return dropOffNotification;
    }

    public void setDropOffNotification(ScheduleDropOffNotification dropOffNotification) {
        this.dropOffNotification = dropOffNotification;
    }

    public static FoodRecoveryTransaction parse(String json)
    {
        FoodRecoveryTransaction foodRecoveryTransaction = new FoodRecoveryTransaction();

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        if(jsonObject.has("pickupNotification"))
        {
            foodRecoveryTransaction.pickUpNotification = SchedulePickUpNotification.parse(
                    jsonObject.get("pickupNotification").toString());
        }
        if(jsonObject.has("dropOffNotification"))
        {
            foodRecoveryTransaction.dropOffNotification = ScheduleDropOffNotification.parse(
                    jsonObject.get("dropOffNotification").toString());
        }

        return foodRecoveryTransaction;
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();

        if(this.pickUpNotification != null) {
            jsonObject.add("pickupNotification", this.pickUpNotification.toJson());
        }
        if(this.dropOffNotification != null) {
            jsonObject.add("dropOffNotification", this.dropOffNotification.toJson());
        }

        return jsonObject;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
