package io.appgal.cloud.infrastructure;

import com.mongodb.client.*;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.SourceOrg;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class OrgStore {
    private static Logger logger = LoggerFactory.getLogger(OrgStore.class);

    @Inject
    private ProfileStore profileStore;

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
            this.profileStore.storeProfile(database,profile);
        }
    }

    public List<SourceOrg> getSourceOrgs(MongoDatabase database)
    {
        List<SourceOrg> sourceOrgs = new ArrayList<>();

        MongoCollection<Document> collection = database.getCollection("customers");

        String queryJson = "{}";
        Bson bson = Document.parse(queryJson);
        FindIterable<Document> iterable = collection.find(bson);
        MongoCursor<Document> cursor = iterable.cursor();
        while(cursor.hasNext())
        {
            Document document = cursor.next();
            String documentJson = document.toJson();
            SourceOrg sourceOrg = SourceOrg.parse(documentJson);
            sourceOrgs.add(sourceOrg);
        }

        return sourceOrgs;
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
