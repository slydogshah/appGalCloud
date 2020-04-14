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
import java.util.Collection;
import java.util.Iterator;

@ApplicationScoped
public class NetworkOrchestrator {
    private static Logger logger = LoggerFactory.getLogger(NetworkOrchestrator.class);

    private ActiveNetwork activeNetwork;

    @PostConstruct
    public void start()
    {
        logger.info("*******");
        logger.info("NETWORK_ORCHESTRATOR_IS_ONLINE_NOW");
        logger.info("*******");
    }

    public void bootUp()
    {
        this.activeNetwork = new ActiveNetwork();
    }

    public void enterNetwork(FoodRunner foodRunner)
    {
        this.activeNetwork.addActiveFoodRunner(foodRunner);
    }

    public void sendPickUpRequest(PickupRequest pickupRequest)
    {
        logger.info("RECEIVED: "+pickupRequest.toString());
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

        return jsonObject;
    }
}
