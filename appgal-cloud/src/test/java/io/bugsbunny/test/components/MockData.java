package io.bugsbunny.test.components;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.appgal.cloud.model.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class MockData {
    public static FoodRecoveryTransaction mockFoodRecoveryTransaction()
    {
        //pickup
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        sourceOrg.setProducer(true);
        SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification(UUID.randomUUID().toString());
        schedulePickUpNotification.setSourceOrg(sourceOrg);
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        schedulePickUpNotification.setStart(start);

        //dropoff
        SourceOrg church = new SourceOrg("church", "Church", "mrchrist@church.com",false);
        ScheduleDropOffNotification dropOffNotification = new ScheduleDropOffNotification(UUID.randomUUID().toString());
        dropOffNotification.setSourceOrg(church);

        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(0.0d, 0.0d);
        FoodRunner bugsBunny = new FoodRunner(profile, location);
        schedulePickUpNotification.setFoodRunner(bugsBunny);

        FoodRecoveryTransaction tx = new FoodRecoveryTransaction(schedulePickUpNotification,dropOffNotification, bugsBunny);
        TransactionState txState = TransactionState.INPROGRESS;
        tx.setTransactionState(txState);

        return tx;
    }

    public static SchedulePickUpNotification mockSchedulePickupNotification()
    {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        sourceOrg.setProducer(true);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "","", ProfileType.FOOD_RUNNER);
        Location location = new Location(0.0d, 0.0d);
        FoodRunner bugsBunny = new FoodRunner(profile, location);
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);

        SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification(UUID.randomUUID().toString());
        schedulePickUpNotification.setSourceOrg(sourceOrg);
        schedulePickUpNotification.setFoodRunner(bugsBunny);
        schedulePickUpNotification.setStart(start);

        FoodDetails foodDetails = new FoodDetails();
        foodDetails.setFoodTypes(FoodTypes.VEG);
        schedulePickUpNotification.setFoodDetails(foodDetails);

        return schedulePickUpNotification;
    }

    public static SourceOrg mockProducerOrg()
    {
        SourceOrg sourceOrg1 = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        sourceOrg1.setProducer(true);
        return sourceOrg1;
    }

    public static SourceOrg mockReceiverOrg()
    {
        SourceOrg sourceOrg2 = new SourceOrg("apple", "Apple", "tim_cook@apple.com",true);
        sourceOrg2.setProducer(true);
        return sourceOrg2;
    }

    public static FoodRunner mockFoodRunner()
    {
        Location location = new Location(30.25860595703125d,-97.74873352050781d);
        Profile profile = new Profile(UUID.randomUUID().toString(), "bugs.bunny.shah@gmail.com", 8675309l, "","",
                ProfileType.FOOD_RUNNER);
        FoodRunner foodRunner = new FoodRunner(profile, location);
        return foodRunner;
    }

    public static Profile mockProfile()
    {
        Profile profile = new Profile("CLOUD_ID","blah@blah.com",8675309l,"photu","", ProfileType.FOOD_RUNNER);
        return profile;
    }

    public static void main(String[] args)
    {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        SchedulePickUpNotification object = mockSchedulePickupNotification();
        System.out.println(gson.toJson(object.toJson()));
    }
}
