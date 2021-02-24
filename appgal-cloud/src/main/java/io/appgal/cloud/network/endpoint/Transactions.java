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
        List<FoodRecoveryTransaction> txs = this.mongoDBJsonStore.getFoodRecoveryTransactions(email);

        JsonObject result = new JsonObject();
        JsonArray pending = new JsonArray();
        JsonArray inprogress = new JsonArray();
        JsonArray transactions = JsonParser.parseString(txs.toString()).getAsJsonArray();
        Iterator<JsonElement> itr = transactions.iterator();
        while(itr.hasNext()) {
            JsonObject transaction = itr.next().getAsJsonObject();
            FoodRecoveryTransaction tx = FoodRecoveryTransaction.parse(transaction.toString());

            if(tx.getTransactionState() == TransactionState.SUBMITTED)
            {
                pending.add(tx.toJson());
            }
            else if(tx.getTransactionState() == TransactionState.INPROGRESS || tx.getTransactionState() == TransactionState.ONTHEWAY)
            {
                inprogress.add(tx.toJson());
            }

            pending.add(transaction);
        }
        result.add("pending",pending);
        result.add("inProgress",inprogress);


        return Response.ok(result.toString()).build();
    }

    @Path("/recovery/history")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFoodRecoveryTransactionHistory(@QueryParam("orgId") String orgId)
    {
        List<FoodRecoveryTransaction> txs = this.mongoDBJsonStore.getFoodRecoveryTransactionHistory(orgId);
        return Response.ok(txs.toString()).build();
    }

    @Path("/dropOff/history")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFoodRecoveryDropOffHistory(@QueryParam("orgId") String orgId)
    {
        List<FoodRecoveryTransaction> txs = this.mongoDBJsonStore.getFoodRecoveryDropOffHistory(orgId);
        return Response.ok(txs.toString()).build();
    }
}