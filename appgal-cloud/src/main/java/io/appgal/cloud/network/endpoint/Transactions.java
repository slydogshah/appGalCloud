package io.appgal.cloud.network.endpoint;

import com.google.common.collect.ForwardingList;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.FoodRecoveryTransaction;
import io.appgal.cloud.model.ScheduleDropOffNotification;
import io.appgal.cloud.model.SchedulePickUpNotification;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.network.services.NetworkOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("tx")
public class Transactions {
    private static Logger logger = LoggerFactory.getLogger(Transactions.class);


    @Path("/recovery")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFoodRecoveryTransactions(@QueryParam("email") String email)
    {
        List<FoodRecoveryTransaction> txs = new ArrayList<>();

        for(int i=0; i<5; i++) {
            //pickup
            SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com", true);
            sourceOrg.setProducer(true);
            SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification(UUID.randomUUID().toString());
            schedulePickUpNotification.setSourceOrg(sourceOrg);
            OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
            schedulePickUpNotification.setStart(start);

            //dropoff
            SourceOrg church = new SourceOrg("church", "Church", "mrchrist@church.com", false);
            ScheduleDropOffNotification dropOffNotification = new ScheduleDropOffNotification(UUID.randomUUID().toString());
            dropOffNotification.setSourceOrg(church);


            FoodRecoveryTransaction tx = new FoodRecoveryTransaction(schedulePickUpNotification, dropOffNotification);
            txs.add(tx);
        }
        return Response.ok(txs.toString()).build();
    }
}