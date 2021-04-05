package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.UUID;

public class FoodRecoveryTransaction implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(FoodRecoveryTransaction.class);

    private String id;
    private SchedulePickUpNotification pickUpNotification;
    private ScheduleDropOffNotification dropOffNotification;
    private FoodRunner foodRunner;
    private TransactionState transactionState = TransactionState.SUBMITTED;

    public FoodRecoveryTransaction()
    {
    }

    public FoodRecoveryTransaction(SchedulePickUpNotification pickUpNotification, ScheduleDropOffNotification dropOffNotification) {
        this.pickUpNotification = pickUpNotification;
        this.dropOffNotification = dropOffNotification;
    }

    public FoodRecoveryTransaction(SchedulePickUpNotification pickUpNotification, ScheduleDropOffNotification dropOffNotification,
                                   FoodRunner foodRunner)
    {
        this.pickUpNotification = pickUpNotification;
        this.dropOffNotification = dropOffNotification;
        this.foodRunner = foodRunner;
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

    public FoodRunner getFoodRunner() {
        return foodRunner;
    }

    public void setFoodRunner(FoodRunner foodRunner) {
        this.foodRunner = foodRunner;
    }

    public TransactionState getTransactionState() {
        return transactionState;
    }

    public void setTransactionState(TransactionState transactionState) {
        this.transactionState = transactionState;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        if(jsonObject.has("foodRunner"))
        {
            foodRecoveryTransaction.foodRunner = FoodRunner.parse(
                    jsonObject.get("foodRunner").toString());
        }
        if(jsonObject.has("transactionState"))
        {
            String txStateString = jsonObject.get("transactionState").getAsString();
            foodRecoveryTransaction.transactionState = TransactionState.valueOf(txStateString);
        }
        if(jsonObject.has("id"))
        {
            foodRecoveryTransaction.id = jsonObject.get("id").getAsString();
        }
        else
        {
            foodRecoveryTransaction.id = UUID.randomUUID().toString();
        }

        return foodRecoveryTransaction;
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();

        if(this.transactionState != null)
        {
            jsonObject.addProperty("transactionState", this.transactionState.name());
        }
        if(this.pickUpNotification != null) {
            jsonObject.add("pickupNotification", this.pickUpNotification.toJson());
        }
        if(this.dropOffNotification != null) {
            jsonObject.add("dropOffNotification", this.dropOffNotification.toJson());
        }
        if(this.foodRunner != null)
        {
            jsonObject.add("foodRunner", this.foodRunner.toJson());
        }

        if(this.id != null)
        {
            jsonObject.addProperty("id", this.id);
        }

        return jsonObject;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
