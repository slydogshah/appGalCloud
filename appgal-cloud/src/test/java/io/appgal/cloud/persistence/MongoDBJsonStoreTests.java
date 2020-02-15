package io.appgal.cloud.persistence;

import com.google.gson.JsonArray;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class MongoDBJsonStoreTests {
    private static Logger logger = LoggerFactory.getLogger(MongoDBJsonStoreTests.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @BeforeEach
    public void setUp()
    {

    }

    @AfterEach
    public void tearDown()
    {

    }

    @Test
    public void testFindDestinationNotifications()
    {
        List<String> notificationIds = new ArrayList<>();
        JsonArray jsonArray = this.mongoDBJsonStore.findDestinationNotifications(notificationIds);

        //assert
        assertNotNull(jsonArray);

        logger.info("****");
        logger.info(jsonArray.toString());
        logger.info("****");
    }
}
