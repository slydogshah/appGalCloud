package io.appgal.cloud.infrastructure;

import io.appgal.cloud.model.*;
import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.model.FoodRunner;

import com.google.gson.JsonObject;

import com.mongodb.client.*;

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

    @ConfigProperty(name = "mongoDBConnectionString")
    private String mongodbConnectionString;

    @ConfigProperty(name = "mongodbHost")
    private String mongodbHost;

    @ConfigProperty(name = "mongodbPort")
    private String mongodbPort;

    private String database = "jennetwork";
    private String password = "jen";

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
        System.out.println("string "+this.mongodbConnectionString);
        System.out.println("host: "+this.mongodbHost);
        System.out.println("port "+this.mongodbPort);
        System.out.println("db "+this.database);
        System.out.println("pass "+this.password);

        String connectionString;
        if(this.mongodbHost.equals("localhost"))
        {
            connectionString = this.mongodbConnectionString;
        }
        else
        {
            connectionString = MessageFormat.format(this.mongodbConnectionString,this.password,this.mongodbHost,this.database);
        }

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

    public MongoDatabase getMongoDatabase() {
        return this.mongoDatabase;
    }

    //Profiles
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

    //Orgs
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

    //FoodRunners
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

    public FoodRunner getFoodRunner(String email)
    {
        return this.foodRunnerStore.getFoodRunner(this.mongoDatabase,email);
    }

    public void storeResults(List<FoodRunner> results)
    {
        this.foodRunnerStore.storeResults(this.mongoDatabase, results);
    }

    //Network
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

    //DropOff
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

    //Trips
    public void setCompletedTrip(CompletedTrip completedTrip)
    {
        this.tripStore.setCompletedTrip(this.mongoDatabase,completedTrip);
    }

    public List<CompletedTrip> getCompletedTrips()
    {
        return this.tripStore.getCompletedTrips(this.mongoDatabase);
    }

    //Pickups
    public List<SchedulePickUpNotification> getSchedulePickUpNotifications()
    {
        return this.pickupRequestStore.getSchedulePickUpNotifications();
    }

    public List<SchedulePickUpNotification> getSchedulePickUpNotifications(String email)
    {
        return this.pickupRequestStore.getSchedulePickUpNotifications(email);
    }

    public List<SchedulePickUpNotification> getPickUpNotificationsWithoutDropOff()
    {
        return this.pickupRequestStore.getPickUpNotificationsWithoutDropOff();
    }

    public List<SchedulePickUpNotification> getUnsentSchedulePickUpNotifications(String email)
    {
        return this.pickupRequestStore.getUnsentSchedulePickUpNotifications(email);
    }

    public void storeScheduledPickUpNotification(SchedulePickUpNotification schedulePickUpNotification)
    {
        this.pickupRequestStore.storeScheduledPickUpNotification(schedulePickUpNotification);
    }

    public void updateScheduledPickUpNotification(SchedulePickUpNotification schedulePickUpNotification)
    {
        this.pickupRequestStore.updateScheduledPickUpNotification(schedulePickUpNotification);
    }

    public JsonObject getScheduledPickUpNotification(String id)
    {
        return this.pickupRequestStore.getScheduledPickUpNotification(id);
    }

    //FoodRecovery
    public void storeFoodRecoveryTransaction(FoodRecoveryTransaction foodRecoveryTransaction)
    {
        this.foodRecoveryStore.storeFoodRecoveryTransaction(this.mongoDatabase,foodRecoveryTransaction);
    }

    public List<FoodRecoveryTransaction> getFoodRecoveryTransactions(String orgId)
    {
        return this.foodRecoveryStore.getFoodRecoveryTransactions(this.mongoDatabase, orgId);
    }

    public List<FoodRecoveryTransaction> getFoodRecoveryTransactions()
    {
        return this.foodRecoveryStore.getFoodRecoveryTransactions(this.mongoDatabase);
    }

    public List<FoodRecoveryTransaction> getFoodRecoveryDropOffTransactions(String orgId)
    {
        return this.foodRecoveryStore.getFoodRecoveryDropOffTransactions(this.mongoDatabase,orgId);
    }

    public List<FoodRecoveryTransaction> getFoodRecoveryTransactionHistory(String orgId)
    {
        return this.foodRecoveryStore.getFoodRecoveryTransactionHistory(this.mongoDatabase, orgId);
    }

    public List<FoodRecoveryTransaction> getFoodRecoveryDropOffHistory(String orgId)
    {
        return this.foodRecoveryStore.getFoodRecoveryDropOffHistory(this.mongoDatabase, orgId);
    }
}
