package io.appgal.cloud.network.services;

import com.google.gson.JsonArray;
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

    public void orchestrateOfflineCommunity()
    {
        logger.info("*******");
        logger.info("ORCHESTRATE_OFFLINE_COMMUNITY");
        logger.info("*******");

        //TODO: Get Offline FoodRunner Pipeline

        //TODO:
    }

    public JsonArray getOfflineDropOffPipeline()
    {
        return this.offlineDropOffPipeline.findRunnersWithDynamicDropOff();
    }
}
