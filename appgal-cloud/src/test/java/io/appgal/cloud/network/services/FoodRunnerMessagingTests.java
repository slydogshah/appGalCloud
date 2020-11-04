package io.appgal.cloud.network.services;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

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