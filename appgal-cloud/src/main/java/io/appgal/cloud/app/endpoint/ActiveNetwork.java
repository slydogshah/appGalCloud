package io.appgal.cloud.app.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.network.services.FoodRecoveryOrchestrator;
import io.appgal.cloud.network.services.NetworkOrchestrator;

import io.appgal.cloud.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Path("activeNetwork")
public class ActiveNetwork {
    private static Logger logger = LoggerFactory.getLogger(ActiveNetwork.class);

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Inject
    private FoodRecoveryOrchestrator foodRecoveryOrchestrator;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Path("activeView")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActiveView()
    {
        try {
            JsonObject activeView = this.networkOrchestrator.getActiveView();
            return Response.ok(activeView.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("/enterNetwork")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response enterNetwork(@QueryParam("email") String email)
    {
        try {
            Profile profile = this.mongoDBJsonStore.getProfile(email);
            if (profile == null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("message", "BAD_REQUEST: \"+email+\" is not a registered user");
                jsonObject.addProperty("email", email);
                return Response.status(400).entity(jsonObject.toString()).build();
            }

            FoodRunner foodRunner = new FoodRunner(profile);
            this.networkOrchestrator.enterNetwork(foodRunner);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("success", true);
            return Response.ok(jsonObject.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    //TODO
    @Path("/findBestDestination")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response findBestDestination(@RequestBody String jsonBody)
    {
        try {
            Profile profile = this.mongoDBJsonStore.getProfile("bugs.bunny.shah@gmail.com");
            FoodRunner foodRunner = new FoodRunner(profile, new Location(Double.parseDouble("30.25860595703125d"), Double.parseDouble("-97.74873352050781d")));
            List<SourceOrg> sourceOrgs = this.networkOrchestrator.findBestDestination(foodRunner);
            return Response.ok(sourceOrgs.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    /*@Path("/sendDeliveryNotification")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendDeliveryNotification(@RequestBody String jsonBody)
    {
        //DropOffNotification dropOffNotification = DropOffNotification.parse(jsonBody);
        //this.deliveryOrchestrator.sendDeliveryNotification(dropOffNotification);

        return Response.ok().build();
    }*/

    @Path("sourceOrgs")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSourceOrgs()
    {
        try {
            List<SourceOrg> sourceOrgs = this.mongoDBJsonStore.getSourceOrgs();
            return Response.ok(sourceOrgs.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("/accept")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response accept(@RequestBody String jsonBody)
    {
        try {
            JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();
            //JsonUtil.print(this.getClass(),json);
            String email = json.get("email").getAsString();
            String accepted = json.get("accepted").getAsString();

            FoodRunner foodRunner = this.networkOrchestrator.getActiveNetwork().findFoodRunnerByEmail(email);

            FoodRecoveryTransaction tx = this.mongoDBJsonStore.getFoodRecoveryTransaction(accepted);
            tx.setFoodRunner(foodRunner);
            tx.setTransactionState(TransactionState.INPROGRESS);

            SchedulePickUpNotification pickUpNotification = tx.getPickUpNotification();
            pickUpNotification.setFoodRunner(foodRunner);

            //JsonUtil.print(this.getClass(),tx.toJson());

            JsonObject responseJson = this.networkOrchestrator.acceptRecoveryTransaction(tx);

            return Response.ok(responseJson.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("/scheduleDropOff")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response scheduleDropOff(@RequestBody String jsonBody)
    {
        try {
            JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();
            String txId = json.get("txId").getAsString();

            FoodRecoveryTransaction tx = this.mongoDBJsonStore.getFoodRecoveryTransaction(txId);
            this.foodRecoveryOrchestrator.notifyDropOff(tx);

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", true);
            return Response.ok(responseJson.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("/foodPickedUp")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response foodPickedUp(@RequestBody String jsonBody)
    {
        try {
            JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();
            String txId = json.get("txId").getAsString();

            FoodRecoveryTransaction tx = this.mongoDBJsonStore.getFoodRecoveryTransaction(txId);
            tx.setTransactionState(TransactionState.ONTHEWAY);
            this.mongoDBJsonStore.storeFoodRecoveryTransaction(tx);

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success",true);
            return Response.ok(responseJson.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("/foodPickedUp")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFoodPickedUp(@QueryParam("email") String email)
    {
        try {
            List<FoodRecoveryTransaction> txs = this.mongoDBJsonStore.getPickedUpTransactions(email);
            return Response.ok(JsonParser.parseString(txs.toString()).getAsJsonArray().toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("/notifyDelivery")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response notifyDelivery(@RequestBody String jsonBody)
    {
        try {
            JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();
            String email = json.get("email").getAsString();
            String txId = json.get("txId").getAsString();
            FoodRecoveryTransaction tx = this.mongoDBJsonStore.getFoodRecoveryTransaction(txId);

            this.foodRecoveryOrchestrator.notifyDelivery(tx);

            JsonArray pending = new JsonArray();
            JsonArray inProgress = new JsonArray();
            List<FoodRecoveryTransaction> transactions = this.networkOrchestrator.findMyTransactions(email);
            for(FoodRecoveryTransaction cour: transactions) {
                if (cour.getTransactionState() == TransactionState.SUBMITTED)
                {
                    cour.getPickUpNotification().setNotificationSent(true);
                    SchedulePickUpNotification courPickUp = SchedulePickUpNotification.parse(this.mongoDBJsonStore.
                            getScheduledPickUpNotification(cour.getPickUpNotification().getId()).toString());
                    courPickUp.setNotificationSent(true);
                    this.mongoDBJsonStore.storeFoodRecoveryTransaction(cour);
                    this.mongoDBJsonStore.storeScheduledPickUpNotification(courPickUp);
                    pending.add(cour.toJson());
                }
                else if(cour.getTransactionState() == TransactionState.INPROGRESS || cour.getTransactionState() == TransactionState.ONTHEWAY)
                {
                    inProgress.add(cour.toJson());
                }
            }

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", true);
            responseJson.add("pending",pending);
            responseJson.add("inProgress",inProgress);
            return Response.ok(responseJson.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }
}
