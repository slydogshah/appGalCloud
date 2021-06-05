package io.appgal.cloud.network.services;

import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.*;
import io.appgal.cloud.network.geospatial.DistanceCalculator;
import io.appgal.cloud.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class FoodRecoveryOrchestrator {
    private static Logger logger = LoggerFactory.getLogger(FoodRecoveryOrchestrator.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    public void notifyForPickUp(SchedulePickUpNotification schedulePickUpNotification)
    {
        FoodRecoveryTransaction recoveryTx = new FoodRecoveryTransaction();
        recoveryTx.setId(UUID.randomUUID().toString());
        recoveryTx.setPickUpNotification(schedulePickUpNotification);
        recoveryTx.setOfflineCommunitySupport(schedulePickUpNotification.isDropOffDynamic());

        //start the transaction
        this.mongoDBJsonStore.storeFoodRecoveryTransaction(recoveryTx);
    }

    public void notifyDropOff(FoodRecoveryTransaction foodRecoveryTransaction)
    {
        this.mongoDBJsonStore.storeFoodRecoveryTransaction(foodRecoveryTransaction);
    }

    public void notifyDelivery(FoodRecoveryTransaction foodRecoveryTransaction)
    {
        //logger.info("**********BEFORE*************");
        //JsonUtil.print(this.getClass(),foodRecoveryTransaction.toJson());
        foodRecoveryTransaction.setTransactionState(TransactionState.CLOSED);
        FoodRecoveryTransaction tx = this.mongoDBJsonStore.storeFoodRecoveryTransaction(foodRecoveryTransaction);
        //logger.info("**********AFTER*************");
        //JsonUtil.print(this.getClass(),tx.toJson());
    }

    //DropOff Location Recommendation Algorithm
    public List<SourceOrg> findDropOffOrganizations(String orgId) {
        SourceOrg producer = this.mongoDBJsonStore.getSourceOrg(orgId);
        List<SourceOrg> dropOffOrgs = this.mongoDBJsonStore.getSourceOrgs();

        DistanceCalculator distanceCalculator = new DistanceCalculator();
        List<SourceOrg> dropOffOptions = new ArrayList<>();
        for (SourceOrg dropOffOption : dropOffOrgs)
        {
            if(dropOffOption.isProducer())
            {
                continue;
            }

            JsonUtil.print(this.getClass(),dropOffOption.toJson());

            if(dropOffOption.getOrgId().equals(producer.getOrgId()))
            {
                continue;
            }

            Location location = dropOffOption.getLocation();
            if(location != null) {
                Double distance = distanceCalculator.calculateDistance(producer.getLocation(), location);
                logger.info("DISTANCE: " + distance);
            }
            dropOffOptions.add(dropOffOption);
        }

        return dropOffOptions;
    }
}
