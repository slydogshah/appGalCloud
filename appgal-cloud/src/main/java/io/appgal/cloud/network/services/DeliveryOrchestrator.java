package io.appgal.cloud.network.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.bugsbunny.data.history.service.DataReplayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@ApplicationScoped
public class DeliveryOrchestrator {
    private static Logger logger = LoggerFactory.getLogger(DeliveryOrchestrator.class);

    private ActiveNetwork activeNetwork;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    private DataReplayService dataReplayService;

    public DeliveryOrchestrator() {
    }

    @PostConstruct
    public void start()
    {
        this.activeNetwork = this.mongoDBJsonStore.getActiveNetwork();
    }

    public List<SourceOrg> findBestDestination(FoodRunner foodRunner)
    {
        List<SourceOrg> sourceOrgs = this.activeNetwork.getSourceOrgs();
        return sourceOrgs;
    }

    public void sendDeliveryNotification(DestinationNotification destinationNotification)
    {
        JsonObject jsonObject = JsonParser.parseString(destinationNotification.toString()).getAsJsonObject();

        FoodRunner foodRunner = destinationNotification.getDropOffNotification().getFoodRunner();
        String chainId = foodRunner.getProfile().getChainId();

        if(chainId == null) {
            Random random = new Random();
            JsonObject modelChain = new JsonObject();
            modelChain.addProperty("modelId", random.nextLong());
            modelChain.add("payload", jsonObject);
            chainId = this.dataReplayService.generateDiffChain(modelChain);
            foodRunner.getProfile().setChainId(chainId);
        }
        else
        {
            String modelId = chainId.substring(chainId.lastIndexOf("/"));
            JsonObject modelChain = new JsonObject();
            modelChain.addProperty("modelId", modelId);
            modelChain.add("payload", jsonObject);
            this.dataReplayService.addToDiffChain(chainId, modelChain);
        }
    }
}
