package io.appgal.cloud.network.services;

import com.google.gson.JsonArray;
import io.appgal.cloud.util.JsonUtil;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@QuarkusTest
public class DynamicDropOffOrchestratorTests {
    private static Logger logger = LoggerFactory.getLogger(DynamicDropOffOrchestratorTests.class);

    @Inject
    private DynamicDropOffOrchestrator dynamicDropOffOrchestrator;

    @Test
    public void testOrchestrateOfflineCommunity()
    {
        this.dynamicDropOffOrchestrator.orchestrateOfflineCommunity();
    }

    @Test
    public void testGetOfflineDropOffPipeline()
    {
        JsonUtil.print(this.dynamicDropOffOrchestrator.getOfflineDropOffPipeline());
    }
}
