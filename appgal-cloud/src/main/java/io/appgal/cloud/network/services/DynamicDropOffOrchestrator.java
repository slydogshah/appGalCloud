package io.appgal.cloud.network.services;

import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.infrastructure.OfflineDropOffPipeline;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.appgal.cloud.model.*;
import io.appgal.cloud.util.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class DynamicDropOffOrchestrator {
    private static Logger logger = LoggerFactory.getLogger(DynamicDropOffOrchestrator.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    private ActiveNetwork activeNetwork;

    @Inject
    private MapUtils mapUtils;


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

    public List<FoodRunner> getOfflineDropOffHelpers(SourceOrg pickup)
    {
        List<FoodRunner> helpers = new ArrayList<>();
        try {
            Location source = pickup.getLocation();
            Map<String,FoodRunner> allHelpers = this.activeNetwork.getActiveFoodRunners();
            Set<Map.Entry<String,FoodRunner>> entrySet = allHelpers.entrySet();
            for (Map.Entry<String,FoodRunner> entry:entrySet) {
                FoodRunner cour = entry.getValue();

                Location foodRunnerLocation = cour.getLocation();
                if(foodRunnerLocation == null)
                {
                    logger.info("FOODRUNNER_LOCATION_NOT_FOUND");
                    continue;
                }

                Double distance = this.mapUtils.calculateDistance(foodRunnerLocation.getLatitude(),
                        foodRunnerLocation.getLongitude(),
                        source.getLatitude(), source.getLongitude());

                logger.info("**************DISTANCE*****************");
                logger.info("ALGO_DISTANCE: "+distance);
                logger.info("**************DISTANCE*****************");

                if (distance <= 5.0d) {
                    helpers.add(cour);
                }
            }

            return helpers;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(),e);
            return helpers;
        }
    }
}
