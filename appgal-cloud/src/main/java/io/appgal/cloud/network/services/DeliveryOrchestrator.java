package io.appgal.cloud.network.services;

import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class DeliveryOrchestrator {
    private static Logger logger = LoggerFactory.getLogger(DeliveryOrchestrator.class);

    private ActiveNetwork activeNetwork;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    public DeliveryOrchestrator() {
    }

    @PostConstruct
    public void start()
    {
        this.activeNetwork = this.mongoDBJsonStore.getActiveNetwork();
    }

    public List<SourceOrg> findBestDestination(FoodRunner foodRunner)
    {
        List<SourceOrg> sourceOrgs = new ArrayList<>();

        sourceOrgs = this.activeNetwork.getSourceOrgs();

        return sourceOrgs;
    }

    public void sendDeliveryNotification(DropOffNotification notification)
    {
        this.mongoDBJsonStore.storeDropOffNotification(notification);
    }

    public String sendFoodRequest(FoodRequest foodRequest)
    {
        String requestId = UUID.randomUUID().toString();
        foodRequest.setId(requestId);
        this.mongoDBJsonStore.storeFoodRequest(foodRequest);
        return requestId;
    }

    public FoodRequest getFoodRequest(String requestId)
    {
        return this.mongoDBJsonStore.getFoodRequest(requestId);
    }

    public void schedulePickUp(SchedulePickUpNotification schedulePickUpNotification)
    {
        logger.info("***********************");
        logger.info("***********************");
    }
}
