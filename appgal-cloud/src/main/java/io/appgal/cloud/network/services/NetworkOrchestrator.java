package io.appgal.cloud.network.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
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

    private Queue<PickupRequest> activeFoodRunnerQueue;

    private Map<String, Collection<FoodRunner>> finderResults;

    @PostConstruct
    public void start()
    {
        this.activeFoodRunnerQueue = new PriorityQueue<>();
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

    public String sendPickUpRequest(PickupRequest pickupRequest)
    {
        String requestId = UUID.randomUUID().toString();
        pickupRequest.setRequestId(requestId);

        this.mongoDBJsonStore.storePickUpRequest(pickupRequest);

        this.runFoodRunnerFinder();

        return requestId;
    }

    public JsonObject getPickRequestResult(String requestId)
    {
        PickupRequest pickupRequest = this.mongoDBJsonStore.getPickupRequest(requestId);
        if(pickupRequest == null)
        {
            return new JsonObject();
        }
        return pickupRequest.toJson();
    }

    public JsonArray getLatestResults(String requestId)
    {
        PickupRequest pickupRequest = this.mongoDBJsonStore.getPickupRequest(requestId);
        List<SourceOrg> sourceOrgs = this.activeNetwork.matchSourceOrgs(pickupRequest);
        return JsonParser.parseString(sourceOrgs.toString()).getAsJsonArray();
    }

    public JsonArray getRegistered(SourceOrg destination){
        List<SourceOrg>registeredSources=this.activeNetwork.findSourceOrgs(destination);
        return JsonParser.parseString(registeredSources.toString()).getAsJsonArray();
    }

    public List<SourceOrg> getSourceOrgs()
    {
        return this.mongoDBJsonStore.getSourceOrgs();
    }

    public JsonObject getActiveView()
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("activeFoodRunners", JsonParser.parseString(this.activeNetwork.toString()));

        JsonArray pickUpRequestArray = new JsonArray();
        Iterator<PickupRequest> itr = this.activeFoodRunnerQueue.iterator();
        while(itr.hasNext())
        {
            PickupRequest pickupRequest = itr.next();
            pickUpRequestArray.add(pickupRequest.toJson());
        }
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

    public void schedulePickUp(SchedulePickUpNotification schedulePickUpNotification)
    {
        logger.info("***********************");
        logger.info("***********************");
    }

    private void runFoodRunnerFinder()
    {
        //PickupRequest pickupRequest = this.activeFoodRunnerQueue.remove();
        //Collection<FoodRunner> findResults = this.activeNetwork.findFoodRunners(pickupRequest);
        //this.finderResults.put(pickupRequest.getRequestId(), findResults);
    }
}
