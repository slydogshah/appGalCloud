package io.appgal.cloud.model;

import com.google.gson.JsonArray;
import io.appgal.cloud.network.geospatial.DistanceCalculator;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;

@ApplicationScoped
public class ActiveNetwork implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(ActiveNetwork.class);

    private Map<String, FoodRunner> activeFoodRunners;
    private List<SourceOrg> sourceOrgs;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;
    

    public ActiveNetwork()
    {
        this.activeFoodRunners = new HashMap<>();
        this.sourceOrgs = new ArrayList<>();
    }

    @PostConstruct
    public void start()
    {
       ActiveNetwork activeNetwork = this.mongoDBJsonStore.getActiveNetwork();
       this.activeFoodRunners = activeNetwork.activeFoodRunners;
       this.sourceOrgs = activeNetwork.sourceOrgs;
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

    public FoodRunner findFoodRunner(String foodRunnerId)
    {
        return this.activeFoodRunners.get(foodRunnerId);
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
        if(!result.isEmpty()) {
            this.mongoDBJsonStore.storeResults(result);
        }
        return result;
    }

    public List<SourceOrg> matchSourceOrgs(FoodRequest foodRequest)
    {
        DistanceCalculator distanceCalculator = new DistanceCalculator();
        Location sourceLocation = foodRequest.getSourceOrg().getLocation();
        List<SourceOrg> result = new ArrayList<>();
        List<FoodRunner> results = this.mongoDBJsonStore.getResults();
        for(FoodRunner cour:results) {
            Location location = cour.getLocation();
            Double distance = distanceCalculator.calculateDistance(sourceLocation, location);
            if(distance <= 5.0d)
            {
                result.add(cour.getPickUpOrg());
            }
        }
        return result;
    }

    public List<SourceOrg> findSourceOrgs(SourceOrg destination)
    {
        List<SourceOrg> result = new ArrayList<>();
        DistanceCalculator distanceCalculator = new DistanceCalculator();

        //TODO: ONLY SourceOrgs that have sent out a PICK_UP_FOOD_NOTIFICATION
        List<SourceOrg> sourceOrgs=this.mongoDBJsonStore.getSourceOrgs();
        for(SourceOrg cour:sourceOrgs)
        {
            Double distance = distanceCalculator.calculateDistance(destination.getLocation(),
                    cour.getLocation());
            if(distance == 25d)
            result.add(cour);
        }
        return result;
    }

    public List<SourceOrg> matchFoodRunner(FoodRunner foodRunner)
    {
        List<SourceOrg> result = new ArrayList<>();
        DistanceCalculator distanceCalculator = new DistanceCalculator();

        //TODO: ONLY SourceOrgs that have sent out a PICK_UP_FOOD_NOTIFICATION
        result = this.mongoDBJsonStore.getSourceOrgs();
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
