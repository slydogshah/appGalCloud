package io.appgal.cloud.network.model;

import com.google.gson.JsonArray;
import io.appgal.cloud.geospatial.DistanceCalculator;
import io.appgal.cloud.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public class ActiveNetwork implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(ActiveNetwork.class);

    private Map<String, FoodRunner> activeFoodRunners;
    

    public ActiveNetwork()
    {
        this.activeFoodRunners = new HashMap<>();
    }

    public ActiveNetwork(Map<String, FoodRunner> activeFoodRunners) {
        this.activeFoodRunners = activeFoodRunners;
    }

    public Map<String, FoodRunner> getActiveFoodRunners() {
        return activeFoodRunners;
    }

    public void setActiveFoodRunners(Map<String, FoodRunner> activeFoodRunners)
    {
        this.activeFoodRunners = activeFoodRunners;
    }

    public void addActiveFoodRunner(FoodRunner foodRunner)
    {
        this.activeFoodRunners.put(foodRunner.getProfile().getId(), foodRunner);

        //persist the state of the network
    }

    public Collection<FoodRunner> readActiveFoodRunners()
    {
        return this.activeFoodRunners.values();
    }

    public List<FoodRunner> findFoodRunners(PickupRequest pickupRequest)
    {
        List<FoodRunner> result = new ArrayList<>();
        DistanceCalculator distanceCalculator = new DistanceCalculator();
        Iterator<FoodRunner> iterator = this.activeFoodRunners.values().iterator();
        while(iterator.hasNext())
        {
            FoodRunner foodRunner = iterator.next();
            Location pickUpLocation = pickupRequest.getSourceOrg().getLocation();
            Location foodRunnerLocation = foodRunner.getLocation();
            Double distance = distanceCalculator.calculateDistance(pickUpLocation, foodRunnerLocation);
            if(distance <= 5.0d)
            {
                result.add(foodRunner);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        Collection<FoodRunner> foodRunners = this.activeFoodRunners.values();
        JsonArray array = new JsonArray();
        for(FoodRunner cour:foodRunners)
        {
            array.add(cour.toJson());
        }
        return array.toString();
    }
}
