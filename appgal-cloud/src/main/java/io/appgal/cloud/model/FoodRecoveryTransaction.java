package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class FoodRecoveryTransaction implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(FoodRecoveryTransaction.class);

    private SchedulePickUpNotification pickUpNotification;
    private DropOffNotification dropOffNotification;

    FoodRecoveryTransaction()
    {

    }

    public SchedulePickUpNotification getPickUpNotification() {
        return pickUpNotification;
    }

    public void setPickUpNotification(SchedulePickUpNotification pickUpNotification) {
        this.pickUpNotification = pickUpNotification;
    }

    public DropOffNotification getDropOffNotification() {
        return dropOffNotification;
    }

    public void setDropOffNotification(DropOffNotification dropOffNotification) {
        this.dropOffNotification = dropOffNotification;
    }

    public static FoodRecoveryTransaction parse(String json)
    {
        return new FoodRecoveryTransaction();
    }

    public JsonObject toJson()
    {
        return new JsonObject();
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
