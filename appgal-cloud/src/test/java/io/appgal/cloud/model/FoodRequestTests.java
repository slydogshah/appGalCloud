package io.appgal.cloud.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class FoodRequestTests {
    private static Logger logger = LoggerFactory.getLogger(FoodRequestTests.class);

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Test
    public void testJson() throws Exception
    {
        FoodRequest foodRequest = new FoodRequest();
        SourceOrg sourceOrg1 = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com");
        foodRequest.setFoodType(FoodTypes.VEG);
        foodRequest.setSourceOrg(sourceOrg1);

        logger.info("*******");
        logger.info(foodRequest.toString());
    }
}
