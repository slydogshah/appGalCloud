package io.appgal.cloud.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.appgal.cloud.model.Location;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.persistence.MongoDBJsonStore;
import io.appgal.cloud.services.DeliveryOrchestrator;
import io.appgal.cloud.services.NetworkOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("activeNetwork")
public class ActiveNetwork {
    private static Logger logger = LoggerFactory.getLogger(ActiveNetwork.class);

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Inject
    private DeliveryOrchestrator deliveryOrchestrator;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Path("activeView")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getActiveView()
    {
        JsonObject activeView = this.networkOrchestrator.getActiveView();
        return activeView.toString();
    }

    @Path("pickUpRequest/result")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPickRequestResult(@PathParam("") String requestId)
    {
        JsonArray pickRequestResult = this.networkOrchestrator.getPickRequestResult(requestId);
        return pickRequestResult.toString();
    }

    @Path("/enterNetwork")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String enterNetwork(@RequestBody String jsonBody)
    {
        Profile profile = this.mongoDBJsonStore.getProfile("bugs.bunny.shah@gmail.com");
        FoodRunner foodRunner = new FoodRunner(profile, new Location(Double.parseDouble("30.25860595703125d"), Double.parseDouble("-97.74873352050781d")));
        this.networkOrchestrator.enterNetwork(foodRunner);
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("statusCode", "0");
        return responseJson.toString();
    }

    @Path("/findBestDestination")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String findBestDestination(@RequestBody String jsonBody)
    {
        Profile profile = this.mongoDBJsonStore.getProfile("bugs.bunny.shah@gmail.com");
        FoodRunner foodRunner = new FoodRunner(profile, new Location(Double.parseDouble("30.25860595703125d"), Double.parseDouble("-97.74873352050781d")));
        List<SourceOrg> sourceOrgs = this.deliveryOrchestrator.findBestDestination(foodRunner);
        return sourceOrgs.toString();
    }
}
