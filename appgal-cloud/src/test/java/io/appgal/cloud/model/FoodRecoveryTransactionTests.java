package io.appgal.cloud.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
public class FoodRecoveryTransactionTests {
    private static Logger logger = LoggerFactory.getLogger(FoodRecoveryTransactionTests.class);

    @Test
    public void testJson()
    {
        FoodRecoveryTransaction tx = new FoodRecoveryTransaction();
        logger.info("FOOD_RECOVERY_TRANSACTION_JSON_LOGGER");
        logger.info(tx.toString());
    }
}
