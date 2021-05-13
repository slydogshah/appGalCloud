package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.FoodRecoveryTransaction;
import io.appgal.cloud.model.SchedulePickUpNotification;
import io.appgal.cloud.model.TransactionState;
import io.appgal.cloud.network.services.NetworkOrchestrator;
import io.appgal.cloud.util.JsonUtil;
import io.smallrye.mutiny.Uni;
import org.apache.commons.io.IOUtils;
import org.bson.internal.Base64;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Path("tx")
public class Transactions {
    private static Logger logger = LoggerFactory.getLogger(Transactions.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Path("/recovery/foodRunner")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFoodRecoveryTransactionsByRunner(@QueryParam("email") String email)
    {
        try
        {
            JsonObject result = new JsonObject();
            JsonArray pending = new JsonArray();
            List<FoodRecoveryTransaction> transactions = this.networkOrchestrator.findMyTransactions(email);
            //JsonUtil.print(this.getClass(),JsonParser.parseString(transactions.toString()));

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
            }
            result.add("pending", pending);

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


    @Path("/push/recovery")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPushTransactions(@QueryParam("email") String email)
    {
        try
        {
            JsonObject result = new JsonObject();
            JsonArray pending = new JsonArray();
            List<FoodRecoveryTransaction> transactions = this.networkOrchestrator.findMyTransactions(email);

            //JsonUtil.print(this.getClass(),JsonParser.parseString(transactions.toString()));


            for(FoodRecoveryTransaction cour: transactions) {
                if (cour.getTransactionState() == TransactionState.SUBMITTED)
                {
                    if(!cour.getPickUpNotification().isNotificationSent()) {
                        cour.getPickUpNotification().setNotificationSent(true);
                        SchedulePickUpNotification courPickUp = SchedulePickUpNotification.parse(this.mongoDBJsonStore.
                                getScheduledPickUpNotification(cour.getPickUpNotification().getId()).toString());
                        courPickUp.setNotificationSent(true);
                        this.mongoDBJsonStore.storeFoodRecoveryTransaction(cour);
                        this.mongoDBJsonStore.storeScheduledPickUpNotification(courPickUp);
                        pending.add(cour.toJson());
                    }
                }
            }
            result.add("pending", pending);

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


    @Path("/recovery")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFoodRecoveryTransactions(@QueryParam("orgId") String orgId)
    {
        try
        {
            JsonObject result = new JsonObject();
            JsonArray pending = new JsonArray();
            JsonArray inProgress = new JsonArray();
            List<FoodRecoveryTransaction> transactions = this.mongoDBJsonStore.getFoodRecoveryTransactions(orgId);

            JsonUtil.print(this.getClass(),JsonParser.parseString(transactions.toString()));


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

            List<FoodRecoveryTransaction> history = this.mongoDBJsonStore.getFoodRecoveryTransactionHistory(orgId);
            boolean historyExists = false;
            if(!history.isEmpty())
            {
                historyExists = true;
            }
            result.addProperty("historyExists",historyExists);
            result.add("history",JsonParser.parseString(history.toString()).getAsJsonArray());
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

    @Path("/dropoff")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFoodRecoveryDropOffTransactions(@QueryParam("orgId") String orgId)
    {
        try
        {
            JsonObject result = new JsonObject();
            JsonArray pending = new JsonArray();
            JsonArray inProgress = new JsonArray();
            List<FoodRecoveryTransaction> transactions = this.mongoDBJsonStore.getFoodRecoveryDropOffTransactions(orgId);
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

            List<FoodRecoveryTransaction> history = this.mongoDBJsonStore.getFoodRecoveryDropOffHistory(orgId);
            boolean historyExists = false;
            if(!history.isEmpty())
            {
                historyExists = true;
            }
            result.addProperty("historyExists",historyExists);
            result.add("history",JsonParser.parseString(history.toString()).getAsJsonArray());
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

    @Path("/recovery/transaction")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFoodRecoveryTransaction(@QueryParam("id") String id)
    {
        try {
            FoodRecoveryTransaction tx = this.mongoDBJsonStore.getFoodRecoveryTransaction(id);
            return Response.ok(tx.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("/recovery/transaction/foodPic")
    @GET
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
    public Response getFoodPic(@QueryParam("id") String id)
    {
        try {
            FoodRecoveryTransaction tx = this.mongoDBJsonStore.getFoodRecoveryTransaction(id);
            ObjectId imageId = new ObjectId(tx.getPickUpNotification().getFoodDetails().getFoodPic());
            byte[] data = this.mongoDBJsonStore.getImage(imageId);
            return Response.ok( (StreamingOutput) output -> {
                try {
                    logger.info(data.length+"");
                    InputStream input = new ByteArrayInputStream(data);
                    IOUtils.copy(input,output);
                    output.flush();
                } catch ( Exception e ) { e.printStackTrace(); }
            } ).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @GET
    @Path("/tx/img")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public javax.ws.rs.core.Response downloadFile() throws Exception {
        FoodRecoveryTransaction tx = this.mongoDBJsonStore.getFoodRecoveryTransaction("2a9029d9-41a5-4434-91eb-962afc5dd576");

        ObjectId imageId = new ObjectId(tx.getPickUpNotification().getFoodDetails().getFoodPic());
        byte[] data = this.mongoDBJsonStore.getImage(imageId);
        return Response.ok( (StreamingOutput) output -> {
            try {
                logger.info(data.length+"");
                InputStream input = new ByteArrayInputStream(data);
                IOUtils.copy(input,output);
                output.flush();
            } catch ( Exception e ) { e.printStackTrace(); }
        } ).build();
    }
}