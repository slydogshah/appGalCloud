package io.appgal.cloud.network;

import com.google.gson.JsonObject;
import io.appgal.cloud.network.model.ActiveNetwork;
import io.appgal.cloud.network.model.FoodRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NetworkOrchestrator {
    private static Logger logger = LoggerFactory.getLogger(NetworkOrchestrator.class);

    private ActiveNetwork activeNetwork;

    public void bootUp()
    {
        this.activeNetwork = new ActiveNetwork();
    }

    public void enterNetwork(FoodRunner foodRunner)
    {
        this.activeNetwork.addActiveFoodRunner(foodRunner);
    }

    public JsonObject getActiveView()
    {
        JsonObject jsonObject = new JsonObject();
        return jsonObject;
    }
}
