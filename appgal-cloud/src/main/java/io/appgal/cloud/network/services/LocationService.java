package io.appgal.cloud.network.services;

import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.model.Location;
import io.appgal.cloud.network.endpoint.LocationAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class LocationService {
    private static Logger logger = LoggerFactory.getLogger(LocationService.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    private ActiveNetwork activeNetwork;

    public void receiveUpdate(FoodRunner foodRunner)
    {

    }

    public Location getCurrentLocation(String foodRunnerEmail)
    {
        FoodRunner foodRunner = this.activeNetwork.findFoodRunnerByEmail(foodRunnerEmail);
        this.mongoDBJsonStore.updateFoodRunner(foodRunner);
        this.activeNetwork.flushToStore();
        return foodRunner.getLocation();
    }
}
