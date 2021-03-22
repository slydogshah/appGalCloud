package io.appgal.cloud.model;

import io.appgal.cloud.util.JsonUtil;
import io.bugsbunny.test.components.MockData;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
public class FoodRecoveryTransactionTests {
    private static Logger logger = LoggerFactory.getLogger(FoodRecoveryTransactionTests.class);

    @Test
    public void testJson()
    {
        FoodRecoveryTransaction tx = MockData.mockFoodRecoveryTransaction();
        JsonUtil.print(this.getClass(),tx.toJson());

        String jsonString = tx.toString();
        FoodRecoveryTransaction deser = FoodRecoveryTransaction.parse(jsonString);
        JsonUtil.print(this.getClass(),deser.toJson());
    }
}
