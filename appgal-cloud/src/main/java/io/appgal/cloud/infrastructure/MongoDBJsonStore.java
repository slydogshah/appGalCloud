package io.appgal.cloud.infrastructure;

import io.appgal.cloud.model.*;
import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.model.FoodRunner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.mongodb.client.*;

import org.bson.conversions.Bson;
import org.bson.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.text.MessageFormat;
import java.util.*;

@ApplicationScoped
public class MongoDBJsonStore {
    private static Logger logger = LoggerFactory.getLogger(MongoDBJsonStore.class);

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;


    private String database = "jennetwork";
    private String password = "jen";

    @ConfigProperty(name = "mongodbHost")
    private String mongodbHost;

    @ConfigProperty(name = "mongoDBConnectionString")
    private String mongodbConnectionString;

    @ConfigProperty(name = "mongodbPort")
    private String mongodbPort;

    @Inject
    private OrgStore orgStore;

    @Inject
    private ProfileStore profileStore;

    @Inject
    private NetworkStore networkStore;

    @Inject
    private FoodRunnerStore foodRunnerStore;

    @Inject
    private DropOffStore dropOffStore;

    @Inject
    private TripStore tripStore;

    @Inject
    private FoodRecoveryStore foodRecoveryStore;

    @Inject
    private PickupRequestStore pickupRequestStore;

    @PostConstruct
    public void start()
    {
        String connectionString = MessageFormat.format(this.mongodbConnectionString,this.password,this.mongodbHost,this.database);
        logger.info("************");
        logger.info(connectionString);
        logger.info("************");
        this.mongoClient = MongoClients.create(connectionString);
        this.mongoDatabase = mongoClient.getDatabase(this.database);

        //this.mongoClient = MongoClients.create();
    }

    @PreDestroy
    public void stop()
    {
        this.mongoClient.close();
    }

    public MongoClient getMongoClient()
    {
        return this.mongoClient;
    }

    public void clearAllProfiles()
    {
        this.profileStore.clearAllProfiles(this.mongoDatabase);
    }

    public void storeProfile(Profile profile)
    {
        this.profileStore.storeProfile(this.mongoDatabase,profile);
    }

    public Profile getProfile(String email)
    {
        return this.profileStore.getProfile(this.mongoDatabase,email);
    }

    public void storeSourceOrg(SourceOrg sourceOrg)
    {
        this.orgStore.storeSourceOrg(this.mongoDatabase, sourceOrg);
    }
    public List<SourceOrg> getSourceOrgs()
    {
        return this.orgStore.getSourceOrgs(this.mongoDatabase);
    }

    public SourceOrg getSourceOrg(String orgId)
    {
        return this.orgStore.getSourceOrg(this.mongoDatabase,orgId);
    }

    public List<FoodRunner> getAllFoodRunners()
    {
        return this.foodRunnerStore.getAllFoodRunners(this.mongoDatabase);
    }

    public void deleteFoodRunner(FoodRunner foodRunner)
    {
        this.foodRunnerStore.deleteFoodRunner(this.mongoDatabase, foodRunner);
    }

    public FoodRunner updateFoodRunner(FoodRunner foodRunner)
    {
       return this.foodRunnerStore.updateFoodRunner(this.mongoDatabase, foodRunner);
    }

    public void storeResults(List<FoodRunner> results)
    {
        this.foodRunnerStore.storeResults(this.mongoDatabase, results);
    }

    public void storeActiveNetwork(Map<String, FoodRunner> activeFoodRunners)
    {
        this.networkStore.storeActiveNetwork(this.mongoDatabase, activeFoodRunners);
    }

    public ActiveNetwork getActiveNetwork()
    {
        return this.networkStore.getActiveNetwork(this.mongoDatabase);
    }

    public void clearActiveNetwork()
    {
        this.networkStore.clearActiveNetwork(this.mongoDatabase);
    }


    public DropOffNotification findDropOffNotificationByFoodRunnerId(String foodRunnerId)
    {
        return this.dropOffStore.findDropOffNotificationByFoodRunnerId(this.mongoDatabase, foodRunnerId);
    }

    public void storeScheduledDropOffNotification(ScheduleDropOffNotification scheduleDropOffNotification)
    {
        this.dropOffStore.storeScheduledDropOffNotification(this.mongoDatabase,scheduleDropOffNotification);
    }

    public void updateScheduledDropOffNotification(ScheduleDropOffNotification notification)
    {
        this.dropOffStore.updateScheduledDropOffNotification(this.mongoDatabase, notification);
    }

    public JsonObject getScheduledDropOffNotification(String id)
    {
        return this.dropOffStore.getScheduledDropOffNotification(this.mongoDatabase,id);
    }

    public List<ScheduleDropOffNotification> getScheduledDropOffNotifications(String orgId)
    {
        return this.dropOffStore.getScheduledDropOffNotifications(this.mongoDatabase, orgId);
    }

    public List<ScheduleDropOffNotification> getScheduledDropOffNotifications()
    {
        return this.dropOffStore.getScheduledDropOffNotifications(this.mongoDatabase);
    }

