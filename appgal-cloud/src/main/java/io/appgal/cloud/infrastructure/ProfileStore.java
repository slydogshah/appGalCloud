package io.appgal.cloud.infrastructure;

import com.google.gson.JsonObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.appgal.cloud.model.Profile;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProfileStore {
    private static Logger logger = LoggerFactory.getLogger(ProfileStore.class);

    public Profile getProfile(MongoDatabase database, String email)
    {
        Profile profile = null;

        MongoCollection<Document> collection = database.getCollection("profile");

        String queryJson = "{\"email\":\""+email+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            profile = Profile.parse(documentJson);
            return profile;
        }

        return null;
    }

    public void storeProfile(MongoDatabase database,Profile profile)
    {
        if(this.getProfile(database,profile.getEmail()) != null)
        {
            return;
        }
        if(profile.getProfileType() == null)
        {
            throw new RuntimeException("ProfileType: ISNULL");
        }

        MongoCollection<Document> collection = database.getCollection("profile");

        Document doc = Document.parse(profile.toString());
        collection.insertOne(doc);
    }

    public void updateProfile(MongoDatabase database,Profile profile)
    {
        if(this.getProfile(database,profile.getEmail()) == null)
        {
            return;
        }
        MongoCollection<Document> collection = database.getCollection("profile");

        String email = profile.getEmail();
        String queryJson = "{\"email\":\""+email+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            collection.deleteOne(document);
            break;
        }

        JsonObject profileJson = profile.toJson();
        if(profile.getResetCode() != null) {
            profileJson.addProperty("resetCode", profile.getResetCode());
        }
        Document doc = Document.parse(profileJson.toString());
        collection.insertOne(doc);
    }
}
