package io.appgal.cloud.infrastructure;

import com.google.gson.JsonParser;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.appgal.cloud.model.FoodRecoveryTransaction;
import io.appgal.cloud.model.TransactionState;
import io.appgal.cloud.util.JsonUtil;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FoodRecoveryStore {
    private static Logger logger = LoggerFactory.getLogger(FoodRecoveryStore.class);

    public FoodRecoveryTransaction storeFoodRecoveryTransaction(MongoDatabase database,FoodRecoveryTransaction foodRecoveryTransaction)
    {
        MongoCollection<Document> collection = database.getCollection("foodRecoveryTransaction");
        //logger.info("*******************************STORE************************************************************************************");
        //JsonUtil.print(FoodRecoveryStore.class,foodRecoveryTransaction.toJson());
        //logger.info("*******************************************************************************************************************");

        FoodRecoveryTransaction exists = null;
        String queryJson = "{\"id\":\""+foodRecoveryTransaction.getId()+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            exists = FoodRecoveryTransaction.parse(documentJson);
        }
        if(exists != null)
        {
            bson = Document.parse(exists.toString());
            collection.deleteOne(bson);
        }


        Document doc = Document.parse(foodRecoveryTransaction.toString());
        collection.insertOne(doc);


        bson = Document.parse(queryJson);
        iterable = collection.find(bson);
        cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            return FoodRecoveryTransaction.parse(documentJson);
        }

        return null;
    }

    public FoodRecoveryTransaction getFoodRecoveryTransaction(MongoDatabase database,String id)
    {
        MongoCollection<Document> collection = database.getCollection("foodRecoveryTransaction");
        //logger.info("*******************************STORE************************************************************************************");
        //JsonUtil.print(FoodRecoveryStore.class,foodRecoveryTransaction.toJson());
        //logger.info("*******************************************************************************************************************");

        FoodRecoveryTransaction exists = null;
        String queryJson = "{\"id\":\""+id+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            return FoodRecoveryTransaction.parse(documentJson);
        }
        return null;
    }

    public List<FoodRecoveryTransaction> getFoodRecoveryTransactions(MongoDatabase database,String orgId)
    {
        List<FoodRecoveryTransaction> list = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("foodRecoveryTransaction");

        String queryJson = "{\"pickupNotification.sourceOrg.orgId\":\""+orgId+"\"}";
        //String queryJson = "{}";
        //logger.info(queryJson);
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            list.add(FoodRecoveryTransaction.parse(documentJson));
        }
        return list;
    }

    public List<FoodRecoveryTransaction> getFoodRecoveryTransactions(MongoDatabase database)
    {
        List<FoodRecoveryTransaction> list = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("foodRecoveryTransaction");

        String queryJson = "{}";
        //logger.info(queryJson);
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            list.add(FoodRecoveryTransaction.parse(documentJson));
        }
        return list;
    }

    public List<FoodRecoveryTransaction> getFoodRecoveryDropOffTransactions(MongoDatabase database,String orgId)
    {
        List<FoodRecoveryTransaction> list = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("foodRecoveryTransaction");

        //Query: {$and:[{"sourceOrg.orgId":"microsoft"},{"notificationSent":true}]}
        String queryJson = "{$and:[{\"dropOffNotification.sourceOrg.orgId\":\""+orgId+"\"},{\"transactionState\":\""+TransactionState.INPROGRESS+"\"}]}";
        //logger.info(queryJson);
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            list.add(FoodRecoveryTransaction.parse(documentJson));
        }
        return list;
    }

    public List<FoodRecoveryTransaction> getFoodRecoveryTransactionHistory(MongoDatabase database,String orgId)
    {
        List<FoodRecoveryTransaction> list = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("foodRecoveryTransaction");

        //Query: {$and:[{"sourceOrg.orgId":"microsoft"},{"notificationSent":true}]}
        String queryJson = "{$and:[{\"pickupNotification.sourceOrg.orgId\":\""+orgId+"\"},{\"transactionState\":\""+ TransactionState.CLOSED+"\"}]}";
        //JsonUtil.print(this.getClass(), JsonParser.parseString(queryJson));
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            list.add(FoodRecoveryTransaction.parse(documentJson));
        }
        return list;
    }

    public List<FoodRecoveryTransaction> getFoodRecoveryDropOffHistory(MongoDatabase database,String orgId)
    {
        List<FoodRecoveryTransaction> list = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("foodRecoveryTransaction");

        //Query: {$and:[{"sourceOrg.orgId":"microsoft"},{"notificationSent":true}]}
        String queryJson = "{$and:[{\"pickupNotification.dropOffOrg.orgId\":\""+orgId+"\"},{\"transactionState\":\""+TransactionState.CLOSED+"\"}]}";
        //logger.info(queryJson);
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            list.add(FoodRecoveryTransaction.parse(documentJson));
        }
        return list;
    }
}
