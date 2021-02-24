package io.appgal.cloud.network.services;

import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.FoodRecoveryTransaction;
import io.appgal.cloud.model.ScheduleDropOffNotification;
import io.appgal.cloud.model.SchedulePickUpNotification;
import io.appgal.cloud.model.SourceOrg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FoodRecoveryOrchestrator {
    private static Logger logger = LoggerFactory.getLogger(FoodRecoveryOrchestrator.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    public void notifyForPickUp(SchedulePickUpNotification schedulePickUpNotification)
    {
        FoodRecoveryTransaction recoveryTx = new FoodRecoveryTransaction();
        recoveryTx.setPickUpNotification(schedulePickUpNotification);

        //start the transaction
        this.mongoDBJsonStore.storeFoodRecoveryTransaction(recoveryTx);
    }

    public void notifyDropOff(ScheduleDropOffNotification scheduleDropOffNotification)
    {

    }

    public List<SourceOrg> findDropOffOrganizations(String orgId)
    {
        List<SourceOrg> dropOffOrgs = this.mongoDBJsonStore.getSourceOrgs();
        return dropOffOrgs;
    }
}
