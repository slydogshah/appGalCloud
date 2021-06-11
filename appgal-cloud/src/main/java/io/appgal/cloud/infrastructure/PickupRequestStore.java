package io.appgal.cloud.infrastructure;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSUploadStream;
import io.appgal.cloud.model.SchedulePickUpNotification;
import io.appgal.cloud.util.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

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
        OffsetDateTime tomorrow = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1);
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            SchedulePickUpNotification notification = SchedulePickUpNotification.parse(documentJson);

            if(notification.getStart().toEpochSecond() < tomorrow.toEpochSecond()) {
                notifications.add(notification);
            }
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
        try {
            MongoDatabase database = this.mongoDBJsonStore.getMongoDatabase();

            MongoCollection<Document> collection = database.getCollection("scheduledPickUpNotifications");

            if (foodPic != null) {
                //System.out.println(foodPic);

                String encodedImg = null;
                String imageType = "jpg";
                if(foodPic.startsWith("data:image")) {
                    String[] tokens = foodPic.split(",");
                    imageType = tokens[0];
                    imageType = imageType.split(":")[1];
                    imageType = imageType.split(";")[0];
                    imageType = imageType.split("/")[1];
                    encodedImg = foodPic.split(",")[1];
                }
                else
                {
                    encodedImg = foodPic;
                }

                //System.out.println(encodedImg);
                /*File file = new File("/Users/babyboy/mamasboy/appgallabs/jen/mumma/appGalCloud/appgal-cloud/src/main/resources/encodedImage");
                if(file.exists())
                {
                    file.delete();
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(encodedImg.getBytes(StandardCharsets.UTF_8));*/

                byte[] imageByte = Base64.getDecoder().decode(encodedImg.getBytes(StandardCharsets.UTF_8));
                ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                BufferedImage image = ImageIO.read(bis);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(image, imageType, bos);
                byte[] data = bos.toByteArray();


                ObjectId imageId = this.storeImage(database,
                        new ByteArrayInputStream(data)
                        //Thread.currentThread().getContextClassLoader().getResourceAsStream("foodpic.jpeg")
                );
                schedulePickUpNotification.getFoodDetails().setFoodPic(imageId.toHexString());
            }

            Document doc = Document.parse(schedulePickUpNotification.toString());
            collection.insertOne(doc);

            String queryJson = "{\"id\":\"" + schedulePickUpNotification.getId() + "\"}";
            Bson bson = Document.parse(queryJson);
            FindIterable<Document> iterable = collection.find(bson);
            MongoCursor<Document> cursor = iterable.cursor();
            while (cursor.hasNext()) {
                Document document = cursor.next();
                String documentJson = document.toJson();
                return SchedulePickUpNotification.parse(documentJson);
            }

            return null;
        }
        catch(Exception ioException)
        {
            logger.error(ioException.getMessage(),ioException);
            throw new RuntimeException(ioException);
        }
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

    public byte[] getImage(MongoDatabase mongoDatabase, ObjectId fileId)
    {
        GridFSDownloadStream downloadStream = null;
        try
        {
            GridFSBucket bucket = GridFSBuckets.create(
                    mongoDatabase,
                    "images");
            downloadStream = bucket.openDownloadStream(fileId);
            int fileLength = (int) downloadStream.getGridFSFile().getLength();
            byte[] bytesToWriteTo = new byte[fileLength];
            downloadStream.read(bytesToWriteTo);

            return bytesToWriteTo;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
        finally
        {
            if(downloadStream != null)
            {
                downloadStream.close();
            }
        }
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
