package io.appgal.cloud.model;

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
}
