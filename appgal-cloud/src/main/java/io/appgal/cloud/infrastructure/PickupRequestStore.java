package io.appgal.cloud.infrastructure;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSUploadStream;
import io.appgal.cloud.model.FoodRecoveryTransaction;
import io.appgal.cloud.model.SchedulePickUpNotification;
import io.appgal.cloud.util.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PickupRequestStore {
    private static Logger logger = LoggerFactory.getLogger(PickupRequestStore.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    public List<SchedulePickUpNotification> getSchedulePickUpNotifications()
    {
        List<SchedulePickUpNotification> notifications = new ArrayList<>();

        MongoDatabase database = this.mongoDBJsonStore.getMongoDatabase();
        MongoCollection<Document> collection = database.getCollection("scheduledPickUpNotifications");

        String queryJson = "{}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            SchedulePickUpNotification notification = SchedulePickUpNotification.parse(documentJson);

            notifications.add(notification);
        }

        return notifications;
    }

    public List<SchedulePickUpNotification> getSchedulePickUpNotifications(String email)
    {
        List<SchedulePickUpNotification> notifications = new ArrayList<>();

        MongoDatabase database = this.mongoDBJsonStore.getMongoDatabase();
        MongoCollection<Document> collection = database.getCollection("scheduledPickUpNotifications");

        //Query Ex: {$and:[{"foodRunner.profile.email":"bugs.bunny.shah@gmail.com"},{"notificationSent":true}]}
        String queryJson = "{$and:[{\"foodRunner.profile.email\":\""+email+"\"},{\"notificationSent\":"+Boolean.TRUE.booleanValue()+"}]}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            SchedulePickUpNotification notification = SchedulePickUpNotification.parse(documentJson);
            notifications.add(notification);
        }

        return notifications;
    }

    public List<SchedulePickUpNotification> getPickUpNotificationsWithoutDropOff()
    {
        List<SchedulePickUpNotification> notifications = new ArrayList<>();

        MongoDatabase database = this.mongoDBJsonStore.getMongoDatabase();
        MongoCollection<Document> collection = database.getCollection("scheduledPickUpNotifications");

        //Query Ex: {$and:[{"foodRunner.profile.email":"bugs.bunny.shah@gmail.com"},{"notificationSent":true}]}
        String queryJson = "{\"isDropOffDynamic\":"+Boolean.TRUE.booleanValue()+"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            SchedulePickUpNotification notification = SchedulePickUpNotification.parse(documentJson);
            notifications.add(notification);
        }

        return notifications;
    }

    public List<SchedulePickUpNotification> getUnsentSchedulePickUpNotifications(String email)
    {
        List<SchedulePickUpNotification> notifications = new ArrayList<>();

        MongoDatabase database =this.mongoDBJsonStore.getMongoDatabase();
        MongoCollection<Document> collection = database.getCollection("scheduledPickUpNotifications");

        String queryJson = "{\"foodRunner.profile.email\":\""+email+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            SchedulePickUpNotification notification = SchedulePickUpNotification.parse(documentJson);
            if(!notification.isNotificationSent()) {
                notifications.add(notification);
            }
        }

        return notifications;
    }

    public void storeScheduledPickUpNotification(SchedulePickUpNotification schedulePickUpNotification)
    {
        MongoDatabase database = this.mongoDBJsonStore.getMongoDatabase();

        MongoCollection<Document> collection = database.getCollection("scheduledPickUpNotifications");

        Document doc = Document.parse(schedulePickUpNotification.toString());
        collection.insertOne(doc);
    }

    public SchedulePickUpNotification storeScheduledPickUpNotification(String foodPic, SchedulePickUpNotification schedulePickUpNotification)
    {
        MongoDatabase database = this.mongoDBJsonStore.getMongoDatabase();

        MongoCollection<Document> collection = database.getCollection("scheduledPickUpNotifications");

        if(foodPic != null) {
            ObjectId imageId = this.storeImage(database, new ByteArrayInputStream(foodPic.getBytes(StandardCharsets.UTF_8)));
            schedulePickUpNotification.getFoodDetails().setFoodPic(imageId.toHexString());
        }

        Document doc = Document.parse(schedulePickUpNotification.toString());
        collection.insertOne(doc);

        String queryJson = "{\"id\":\""+schedulePickUpNotification.getId()+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            return SchedulePickUpNotification.parse(documentJson);
        }

        return null;
    }

    public void updateScheduledPickUpNotification(SchedulePickUpNotification schedulePickUpNotification)
    {
        MongoDatabase database = this.mongoDBJsonStore.getMongoDatabase();
        MongoCollection<Document> collection = database.getCollection("scheduledPickUpNotifications");

        System.out.println("*************************************************");
        JsonUtil.print(this.getClass(),schedulePickUpNotification.toJson());
        System.out.println("*************************************************");

        JsonObject stored = this.getScheduledPickUpNotification(schedulePickUpNotification.getId());

        Bson bson = Document.parse(stored.toString());
        collection.deleteOne(bson);

        stored.remove("_id");
        stored.addProperty("notificationSent", true);
        this.storeScheduledPickUpNotification(SchedulePickUpNotification.parse(stored.toString()));
    }

    public JsonObject getScheduledPickUpNotification(String id)
    {
        MongoDatabase database = this.mongoDBJsonStore.getMongoDatabase();
        MongoCollection<Document> collection = database.getCollection("scheduledPickUpNotifications");

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

    private ObjectId storeImage(MongoDatabase mongoDatabase, InputStream imageStream)
    {
        GridFSUploadStream uploadStream = null;
        try {
            GridFSBucket bucket = GridFSBuckets.create(
                    mongoDatabase,
                    "images");
            uploadStream = bucket.openUploadStream(UUID.randomUUID().toString());

            byte[] data = IOUtils.toByteArray(imageStream);
            uploadStream.write(data) ;


            uploadStream.close();

            ObjectId fileid = uploadStream.getObjectId() ;

            return fileid;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
        finally
        {
            if(uploadStream != null)
            {
                uploadStream.close();
            }
        }
    }
}
