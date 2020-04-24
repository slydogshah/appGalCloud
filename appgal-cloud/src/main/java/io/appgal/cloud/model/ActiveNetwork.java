package io.appgal.cloud.model;

import com.google.gson.JsonArray;
import io.appgal.cloud.geospatial.DistanceCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public class ActiveNetwork implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(ActiveNetwork.class);

    private Map<String, FoodRunner> activeFoodRunners;
    private List<SourceOrg> sourceOrgs;
    

    public ActiveNetwork()
    {
        this.activeFoodRunners = new HashMap<>();
        this.sourceOrgs = new ArrayList<>();
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

    public List<SourceOrg> getSourceOrgs() {
        return sourceOrgs;
    }

    public void setSourceOrgs(List<SourceOrg> sourceOrgs) {
        this.sourceOrgs = sourceOrgs;
    }

    public void addActiveFoodRunner(FoodRunner foodRunner)
    {
        this.activeFoodRunners.put(foodRunner.getProfile().getId(), foodRunner);

        //persist the state of the network
    }

    public void removeFoodRunner(FoodRunner foodRunner)
    {
        this.activeFoodRunners.remove(foodRunner.getProfile().getId());
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
