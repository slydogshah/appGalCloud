package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.UUID;

public class FoodRecoveryTransaction implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(FoodRecoveryTransaction.class);

    private String id;
    private SchedulePickUpNotification pickUpNotification;
    private FoodRunner foodRunner;
    private TransactionState transactionState = TransactionState.SUBMITTED;
    private String estimatedPickupTime;
    private String estimatedDropOffTime;

    public FoodRecoveryTransaction()
    {
    }

    public FoodRecoveryTransaction(SchedulePickUpNotification pickUpNotification) {
        this.pickUpNotification = pickUpNotification;
    }

    public FoodRecoveryTransaction(SchedulePickUpNotification pickUpNotification,
                                   FoodRunner foodRunner)
    {
        this.pickUpNotification = pickUpNotification;
        this.foodRunner = foodRunner;
    }

    public SchedulePickUpNotification getPickUpNotification() {
        return pickUpNotification;
    }

    public void setPickUpNotification(SchedulePickUpNotification pickUpNotification) {
        this.pickUpNotification = pickUpNotification;
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

    public String getEstimatedPickupTime() {
        return estimatedPickupTime;
    }

    public void setEstimatedPickupTime(String estimatedPickupTime) {
        this.estimatedPickupTime = estimatedPickupTime;
    }

    public String getEstimatedDropOffTime() {
        return estimatedDropOffTime;
    }

    public void setEstimatedDropOffTime(String estimatedDropOffTime) {
        this.estimatedDropOffTime = estimatedDropOffTime;
    }

    public String accept(MongoDBJsonStore mongoDBJsonStore)
    {
        synchronized (this) {
            FoodRecoveryTransaction tx = mongoDBJsonStore.getFoodRecoveryTransaction(this.getId());
            if(tx.getTransactionState() == TransactionState.SUBMITTED) {
                FoodRecoveryTransaction stored = mongoDBJsonStore.storeFoodRecoveryTransaction(this);
                return stored.getId();
            }
            return null;
        }
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

        if(jsonObject.has("estimatedPickupTime"))
        {
            String estimatedPickupTime = jsonObject.get("estimatedPickupTime").getAsString();
            foodRecoveryTransaction.estimatedPickupTime = estimatedPickupTime;
        }

        if(jsonObject.has("estimatedDropOffTime"))
        {
            String estimatedDropOffTime = jsonObject.get("estimatedDropOffTime").getAsString();
            foodRecoveryTransaction.estimatedDropOffTime = estimatedDropOffTime;
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
        if(this.foodRunner != null)
        {
            jsonObject.add("foodRunner", this.foodRunner.toJson());
        }

        if(this.id != null)
        {
            jsonObject.addProperty("id", this.id);
        }

        if(this.estimatedPickupTime != null)
        {
            jsonObject.addProperty("estimatedPickupTime", this.estimatedPickupTime);
        }

        if(this.estimatedDropOffTime != null)
        {
            jsonObject.addProperty("estimatedDropOffTime", this.estimatedDropOffTime);
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
