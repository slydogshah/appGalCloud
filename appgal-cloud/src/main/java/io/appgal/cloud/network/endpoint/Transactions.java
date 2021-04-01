package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.FoodRecoveryTransaction;
import io.appgal.cloud.model.TransactionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.List;

@Path("tx")
public class Transactions {
    private static Logger logger = LoggerFactory.getLogger(Transactions.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;


    @Path("/recovery")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFoodRecoveryTransactions(@QueryParam("email") String email)
    {
        try
        {
            JsonObject result = new JsonObject();
            JsonArray pending = new JsonArray();
            JsonArray inProgress = new JsonArray();
            List<FoodRecoveryTransaction> transactions = this.mongoDBJsonStore.getFoodRecoveryTransactions(email);
            for(FoodRecoveryTransaction cour: transactions) {
                if (cour.getTransactionState() == TransactionState.SUBMITTED) {
                    pending.add(cour.toJson());
                } else if (cour.getTransactionState() == TransactionState.INPROGRESS ||
                        cour.getTransactionState() == TransactionState.ONTHEWAY) {
                    inProgress.add(cour.toJson());
                }
            }
            result.add("pending", pending);
            result.add("inProgress", inProgress);
            return Response.ok(result.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("/recovery/history")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFoodRecoveryTransactionHistory(@QueryParam("orgId") String orgId)
    {
        try {
            List<FoodRecoveryTransaction> txs = this.mongoDBJsonStore.getFoodRecoveryTransactionHistory(orgId);
            return Response.ok(txs.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("/dropOff/history")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFoodRecoveryDropOffHistory(@QueryParam("orgId") String orgId)
    {
        try {
            List<FoodRecoveryTransaction> txs = this.mongoDBJsonStore.getFoodRecoveryDropOffHistory(orgId);
            return Response.ok(txs.toString()).build();
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