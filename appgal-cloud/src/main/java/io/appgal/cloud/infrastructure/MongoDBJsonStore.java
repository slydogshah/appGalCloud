package io.appgal.cloud.infrastructure;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.*;
import io.appgal.cloud.model.*;
import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.model.FoodRunner;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.bson.Document;

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
        List<FoodRunner> foodRunners = new ArrayList<>();

        MongoDatabase database = mongoClient.getDatabase(this.database);

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

    public void deleteFoodRunner(FoodRunner foodRunner)
    {
        MongoDatabase database = mongoClient.getDatabase(this.database);
        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");
        collection.deleteMany(new Document());
    }

    public void storeActiveNetwork(Map<String, FoodRunner> activeFoodRunners)
    {
        if(activeFoodRunners.isEmpty())
        {
            return;
        }
        
        MongoDatabase database = mongoClient.getDatabase(this.database);

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

        if(!activeFoodRunnerDocs.isEmpty()) {
            collection.insertMany(activeFoodRunnerDocs);
        }
    }

    public ActiveNetwork getActiveNetwork()
    {
        ActiveNetwork activeNetwork = new ActiveNetwork();
        activeNetwork.setMongoDBJsonStore(this);

        MongoDatabase database = mongoClient.getDatabase(this.database);

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

    public void clearActiveNetwork()
    {
        MongoDatabase database = mongoClient.getDatabase(this.database);

        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");

        String json = "{}";

        collection.deleteMany(new Document());
    }

    public void storeDropOffNotification(DropOffNotification dropOffNotification)
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
    }

    public DropOffNotification findDropOffNotificationByFoodRunnerId(String foodRunnerId)
    {
        DropOffNotification dropOffNotification = new DropOffNotification();

        MongoDatabase database = mongoClient.getDatabase(this.database);

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
        MongoDatabase database = mongoClient.getDatabase(this.database);
        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");

        collection.insertOne(Document.parse(completedTrip.getFoodRunner().toString()));
    }

    public List<CompletedTrip> getCompletedTrips()
    {
        List<CompletedTrip> completedTrips = new ArrayList<>();

        MongoDatabase database = mongoClient.getDatabase(this.database);
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

    public void storeResults(List<FoodRunner> results)
    {
        MongoDatabase database = mongoClient.getDatabase(this.database);

        MongoCollection<Document> collection = database.getCollection("results");
        List<Document> documents = new ArrayList<>();
        for(FoodRunner foodRunner:results)
        {
            Document doc = Document.parse(foodRunner.toString());
            documents.add(doc);
        }
        collection.insertMany(documents);
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


    public void storeScheduledDropOffNotification(ScheduleDropOffNotification scheduleDropOffNotification)
    {
        MongoDatabase database = mongoClient.getDatabase(this.database);

        MongoCollection<Document> collection = database.getCollection("scheduledDropOffNotifications");

        Document doc = Document.parse(scheduleDropOffNotification.toString());
        collection.insertOne(doc);
    }

    public void updateScheduledDropOffNotification(ScheduleDropOffNotification notification)
    {
        MongoDatabase database = mongoClient.getDatabase(this.database);

        MongoCollection<Document> collection = database.getCollection("scheduledDropOffNotifications");

        JsonObject stored = this.getScheduledDropOffNotification(notification.getId());
        Bson bson = Document.parse(stored.toString());
        collection.deleteOne(bson);

        stored.remove("_id");
        stored.addProperty("notificationSent", true);
        this.storeScheduledDropOffNotification(ScheduleDropOffNotification.parse(stored.toString()));
    }

    public JsonObject getScheduledDropOffNotification(String id)
    {
        MongoDatabase database = mongoClient.getDatabase(this.database);
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

    public List<ScheduleDropOffNotification> getScheduledDropOffNotifications(String orgId)
    {
        List<ScheduleDropOffNotification> notifications = new ArrayList<>();
        MongoDatabase database = mongoClient.getDatabase(this.database);
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

    public List<ScheduleDropOffNotification> getScheduledDropOffNotifications()
    {
        List<ScheduleDropOffNotification> notifications = new ArrayList<>();
        MongoDatabase database = mongoClient.getDatabase(this.database);
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

    public FoodRunner updateFoodRunner(FoodRunner foodRunner)
    {
        return null;
    }
}
