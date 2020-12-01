package io.appgal.cloud.infrastructure;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.ClientSessionOptions;
import com.mongodb.client.*;
import com.mongodb.connection.ClusterDescription;
import io.appgal.cloud.model.*;
import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.model.FoodRunner;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.bson.Document;

import java.util.*;

@ApplicationScoped
public class MongoDBJsonStore {
    private static Logger logger = LoggerFactory.getLogger(MongoDBJsonStore.class);

    private MongoClient mongoClient;

    @PostConstruct
    public void start()
    {
        this.mongoClient = MongoClients.create();
    }

    @PreDestroy
    public void stop()
    {
        this.mongoClient.close();
    }

    public void storeProfile(Profile profile)
    {
        if(this.getProfile(profile.getEmail()) != null)
        {
            return;
        }
        if(profile.getProfileType() == null)
        {
            throw new RuntimeException("ProfileType: ISNULL");
        }
        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("profile");

        Document doc = Document.parse(profile.toString());
        collection.insertOne(doc);
    }

    public Profile getProfile(String email)
    {
        Profile profile = null;

        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("profile");

        String queryJson = "{\"email\":\""+email+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            profile = Profile.parse(documentJson);
            return profile;
        }

        return null;
    }

    public void storeSourceOrg(SourceOrg sourceOrg)
    {
        if(this.getSourceOrg(sourceOrg.getOrgId()) != null)
        {
            //it already exists
            return;
        }

        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("customers");

        Document doc = Document.parse(sourceOrg.toString());
        collection.insertOne(doc);

        for(Profile profile:sourceOrg.getProfiles())
        {
            this.storeProfile(profile);
        }
    }

    public SourceOrg getSourceOrg(String orgId)
    {
        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("customers");

        String queryJson = "{\"orgId\":\""+orgId+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            return SourceOrg.parse(documentJson);
        }
        return null;
    }

    public List<SourceOrg> getSourceOrgs()
    {
        List<SourceOrg> sourceOrgs = new ArrayList<>();

        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("customers");

        String queryJson = "{}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            SourceOrg sourceOrg = SourceOrg.parse(documentJson);
            sourceOrgs.add(sourceOrg);
        }

        return sourceOrgs;
    }

    public void storeActiveNetwork(Map<String, FoodRunner> activeFoodRunners)
    {
        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");

        List<Document> activeFoodRunnerDocs = new ArrayList<>();
        final Iterator<FoodRunner> iterator = activeFoodRunners.values().iterator();
        while(iterator.hasNext())
        {
            FoodRunner foodRunner = iterator.next();
            if(foodRunner.getProfile().getProfileType()==null || !foodRunner.getProfile().getProfileType().equals(ProfileType.FOOD_RUNNER))
            {
                continue;
            }
            String json = foodRunner.toString();
            Document doc = Document.parse(json);
            activeFoodRunnerDocs.add(doc);
        }
        collection.insertMany(activeFoodRunnerDocs);
    }

    public ActiveNetwork getActiveNetwork()
    {
        ActiveNetwork activeNetwork = new ActiveNetwork();

        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");

        String queryJson = "{}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();

            FoodRunner foodRunner = FoodRunner.parse(documentJson);
            activeNetwork.addActiveFoodRunner(foodRunner);
        }

        //Load the SourceOrgs
        activeNetwork.setSourceOrgs(this.getSourceOrgs());

        return activeNetwork;
    }

    public void deleteFoodRunner(FoodRunner foodRunner)
    {
        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");

        String json = "{}";

        collection.deleteMany(Document.parse(json));
    }

    public void storeDropOffNotification(DropOffNotification dropOffNotification)
    {
        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("dropOffNotifications");

        String json = dropOffNotification.toString();
        Document doc = Document.parse(json);

        collection.insertOne(doc);
    }

    public DropOffNotification findDropOffNotification(String dropOffNotificationId)
    {
        DropOffNotification dropOffNotification = new DropOffNotification();

        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("dropOffNotifications");

        String queryJson = "{}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            dropOffNotification = DropOffNotification.parse(documentJson);
        }

        return dropOffNotification;
    }

    public DropOffNotification findDropOffNotificationByFoodRunnerId(String foodRunnerId)
    {
        DropOffNotification dropOffNotification = new DropOffNotification();

        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("dropOffNotifications");

        JsonObject foodRunnerJson = new JsonObject();
        JsonObject profileJson = new JsonObject();
        profileJson.addProperty("id", foodRunnerId);
        foodRunnerJson.add("profile", profileJson);
        String queryJson = foodRunnerJson.toString();
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            dropOffNotification = DropOffNotification.parse(documentJson);
        }

        return dropOffNotification;
    }

    public void setCompletedTrip(CompletedTrip completedTrip)
    {
        MongoDatabase database = mongoClient.getDatabase("appgalcloud");
        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");

        collection.insertOne(Document.parse(completedTrip.getFoodRunner().toString()));
    }

    public List<CompletedTrip> getCompletedTrips()
    {
        List<CompletedTrip> completedTrips = new ArrayList<>();

        MongoDatabase database = mongoClient.getDatabase("appgalcloud");
        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");

        String queryJson = "{}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            CompletedTrip completedTrip = new CompletedTrip();
            Document document = cursor.next();
            String documentJson = document.toJson();

            FoodRunner foodRunner = FoodRunner.parse(documentJson);
            completedTrip.setFoodRunner(foodRunner);

            DropOffNotification dropOffNotification = this.findDropOffNotificationByFoodRunnerId(foodRunner.getProfile().getId());
            dropOffNotification.setFoodRunner(foodRunner);
            completedTrip.setDropOffNotification(dropOffNotification);

            completedTrips.add(completedTrip);
        }

        return completedTrips;
    }

    public void storeFoodRequest(FoodRequest foodRequest)
    {
        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("foodrequest");

        Document doc = Document.parse(foodRequest.toString());
        collection.insertOne(doc);
    }

    public FoodRequest getFoodRequest(String requestId)
    {
        FoodRequest foodRequest = new FoodRequest();

        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("foodrequest");

        String queryJson = "{\"id\":\""+requestId+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            foodRequest = FoodRequest.parse(documentJson);
        }

        return foodRequest;
    }

    public void storeResults(List<FoodRunner> results)
    {
        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("results");
        List<Document> documents = new ArrayList<>();
        for(FoodRunner foodRunner:results)
        {
            Document doc = Document.parse(foodRunner.toString());
            documents.add(doc);
        }
        collection.insertMany(documents);
    }

    public List<FoodRunner> getResults()
    {
        List<FoodRunner> results = new ArrayList<>();

        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("results");

        String queryJson = "{}}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            FoodRunner foodRunner = FoodRunner.parse(documentJson);
            results.add(foodRunner);
        }

        return results;
    }

    void cleanup()
    {
        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");
        collection.deleteMany(Document.parse("{}"));

        collection = database.getCollection("customers");
        collection.deleteMany(Document.parse("{}"));

        collection = database.getCollection("dropOffNotifications");
        collection.deleteMany(Document.parse("{}"));

        collection = database.getCollection("kafkaDaemonBootstrapData");
        collection.deleteMany(Document.parse("{}"));

        collection = database.getCollection("profile");
        collection.deleteMany(Document.parse("{}"));
    }

    public void storeScheduledPickUpNotification(SchedulePickUpNotification schedulePickUpNotification)
    {
        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("scheduledPickUpNotifications");

        Document doc = Document.parse(schedulePickUpNotification.toString());
        collection.insertOne(doc);
    }
}
