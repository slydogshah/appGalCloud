package io.appgal.cloud.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.persistence.MongoDBJsonStore;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

@QuarkusTest
public class FoodRunnerMessagingTests
{
    private static Logger logger = LoggerFactory.getLogger(FoodRunnerMessagingTests.class);

    @Inject
    private FoodRunnerMessaging foodRunnerMessaging;

    @Test
    public void testNotifyCustomerPickup() throws Exception
    {
        this.foodRunnerMessaging.notifyCustomerPickup();
    }
}