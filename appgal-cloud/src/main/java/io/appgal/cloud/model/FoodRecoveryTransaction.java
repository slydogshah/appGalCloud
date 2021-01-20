package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class FoodRecoveryTransaction implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(FoodRecoveryTransaction.class);

    private SchedulePickUpNotification pickUpNotification;
    private ScheduleDropOffNotification dropOffNotification;

    FoodRecoveryTransaction()
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
        return new FoodRecoveryTransaction();
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("pickupNotification", this.pickUpNotification.toJson());
        jsonObject.add("dropOffNotification", this.dropOffNotification.toJson());

        return jsonObject;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
