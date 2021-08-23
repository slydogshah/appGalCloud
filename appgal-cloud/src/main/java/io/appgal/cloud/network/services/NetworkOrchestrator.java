package io.appgal.cloud.network.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.restclient.GoogleApiClient;
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
    private FoodRecoveryOrchestrator foodRecoveryOrchestrator;

    private Map<String, Collection<FoodRunner>> finderResults;

    @Inject
    private MapUtils mapUtils;

    @Inject
    private GoogleApiClient googleApiClient;

    @PostConstruct
    public void start()
    {
        this.finderResults = new HashMap<>();
        logger.info("*******");
        logger.info("NETWORK_ORCHESTRATOR_IS_ONLINE_NOW");
        logger.info("*******");
    }

    public ActiveNetwork getActiveNetwork()
    {
        return this.activeNetwork;
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
    public void startPickUpProcess(String pic,SchedulePickUpNotification notification)
    {
        this.mongoDBJsonStore.storeScheduledPickUpNotification(pic,notification);
    }

    public void schedulePickUp(SchedulePickUpNotification notification)
    {
        this.mongoDBJsonStore.updateScheduledPickUpNotification(notification);
        this.foodRecoveryOrchestrator.notifyForPickUp(notification);
    }

    public List<FoodRecoveryTransaction> findMyTransactions(String email)
    {
        List<FoodRecoveryTransaction> myTransactions = new ArrayList<>();
        try {
            FoodRunner foodRunner = this.activeNetwork.findFoodRunnerByEmail(email);
            if(foodRunner == null)
            {
                return myTransactions;
            }

            List<FoodRecoveryTransaction> all = this.mongoDBJsonStore.getFoodRecoveryTransactions();
            for (FoodRecoveryTransaction tx : all) {
                //logger.info("TX:ID>"+tx.getId()+", TX_STATE: "+tx.getTransactionState());

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

                if (distance <= 5.0d) {
                    if(!tx.isOfflineCommunitySupport()) {
                        Location dropoff = tx.getPickUpNotification().getDropOffOrg().getLocation();

                        String estimatedPickupTime = this.estimateTravelTime(foodRunnerLocation, source);
                        String estimatedDropOffTime = this.estimateTravelTime(source, dropoff);

                        tx.setEstimatedPickupTime(estimatedPickupTime);
                        tx.setEstimatedDropOffTime(estimatedDropOffTime);

                        myTransactions.add(tx);
                    }
                    else if(foodRunner.isOfflineCommunitySupport())
                    {
                        String estimatedPickupTime = this.estimateTravelTime(foodRunnerLocation, source);
                        tx.setEstimatedPickupTime(estimatedPickupTime);
                        tx.setEstimatedDropOffTime("n/a");
                        myTransactions.add(tx);
                    }
                }
                else {
                    logger.info("**************DISTANCE*****************");
                    logger.info("ALGO_DISTANCE: "+distance);
                    logger.info("**************DISTANCE*****************");
                }
            }

            return myTransactions;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(),e);
            return myTransactions;
        }
    }

    public List<FoodRunner> notifyFoodRunners(SchedulePickUpNotification pickUpNotification)
    {
        try {
            System.out.println("*****CALLED*******");
            List<FoodRunner> qualified = new ArrayList<>();
            System.out.println("*****CALLED2*******");
            SourceOrg pickupOrg = pickUpNotification.getSourceOrg();
            System.out.println("*****CALLED3*******");
            Map<String, FoodRunner> activeFoodRunners = this.activeNetwork.getActiveFoodRunners();
            System.out.println("*****CALLED4*******");
            System.out.println(activeFoodRunners);
            System.out.println("*****CALLED5*******");

            Set<Map.Entry<String,FoodRunner>> entrySet = activeFoodRunners.entrySet();
            for (Map.Entry<String,FoodRunner> entry:entrySet) {
                FoodRunner foodRunner = entry.getValue();
                System.out.println(foodRunner);

                Location source = pickupOrg.getLocation();
                if(source == null)
                {
                    System.out.println("SOURCE_IS_NULL");
                    continue;
                }

                Location foodRunnerLocation = foodRunner.getLocation();
                if(foodRunnerLocation == null)
                {
                    System.out.println("LOCATION_IS_NULL");
                    continue;
                }

                Double distance = this.mapUtils.calculateDistance(foodRunnerLocation.getLatitude(),
                        foodRunnerLocation.getLongitude(),
                        source.getLatitude(), source.getLongitude());

                if (distance <= 5.0d) {
                    qualified.add(foodRunner);
                    System.out.println("QUALIFIED");
                }
                else {
                    System.out.println("**************CHOOSE_DISTANCE*****************");
                    System.out.println("ALGO_DISTANCE: "+distance);
                    System.out.println("**************DISTANCE*****************");
                    System.out.println("UNQUALIFIED");
                }
            }
            System.out.println("*****CALLED6*******");
            return qualified;
        }
        catch(Exception e)
        {
            System.out.println("*****CALLED7*******");
            e.printStackTrace();
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    private String estimateTravelTime(Location start, Location end)
    {
        return this.googleApiClient.estimateTime(start,end).get("duration").getAsString();
    }
    //-------------------------
    public JsonObject acceptRecoveryTransaction(FoodRecoveryTransaction foodRecoveryTransaction)
    {
        String id = foodRecoveryTransaction.accept(this.mongoDBJsonStore);
        if(id == null)
        {
            throw new RuntimeException("TRANSACTION_IN_PROGRESS");
        }

        JsonObject json = new JsonObject();
        json.addProperty("recoveryTransactionId", id);
        return json;
    }
}
