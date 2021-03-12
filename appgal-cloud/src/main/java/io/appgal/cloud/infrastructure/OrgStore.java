package io.appgal.cloud.infrastructure;

import com.mongodb.client.*;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.SourceOrg;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrgStore {
    private static Logger logger = LoggerFactory.getLogger(OrgStore.class);

    public void storeSourceOrg(MongoDatabase database, SourceOrg sourceOrg)
    {
        SourceOrg existing = this.getSourceOrg(database,sourceOrg.getOrgId());
        MongoCollection<Document> collection = database.getCollection("customers");

        if(existing != null)
        {
            Bson bson = Document.parse(existing.toString());
            collection.deleteOne(bson);
        }

        String json = sourceOrg.toString();
        Document doc = Document.parse(json);
        collection.insertOne(doc);

        for(Profile profile:sourceOrg.getProfiles())
        {
            this.storeProfile(database,profile);
        }
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

    public Profile getProfile(MongoDatabase database,String email)
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

    public SourceOrg getSourceOrg(MongoDatabase database, String orgId)
    {
        MongoCollection<Document> collection = database.getCollection("customers");

        String queryJson = "{\"orgId\":\""+orgId+"\"}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            return SourceOrg.parse(documentJson);
        }
        return null;
    }
}
