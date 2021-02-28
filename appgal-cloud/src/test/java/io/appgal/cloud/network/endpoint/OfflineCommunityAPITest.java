package io.appgal.cloud.network.endpoint;

import io.appgal.cloud.model.*;
import io.appgal.cloud.network.services.LocationService;
import io.appgal.cloud.network.services.NetworkOrchestrator;
import io.appgal.cloud.util.JsonUtil;
import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class OfflineCommunityAPITest extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(OfflineCommunityAPITest.class);

    @Test
    public void testOrchestrateOfflineCommunity() throws Exception
    {
        Response response = given().when().get("/offline/community/")
                .andReturn();
        logger.info(response.asPrettyString());
    }

    @Test
    public void testGetOfflineDropOffPipeline() throws Exception
    {
        Response response = given().when().get("/offline/pipeline/")
                .andReturn();
        logger.info(response.asPrettyString());
    }
}