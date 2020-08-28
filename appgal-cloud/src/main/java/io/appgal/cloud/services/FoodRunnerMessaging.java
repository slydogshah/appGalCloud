package io.appgal.cloud.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class FoodRunnerMessaging
{
    private static Logger logger = LoggerFactory.getLogger(FoodRunnerMessaging.class);

    public void notifyCustomerPickup()
    {
        logger.info("****************");
        logger.info("NOTIFY_CUSTOMER_PICKUP");
        logger.info("****************");
    }
}