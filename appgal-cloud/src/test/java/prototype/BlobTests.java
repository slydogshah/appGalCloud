package prototype;

import com.mongodb.ClientSessionOptions;
import  javax.naming.*;

import com.mongodb.client.*;
import prototype.*;
import com.google.api.services.calendar.Calendar.Acl.Get;

import com.mongodb.connection.ClusterDescription;
import io.quarkus.test.junit.QuarkusTest;
import org.bson.BsonBinary;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.List;

import static org.apache.hadoop.metrics2.impl.MsInfo.Context;

@QuarkusTest
public class BlobTests {
    private Logger logger = LoggerFactory.getLogger(BlobTests.class);

    @Test
    public void testBlob() throws Exception{
        System.out.println("BLOB");

        /*BsonBinary photu = new BsonBinary("PHOTU".getBytes());
        CompositeName name = new CompositeName("photu");
        MongoClientFactory mongoClientFactory = new MongoClientFactory();
        Object obj = new CompositeName("photu");
        InitialContext nameCtx = new InitialContext();
        Hashtable<?,?> environment = new Hashtable<>();
        MongoClient mongoClient = new MongoClient() {
            @Override
            public MongoDatabase getDatabase(String s) {
                return null;
            }

            @Override
            public ClientSession startSession() {
                return null;
            }

            @Override
            public ClientSession startSession(ClientSessionOptions clientSessionOptions) {
                return null;
            }

            @Override
            public void close() {

            }

            @Override
            public MongoIterable<String> listDatabaseNames() {
                return null;
            }

            @Override
            public MongoIterable<String> listDatabaseNames(ClientSession clientSession) {
                return null;
            }

            @Override
            public ListDatabasesIterable<Document> listDatabases() {
                return null;
            }

            @Override
            public ListDatabasesIterable<Document> listDatabases(ClientSession clientSession) {
                return null;
            }

            @Override
            public <TResult> ListDatabasesIterable<TResult> listDatabases(Class<TResult> aClass) {
                return null;
            }

            @Override
            public <TResult> ListDatabasesIterable<TResult> listDatabases(ClientSession clientSession, Class<TResult> aClass) {
                return null;
            }

            @Override
            public ChangeStreamIterable<Document> watch() {
                return null;
            }

            @Override
            public <TResult> ChangeStreamIterable<TResult> watch(Class<TResult> aClass) {
                return null;
            }

            @Override
            public ChangeStreamIterable<Document> watch(List<? extends Bson> list) {
                return null;
            }

            @Override
            public <TResult> ChangeStreamIterable<TResult> watch(List<? extends Bson> list, Class<TResult> aClass) {
                return null;
            }

            @Override
            public ChangeStreamIterable<Document> watch(ClientSession clientSession) {
                return null;
            }

            @Override
            public <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, Class<TResult> aClass) {
                return null;
            }

            @Override
            public ChangeStreamIterable<Document> watch(ClientSession clientSession, List<? extends Bson> list) {
                return null;
            }

            @Override
            public <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, List<? extends Bson> list, Class<TResult> aClass) {
                return null;
            }

            @Override
            public ClusterDescription getClusterDescription() {
                return null;
            }
        };
        MongoDatabase database = mongoClient.getDatabase("appgalcloud");
        //FindIterable<Document> iterable = database.getCollection("my_coll");
        //FindIterable<Document> iterable = db.getCollection("my_coll").find(query);
        MongoCollection<Document> collection = database.getCollection("destinationNotifications");*/
    }

}
