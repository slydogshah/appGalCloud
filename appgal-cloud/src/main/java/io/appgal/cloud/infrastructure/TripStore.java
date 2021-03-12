package io.appgal.cloud.infrastructure;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.appgal.cloud.model.CompletedTrip;
import io.appgal.cloud.model.DropOffNotification;
import io.appgal.cloud.model.FoodRunner;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TripStore {
    private static Logger logger = LoggerFactory.getLogger(TripStore.class);

    @Inject
    private DropOffStore dropOffStore;

    public void setCompletedTrip(MongoDatabase database, CompletedTrip completedTrip)
    {
        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");

        collection.insertOne(Document.parse(completedTrip.getFoodRunner().toString()));
    }

    public List<CompletedTrip> getCompletedTrips(MongoDatabase database)
    {
        List<CompletedTrip> completedTrips = new ArrayList<>();

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

            DropOffNotification dropOffNotification = this.dropOffStore.
                    findDropOffNotificationByFoodRunnerId(database,foodRunner.getProfile().getId());
            dropOffNotification.setFoodRunner(foodRunner);
            completedTrip.setDropOffNotification(dropOffNotification);

            completedTrips.add(completedTrip);
        }

        return completedTrips;
    }
}
