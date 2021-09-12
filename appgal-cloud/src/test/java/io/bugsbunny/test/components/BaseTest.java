package io.bugsbunny.test.components;

import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.preprocess.SecurityTokenProcessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;

public abstract class BaseTest
{
    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    SecurityTokenProcessor securityTokenProcessor;

    @BeforeEach
    public void setUp() throws Exception
    {
        //this.securityTokenProcessor.deactivate();
    }

    @AfterEach
    public void tearDown() throws Exception
    {
        try {
            if (this.mongoDBJsonStore == null) {
                this.mongoDBJsonStore = new MongoDBJsonStore();
            }
            this.mongoDBJsonStore.start();
            this.mongoDBJsonStore.getMongoClient().getDatabase("jennetwork").drop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
