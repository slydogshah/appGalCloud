package io.appgal.cloud.infrastructure;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.model.ProfileType;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class NetworkStore {
    private static Logger logger = LoggerFactory.getLogger(NetworkStore.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;


    public void storeActiveNetwork(MongoDatabase database, Map<String, FoodRunner> activeFoodRunners)
    {
        if(activeFoodRunners.isEmpty())
        {
            return;
        }

        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");

        this.clearActiveNetwork(database);

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

    public ActiveNetwork getActiveNetwork(MongoDatabase database)
    {
        ActiveNetwork activeNetwork = new ActiveNetwork();
        activeNetwork.setMongoDBJsonStore(this.mongoDBJsonStore);

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
        activeNetwork.setSourceOrgs(this.mongoDBJsonStore.getSourceOrgs());

        return activeNetwork;
    }

    public void clearActiveNetwork(MongoDatabase database)
    {
        MongoCollection<Document> collection = database.getCollection("activeFoodRunners");

        String json = "{}";

        collection.deleteMany(new Document());
    }
}