    public void setCompletedTrip(CompletedTrip completedTrip)
    {
        this.tripStore.setCompletedTrip(this.mongoDatabase,completedTrip);
    }

    public List<CompletedTrip> getCompletedTrips()
    {
        return this.tripStore.getCompletedTrips(this.mongoDatabase);
    }

    public List<SchedulePickUpNotification> getSchedulePickUpNotifications()
    {
        List<SchedulePickUpNotification> notifications = new ArrayList<>();

        MongoDatabase database = mongoClient.getDatabase(this.database);
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

        MongoDatabase database = mongoClient.getDatabase(this.database);
        MongoCollection<Document> collection = database.getCollection("scheduledPickUpNotifications");

        //Query Ex: {$and:[{"foodRunner.profile.email":"bugs.bunny.shah@gmail.com"},{"notificationSent":true}]}
        String queryJson = "{$and:[{\"foodRunner.profile.email\":\""+email+"\"},{\"notificationSent\":"+Boolean.TRUE.booleanValue()+"}]}";
        logger.info(queryJson);
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

        MongoDatabase database = mongoClient.getDatabase(this.database);
        MongoCollection<Document> collection = database.getCollection("scheduledPickUpNotifications");

        //Query Ex: {$and:[{"foodRunner.profile.email":"bugs.bunny.shah@gmail.com"},{"notificationSent":true}]}
        String queryJson = "{\"isDropOffDynamic\":"+Boolean.TRUE.booleanValue()+"}";
        logger.info(queryJson);
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

        MongoDatabase database = mongoClient.getDatabase(this.database);
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
        MongoDatabase database = mongoClient.getDatabase(this.database);

        MongoCollection<Document> collection = database.getCollection("scheduledPickUpNotifications");

        Document doc = Document.parse(schedulePickUpNotification.toString());
        collection.insertOne(doc);
    }

    public void updateScheduledPickUpNotification(SchedulePickUpNotification schedulePickUpNotification)
    {
        MongoDatabase database = mongoClient.getDatabase(this.database);

        MongoCollection<Document> collection = database.getCollection("scheduledPickUpNotifications");

        JsonObject stored = this.getScheduledPickUpNotification(schedulePickUpNotification.getId());
        Bson bson = Document.parse(stored.toString());
        collection.deleteOne(bson);

        stored.remove("_id");
        stored.addProperty("notificationSent", true);
        this.storeScheduledPickUpNotification(SchedulePickUpNotification.parse(stored.toString()));
    }

    public JsonObject getScheduledPickUpNotification(String id)
    {
        MongoDatabase database = mongoClient.getDatabase(this.database);
        MongoCollection<Document> collection = database.getCollection("scheduledPickUpNotifications");

        String queryJson = "{\"id\":\""+id+"\"}";
        logger.info(queryJson);
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




    public void storeFoodRecoveryTransaction(FoodRecoveryTransaction foodRecoveryTransaction)
    {
        MongoDatabase database = mongoClient.getDatabase(this.database);

        MongoCollection<Document> collection = database.getCollection("foodRecoveryTransaction");

        Document doc = Document.parse(foodRecoveryTransaction.toString());
        collection.insertOne(doc);
    }

    public List<FoodRecoveryTransaction> getFoodRecoveryTransactions(String email)
    {
        List<FoodRecoveryTransaction> list = new ArrayList<>();
        MongoDatabase database = mongoClient.getDatabase(this.database);
        MongoCollection<Document> collection = database.getCollection("foodRecoveryTransaction");

        String queryJson = "{}";
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

    public List<FoodRecoveryTransaction> getFoodRecoveryTransactionHistory(String orgId)
    {
        List<FoodRecoveryTransaction> list = new ArrayList<>();
        MongoDatabase database = mongoClient.getDatabase(this.database);
        MongoCollection<Document> collection = database.getCollection("foodRecoveryTransaction");

        //Query: {$and:[{"sourceOrg.orgId":"microsoft"},{"notificationSent":true}]}
        String queryJson = "{$and:[{\"pickupNotification.sourceOrg.orgId\":\""+orgId+"\"},{\"transactionState\":\""+TransactionState.CLOSED+"\"}]}";
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

    public List<FoodRecoveryTransaction> getFoodRecoveryDropOffHistory(String orgId)
    {
        List<FoodRecoveryTransaction> list = new ArrayList<>();
        MongoDatabase database = mongoClient.getDatabase(this.database);
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




/*public void storeDropOffNotification(DropOffNotification dropOffNotification)
    {
        MongoDatabase database = mongoClient.getDatabase(this.database);

        MongoCollection<Document> collection = database.getCollection("dropOffNotifications");

        String json = dropOffNotification.toString();
        Document doc = Document.parse(json);

        collection.insertOne(doc);
    }

    public DropOffNotification findDropOffNotification(String dropOffNotificationId)
    {
        DropOffNotification dropOffNotification = new DropOffNotification();

        MongoDatabase database = mongoClient.getDatabase(this.database);

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
    }*/
