package io.appgal.cloud.infrastructure;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.appgal.cloud.model.DropOffNotification;
import io.appgal.cloud.model.ScheduleDropOffNotification;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DropOffStore {
    private static Logger logger = LoggerFactory.getLogger(DropOffStore.class);

    public DropOffNotification findDropOffNotificationByFoodRunnerId(MongoDatabase database, String foodRunnerId)
    {
        DropOffNotification dropOffNotification = new DropOffNotification();

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

    public void storeScheduledDropOffNotification(MongoDatabase database, ScheduleDropOffNotification scheduleDropOffNotification)
    {
        MongoCollection<Document> collection = database.getCollection("scheduledDropOffNotifications");

        Document doc = Document.parse(scheduleDropOffNotification.toString());
        collection.insertOne(doc);
    }

    public void updateScheduledDropOffNotification(MongoDatabase database, ScheduleDropOffNotification notification)
    {
        MongoCollection<Document> collection = database.getCollection("scheduledDropOffNotifications");

        JsonObject stored = this.getScheduledDropOffNotification(database, notification.getId());
        Bson bson = Document.parse(stored.toString());
        collection.deleteOne(bson);

        stored.remove("_id");
        stored.addProperty("notificationSent", true);
        this.storeScheduledDropOffNotification(database, ScheduleDropOffNotification.parse(stored.toString()));
    }

    public JsonObject getScheduledDropOffNotification(MongoDatabase database, String id)
    {
        MongoCollection<Document> collection = database.getCollection("scheduledDropOffNotifications");

        String queryJson = "{\"id\":\""+id+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            return JsonParser.parseString(documentJson).getAsJsonObject();
        }
        return null;
    }

    public List<ScheduleDropOffNotification> getScheduledDropOffNotifications(MongoDatabase database, String orgId)
    {
        List<ScheduleDropOffNotification> notifications = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("scheduledDropOffNotifications");

        //Query: {$and:[{"sourceOrg.orgId":"microsoft"},{"notificationSent":true}]}
        String queryJson = "{$and:[{\"sourceOrg.orgId\":\""+orgId+"\"},{\"notificationSent\":"+Boolean.TRUE.booleanValue()+"}]}";
        logger.info(queryJson);
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            notifications.add(ScheduleDropOffNotification.parse(documentJson));
        }
        return notifications;
    }

    public List<ScheduleDropOffNotification> getScheduledDropOffNotifications(MongoDatabase database)
    {
        List<ScheduleDropOffNotification> notifications = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("scheduledDropOffNotifications");

        String queryJson = "{}";
        logger.info(queryJson);
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            notifications.add(ScheduleDropOffNotification.parse(documentJson));
        }
        return notifications;
    }
}
