package io.appgal.cloud.network.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.appgal.cloud.infrastructure.DropOffPipeline;
import io.appgal.cloud.infrastructure.NotificationEngine;
import io.appgal.cloud.infrastructure.RequestPipeline;
import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.util.JsonUtil;
import io.appgal.cloud.util.MapUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class NetworkOrchestrator {
    private static Logger logger = LoggerFactory.getLogger(NetworkOrchestrator.class);

    @Inject
    private ActiveNetwork activeNetwork;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    private RequestPipeline requestPipeline;

    @Inject
    private DropOffPipeline dropOffPipeline;

    @Inject
    private FoodRecoveryOrchestrator foodRecoveryOrchestrator;

    private Map<String, Collection<FoodRunner>> finderResults;

    @Inject
    private NotificationEngine notificationEngine;

    @Inject
    private MapUtils mapUtils;

    @PostConstruct
    public void start()
    {
        this.finderResults = new HashMap<>();
        logger.info("*******");
        logger.info("NETWORK_ORCHESTRATOR_IS_ONLINE_NOW");
        logger.info("*******");
    }

    public void enterNetwork(FoodRunner foodRunner)
    {
        this.activeNetwork.addActiveFoodRunner(foodRunner);
        this.mongoDBJsonStore.storeActiveNetwork(this.activeNetwork.getActiveFoodRunners());
    }

    public void leaveNetwork(FoodRunner foodRunner)
    {
        this.activeNetwork.removeFoodRunner(foodRunner);
        this.mongoDBJsonStore.storeActiveNetwork(this.activeNetwork.getActiveFoodRunners());
    }

    public JsonObject getActiveView()
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("activeFoodRunners", JsonParser.parseString(this.activeNetwork.toString()));

        JsonArray pickUpRequestArray = new JsonArray();
        jsonObject.add("activeFoodRunnerQueue", pickUpRequestArray);

        JsonArray finderResultsArray = new JsonArray();
        final Set<Map.Entry<String, Collection<FoodRunner>>> entrySet = this.finderResults.entrySet();
        for(Map.Entry<String, Collection<FoodRunner>> entry:entrySet)
        {
            String resultId = entry.getKey();
            Collection<FoodRunner> results = entry.getValue();
            JsonArray array = new JsonArray();
            Iterator<FoodRunner> i = results.iterator();
            while(i.hasNext())
            {
                array.add(i.next().toJson());
            }
            JsonObject result = new JsonObject();
            result.add("result", array);
            finderResultsArray.add(result);
        }
        jsonObject.add("finderResults", finderResultsArray);

        jsonObject.add("sourceOrgs", JsonParser.parseString(this.activeNetwork.getSourceOrgs().toString()));

        return jsonObject;
    }

    public JsonArray getRegistered(SourceOrg destination){
        List<SourceOrg>registeredSources=this.activeNetwork.findSourceOrgs(destination);
        return JsonParser.parseString(registeredSources.toString()).getAsJsonArray();
    }

    public List<SourceOrg> findBestDestination(FoodRunner foodRunner)
    {
        List<SourceOrg> sourceOrgs = this.activeNetwork.matchFoodRunner(foodRunner);
        return sourceOrgs;
    }
    //--------FoodRunner Matching Process-----------------------------------------------
    public void schedulePickUp(SchedulePickUpNotification notification)
    {
        this.mongoDBJsonStore.storeScheduledPickUpNotification(notification);
        this.requestPipeline.add(notification);

        this.foodRecoveryOrchestrator.notifyForPickUp(notification);
    }

    public void scheduleDropOff(ScheduleDropOffNotification scheduleDropOffNotification)
    {
        /*this.mongoDBJsonStore.storeScheduledDropOffNotification(scheduleDropOffNotification);
        this.dropOffPipeline.add(scheduleDropOffNotification);

        this.foodRecoveryOrchestrator.notifyDropOff(scheduleDropOffNotification);*/
    }

    public List<FoodRecoveryTransaction> findMyTransactions(String email)
    {
        List<FoodRecoveryTransaction> myTransactions = new ArrayList<>();
        try {
            FoodRunner foodRunner = this.activeNetwork.findFoodRunnerByEmail(email);

            List<FoodRecoveryTransaction> all = this.mongoDBJsonStore.getFoodRecoveryTransactions();
            for (FoodRecoveryTransaction tx : all) {
                Location source = tx.getPickUpNotification().getSourceOrg().getLocation();
                if(source == null)
                {
                    logger.info("SOURCE_LOCATION_NOT_FOUND");
                    continue;
                }

                Location foodRunnerLocation = foodRunner.getLocation();
                if(foodRunnerLocation == null)
                {
                    logger.info("FOODRUNNER_LOCATION_NOT_FOUND");
                    continue;
                }

                Double distance = this.mapUtils.calculateDistance(foodRunnerLocation.getLatitude(),
                        foodRunnerLocation.getLongitude(),
                        source.getLatitude(), source.getLongitude());

                logger.info("**************DISTANCE*****************");
                logger.info("ALGO_DISTANCE: "+distance);
                logger.info("**************DISTANCE*****************");

                if (distance <= 5.0d) {
                    Location dropoff = tx.getPickUpNotification().getDropOffOrg().getLocation();

                    int estimatedPickupTime = this.estimateTravelTime(foodRunnerLocation,source);
                    int estimatedDropOffTime = this.estimateTravelTime(source,dropoff);

                    tx.setEstimatedPickupTime(estimatedPickupTime);
                    tx.setEstimatedDropOffTime(estimatedDropOffTime);

                    myTransactions.add(tx);
                }
            }

            return myTransactions;
        }
        catch(Exception e)
        {
            return myTransactions;
        }
    }

    private int estimateTravelTime(Location start, Location end)
    {
        return 10;
    }
    //-------------------------
    public NotificationEngine getNotificationEngine()
    {
        return this.notificationEngine;
    }

    public JsonObject acceptRecoveryTransaction(FoodRecoveryTransaction foodRecoveryTransaction)
    {
        FoodRecoveryTransaction stored = this.mongoDBJsonStore.storeFoodRecoveryTransaction(foodRecoveryTransaction);

        JsonObject json = new JsonObject();
        json.addProperty("recoveryTransactionId", stored.getId());
        return json;
    }
}
