package io.appgal.cloud.infrastructure;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.appgal.cloud.model.FoodRecoveryTransaction;
import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.ProfileType;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FoodRunnerStore {
    private static Logger logger = LoggerFactory.getLogger(FoodRunnerStore.class);

    public List<FoodRunner> getAllFoodRunners(MongoDatabase database)
    {
        List<FoodRunner> foodRunners = new ArrayList<>();

        MongoCollection<Document> collection = database.getCollection("profile");

        FindIterable<Document> iterable = collection.find();
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            Profile profile = Profile.parse(documentJson);
            if(profile.getProfileType() == ProfileType.FOOD_RUNNER)
            {
                FoodRunner foodRunner = new FoodRunner(profile);
                foodRunners.add(foodRunner);
            }
        }

        return foodRunners;
    }

    public FoodRunner getFoodRunner(MongoDatabase database, String email)
    {
        FoodRunner foodRunner = null;

        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");

        String queryJson = "{\"profile.email\":\""+email+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            foodRunner = FoodRunner.parse(documentJson);
            return foodRunner;
        }

        return null;
    }

    public void deleteFoodRunner(MongoDatabase database, FoodRunner foodRunner)
    {
        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");
        collection.deleteMany(new Document());
    }

    public void storeResults(MongoDatabase database, List<FoodRunner> results)
    {
        MongoCollection<Document> collection = database.getCollection("results");
        List<Document> documents = new ArrayList<>();
        for(FoodRunner foodRunner:results)
        {
            Document doc = Document.parse(foodRunner.toString());
            documents.add(doc);
        }
        collection.insertMany(documents);
    }

    public void updateFoodRunner(MongoDatabase database, FoodRunner foodRunner)
    {
        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");
        //logger.info("*******************************STORE************************************************************************************");
        //JsonUtil.print(FoodRecoveryStore.class,foodRecoveryTransaction.toJson());
        //logger.info("*******************************************************************************************************************");

        String queryJson = "{\"profile.email\":\""+foodRunner.getProfile().getEmail()+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            collection.deleteOne(document);
        }

        Document doc = Document.parse(foodRunner.toString());
        collection.insertOne(doc);
    }
}
