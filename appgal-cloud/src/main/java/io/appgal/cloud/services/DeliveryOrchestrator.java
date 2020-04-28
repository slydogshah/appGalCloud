package io.appgal.cloud.services;

import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.model.DropOffNotification;
import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.persistence.MongoDBJsonStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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
}
