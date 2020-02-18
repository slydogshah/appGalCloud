package io.appgal.cloud.persistence;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.*;
import io.appgal.cloud.model.DestinationNotification;
import io.appgal.cloud.model.SourceNotification;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import com.mongodb.ConnectionString;

import com.mongodb.ServerAddress;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        List<String> topics = Arrays.asList(SourceNotification.TOPIC, DestinationNotification.TOPIC);

        MongoDatabase database = mongoClient.getDatabase("appgalcloud");

        MongoCollection<Document> collection = database.getCollection("kafkaDaemonBootstrapData");

        Document document = new Document();
        document.put("topics", topics);

        collection.insertOne(document);

        return topics;
    }
}
