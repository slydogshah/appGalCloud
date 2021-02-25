package io.appgal.cloud.network.services;

import com.google.gson.JsonParser;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.util.JsonUtil;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

@QuarkusTest
public class FoodRecoveryOrchestratorTests {
    private static Logger logger = LoggerFactory.getLogger(FoodRecoveryOrchestrator.class);

    @Inject
    private FoodRecoveryOrchestrator foodRecoveryOrchestrator;

    @Test
    public void testFindDropOffOrganizations() {
        List<SourceOrg> sourceOrgList = this.foodRecoveryOrchestrator.findDropOffOrganizations("microsoft");
        JsonUtil.print(JsonParser.parseString(sourceOrgList.toString()));
    }
}