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
        this.mongoDBJsonStore.storeScheduledDropOffNotification(scheduleDropOffNotification);
        this.dropOffPipeline.add(scheduleDropOffNotification);

        this.foodRecoveryOrchestrator.notifyDropOff(scheduleDropOffNotification);
    }

    public List<FoodRecoveryTransaction> findMyTransactions(String email)
    {
        List<FoodRecoveryTransaction> myTransactions = new ArrayList<>();
        try {
            logger.info("*******************************************************************************************************");
            logger.info("FOODRUNNER_EMAIL: "+email);
            logger.info("*******************************************************************************************************");
            FoodRunner foodRunner = this.mongoDBJsonStore.getFoodRunner(email);
            JsonUtil.print(this.getClass(), foodRunner.toJson());

            //TODO
            List<FoodRecoveryTransaction> all = this.mongoDBJsonStore.getFoodRecoveryTransactions();
            //myTransactions.addAll(all);
            for (FoodRecoveryTransaction tx : all) {
                JsonUtil.print(this.getClass(), tx.toJson());


                Location source = tx.getPickUpNotification().getSourceOrg().getLocation();
                if(source == null)
                {
                    logger.info("SOURCE_LOCATION_NOT_FOUND");
                    continue;
                }
                JsonUtil.print(this.getClass(), source.toJson());


                Location foodRunnerLocation = foodRunner.getLocation();
                if(foodRunnerLocation == null)
                {
                    logger.info("FOODRUNNER_LOCATION_NOT_FOUND");
                    continue;
                }
                JsonUtil.print(this.getClass(), foodRunnerLocation.toJson());


                Double distance = this.mapUtils.calculateDistance(foodRunnerLocation.getLatitude(),
                        foodRunnerLocation.getLongitude(),
                        source.getLatitude(), source.getLongitude());

                logger.info("**************DISTANCE*****************");
                logger.info("DISTANCE: "+distance);
                logger.info("**************DISTANCE*****************");

                if (distance <= 5.0d) {
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
    //-------------------------
    public NotificationEngine getNotificationEngine()
    {
        return this.notificationEngine;
    }

    public JsonObject acceptRecoveryTransaction(FoodRecoveryTransaction foodRecoveryTransaction)
    {
        this.mongoDBJsonStore.storeFoodRecoveryTransaction(foodRecoveryTransaction);

        JsonObject json = new JsonObject();
        json.addProperty("recoveryTransactionId", foodRecoveryTransaction.getId());
        return json;
    }
}
