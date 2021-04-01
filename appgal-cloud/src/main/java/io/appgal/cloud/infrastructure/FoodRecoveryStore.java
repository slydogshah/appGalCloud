package io.appgal.cloud.infrastructure;

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

    public void storeFoodRecoveryTransaction(MongoDatabase database,FoodRecoveryTransaction foodRecoveryTransaction)
    {
        MongoCollection<Document> collection = database.getCollection("foodRecoveryTransaction");
        logger.info("*******************************STORE************************************************************************************");
        JsonUtil.print(FoodRecoveryStore.class,foodRecoveryTransaction.toJson());
        logger.info("*******************************************************************************************************************");

        Document doc = Document.parse(foodRecoveryTransaction.toString());
        collection.insertOne(doc);
    }

    public List<FoodRecoveryTransaction> getFoodRecoveryTransactions(MongoDatabase database,String email)
    {
        List<FoodRecoveryTransaction> list = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("foodRecoveryTransaction");

        String queryJson = "{\"pickupNotification.sourceOrg.orgContactEmail\":\""+email+"\"}";
        //String queryJson = "{}";
        logger.info(queryJson);
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
        logger.info(queryJson);
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
        String queryJson = "{$and:[{\"dropOffNotification.sourceOrg.orgId\":\""+orgId+"\"},{\"transactionState\":\""+TransactionState.CLOSED+"\"}]}";
        logger.info(queryJson);
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
