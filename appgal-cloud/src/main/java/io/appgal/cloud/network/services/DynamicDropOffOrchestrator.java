package io.appgal.cloud.network.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.infrastructure.OfflineDropOffPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DynamicDropOffOrchestrator {
    private static Logger logger = LoggerFactory.getLogger(DynamicDropOffOrchestrator.class);

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Inject
    private OfflineDropOffPipeline offlineDropOffPipeline;

    public JsonObject orchestrateOfflineCommunity()
    {
        JsonObject pipelineData = new JsonObject();

        //Get Offline FoodRunner Pipeline
        JsonArray pipeline = this.getOfflineDropOffPipeline();
        pipelineData.add("offlineDropOffs",pipeline);

        return pipelineData;
    }

    public JsonArray getOfflineDropOffPipeline()
    {
        JsonArray jsonArray = this.offlineDropOffPipeline.findRunnersWithDynamicDropOff();
        return jsonArray;
    }
}
