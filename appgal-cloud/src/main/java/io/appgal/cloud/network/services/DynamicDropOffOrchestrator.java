package io.appgal.cloud.network.services;

import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.infrastructure.OfflineDropOffPipeline;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.appgal.cloud.model.FoodRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DynamicDropOffOrchestrator {
    private static Logger logger = LoggerFactory.getLogger(DynamicDropOffOrchestrator.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;


    public void notifyAvailability(String foodRunnerEmail)
    {
        FoodRunner foodRunner = this.mongoDBJsonStore.getFoodRunner(foodRunnerEmail);
        foodRunner.setOfflineCommunitySupport(true);
        this.mongoDBJsonStore.updateFoodRunner(foodRunner);
    }

    public JsonObject orchestrateOfflineCommunity()
    {
        JsonObject pipelineData = new JsonObject();
        return pipelineData;
    }

    public JsonArray getOfflineDropOffPipeline()
    {
        JsonArray jsonArray = new JsonArray();
        return jsonArray;
    }

    public List<FoodRunner> getOfflineDropOffHelpers()
    {
        List<FoodRunner> helpers = new ArrayList<>();

        //TODO
        FoodRunner foodRunner = this.mongoDBJsonStore.getFoodRunner("jen@appgallabs.io");
        helpers.add(foodRunner);

        return helpers;
    }
}
