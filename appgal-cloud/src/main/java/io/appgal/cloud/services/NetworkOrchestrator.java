package io.appgal.cloud.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.Location;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.model.PickupRequest;
import io.appgal.cloud.persistence.MongoDBJsonStore;
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
        this.mongoDBJsonStore.deleteFoodRunner(foodRunner);
    }

    public void sendPickUpRequest(PickupRequest pickupRequest)
    {
        //Place the PickUp Request in the ActiveFoodRunner Queue
        pickupRequest.setRequestId(UUID.randomUUID().toString());
        this.activeFoodRunnerQueue.add(pickupRequest);

        this.runFoodRunnerFinder();
    }

    public JsonArray getPickRequestResult(String requestId)
    {
        //TODO: unmock this beautiful dataset @bugs.bunny.shah@gmail.com
        SourceOrg pickUp1 = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        pickUp1.setLocation(new Location(30.25860595703125d,-97.74873352050781d));


        PickupRequest pickupRequest = new PickupRequest(requestId, pickUp1);
        Collection<FoodRunner> foodRunners = this.activeNetwork.findFoodRunners(pickupRequest);
        Iterator<FoodRunner> itr = foodRunners.iterator();
        JsonArray array = new JsonArray();
        while(itr.hasNext())
        {
            FoodRunner foodRunner = itr.next();
            array.add(foodRunner.toJson());
        }
        return array;
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

    private void runFoodRunnerFinder()
    {
        PickupRequest pickupRequest = this.activeFoodRunnerQueue.remove();
        Collection<FoodRunner> findResults = this.activeNetwork.findFoodRunners(pickupRequest);
        this.finderResults.put(pickupRequest.getRequestId(), findResults);
    }
}
