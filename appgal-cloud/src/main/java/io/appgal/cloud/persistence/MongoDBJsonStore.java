package io.appgal.cloud.persistence;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.*;
import io.appgal.cloud.model.*;
import io.appgal.cloud.network.model.ActiveNetwork;
import io.appgal.cloud.network.model.FoodRunner;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import com.mongodb.ConnectionString;

import com.mongodb.ServerAddress;

import org.bson.Document;

import java.util.*;

import com.mongodb.Block;

import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;

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

    public JsonArray findDestinationNotifications(List<String> notificationIds)
    {
        JsonArray destinationNotifications = new JsonArray();

        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("destinationNotifications");

        String queryJson = "{}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            JsonObject jsonObject = JsonParser.parseString(documentJson).getAsJsonObject();
            destinationNotifications.add(jsonObject);
        }

        return destinationNotifications;
    }

    public void storeDestinationNotifications(DestinationNotification destinationNotification)
    {
        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("destinationNotifications");

        String json = destinationNotification.toString();
        Document doc = Document.parse(json);

        collection.insertOne(doc);
    }

    public List<String> findKafakaDaemonBootstrapData()
    {
        List<String> topics = new ArrayList<>();
        topics.add(SourceNotification.TOPIC);
        topics.add(DestinationNotification.TOPIC);
        topics.add(ActiveFoodRunnerData.TOPIC);

        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("kafkaDaemonBootstrapData");

        Document document = new Document();
        document.put("topics", topics);

        collection.insertOne(document);

        return topics;
    }

    /*public void storeActiveFoodRunnerData(List<ActiveFoodRunnerData> activeFoodRunnerData)
    {
        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");

        List<Document> activeFoodRunners = new ArrayList<>();
        for(ActiveFoodRunnerData local:activeFoodRunnerData)
        {
            Document doc = Document.parse(local.toString());
            activeFoodRunners.add(doc);
        }
        collection.insertMany(activeFoodRunners);
    }*/

    public void storeProfile(Profile profile)
    {
        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("profile");

        Document doc = Document.parse(profile.toString());
        collection.insertOne(doc);
    }

    public Profile getProfile(String email)
    {
        Profile profile = new Profile();

        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("profile");

        //TODO: OPTIMIZE_THIS_QUERY ASSIGNED_TO: @bugs.bunny.shah@gmail.com
        String queryJson = "{}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            JsonObject jsonObject = JsonParser.parseString(documentJson).getAsJsonObject();
            profile = Profile.parseProfile(jsonObject.toString());
        }

        return profile;
    }

    public Profile getProfileById(String profileId)
    {
        Profile profile = new Profile();

        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("profile");

        //String queryJson = "{\"id\":\""+profileId+"\"}";
        //System.out.println(queryJson);
        String queryJson = "{}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            JsonObject jsonObject = JsonParser.parseString(documentJson).getAsJsonObject();
            if(jsonObject.get("id").getAsString().equals(profileId)) {
                profile = Profile.parseProfile(jsonObject.toString());
                return profile;
            }
        }

        return profile;
    }

    public void storeSourceOrg(SourceOrg sourceOrg)
    {
        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("customers");

        Document doc = Document.parse(sourceOrg.toString());
        collection.insertOne(doc);
    }

    public SourceOrg getSourceOrg()
    {
        SourceOrg sourceOrg = new SourceOrg();

        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("customers");

        //TODO: OPTIMIZE_THIS_QUERY ASSIGNED_TO: @bugs.bunny.shah@gmail.com
        String queryJson = "{}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            JsonObject jsonObject = JsonParser.parseString(documentJson).getAsJsonObject();
            sourceOrg = SourceOrg.parseJson(jsonObject);
        }

        return sourceOrg;
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

            JsonObject jsonObject = JsonParser.parseString(documentJson).getAsJsonObject();
            String foodRunnerId = jsonObject.get("foodRunnerId").getAsString();
            Profile profile = this.getProfileById(foodRunnerId);



            FoodRunner foodRunner = FoodRunner.parse(documentJson);
            activeNetwork.addActiveFoodRunner(foodRunner);
        }

        return activeNetwork;
    }
}
