package io.appgal.cloud.network.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.NotificationEngine;
import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.util.JsonUtil;
import io.bugsbunny.data.history.service.DataReplayService;
import io.bugsbunny.preprocess.SecurityTokenContainer;
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
    private DataReplayService dataReplayService;

    @Inject
    private SecurityTokenContainer securityTokenContainer;

    @Inject
    private RequestPipeline requestPipeline;

    @Inject
    private DropOffPipeline dropOffPipeline;

    private Map<String, Collection<FoodRunner>> finderResults;

    private NotificationEngine notificationEngine;

    @PostConstruct
    public void start()
    {
        this.finderResults = new HashMap<>();
        this.notificationEngine = new NotificationEngine(this.securityTokenContainer, this.requestPipeline, this.dropOffPipeline,
                this.mongoDBJsonStore);
        this.notificationEngine.start();
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

    private void runFoodRunnerFinder()
    {
        //PickupRequest pickupRequest = this.activeFoodRunnerQueue.remove();
        //Collection<FoodRunner> findResults = this.activeNetwork.findFoodRunners(pickupRequest);
        //this.finderResults.put(pickupRequest.getRequestId(), findResults);
    }

    public List<SourceOrg> findBestDestination(FoodRunner foodRunner)
    {
        List<SourceOrg> sourceOrgs = this.activeNetwork.matchFoodRunner(foodRunner);
        return sourceOrgs;
    }
    //--------FoodRunner Matching Process-----------------------------------------------
    public void schedulePickUp(SchedulePickUpNotification schedulePickUpNotification)
    {
        this.mongoDBJsonStore.storeScheduledPickUpNotification(schedulePickUpNotification);
        this.requestPipeline.add(schedulePickUpNotification);
    }

    public void schedulePickDropOff(ScheduleDropOffNotification scheduleDropOffNotification)
    {
        this.mongoDBJsonStore.storeScheduledDropOffNotification(scheduleDropOffNotification);
        this.dropOffPipeline.add(scheduleDropOffNotification);
    }
}
