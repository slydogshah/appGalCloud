package io.appgal.cloud.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.network.model.ActiveNetwork;
import io.appgal.cloud.network.model.FoodRunner;
import io.appgal.cloud.network.model.PickupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class NetworkOrchestrator {
    private static Logger logger = LoggerFactory.getLogger(NetworkOrchestrator.class);

    private ActiveNetwork activeNetwork;

    private Queue<PickupRequest> activeFoodRunnerQueue;

    private Map<String, Collection<FoodRunner>> finderResults;

    @PostConstruct
    public void start()
    {
        this.activeNetwork = new ActiveNetwork();
        this.activeFoodRunnerQueue = new PriorityQueue<>();
        this.finderResults = new HashMap<>();
        logger.info("*******");
        logger.info("NETWORK_ORCHESTRATOR_IS_ONLINE_NOW");
        logger.info("*******");
    }

    public void enterNetwork(FoodRunner foodRunner)
    {
        this.activeNetwork.addActiveFoodRunner(foodRunner);
    }

    public void sendPickUpRequest(PickupRequest pickupRequest)
    {
        //Place the PickUp Request in the ActiveFoodRunner Queue
        this.activeFoodRunnerQueue.add(pickupRequest);

        this.runFoodRunnerFinder();
    }

    public JsonObject getActiveView()
    {
        JsonObject jsonObject = new JsonObject();

        Collection<FoodRunner> activeFoodRunners = this.activeNetwork.readActiveFoodRunners();
        JsonArray activeFoodRunnerArray = new JsonArray();
        Iterator<FoodRunner> iterator = activeFoodRunners.iterator();
        while(iterator.hasNext())
        {
            FoodRunner foodRunner = iterator.next();
            activeFoodRunnerArray.add(foodRunner.toJson());
        }
        jsonObject.add("activeFoodRunners", activeFoodRunnerArray);

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

        return jsonObject;
    }

    private void runFoodRunnerFinder()
    {
        Collection<FoodRunner> findResults = this.activeNetwork.readActiveFoodRunners();
        this.finderResults.put(UUID.randomUUID().toString(), findResults);
    }
}
