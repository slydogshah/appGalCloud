package io.appgal.cloud.network.services;

import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.Location;
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

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Test
    public void testFindDropOffOrganizations() {
        SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",true);
        sourceOrg.setLocation(new Location(9.0d, 10.0d));
        this.mongoDBJsonStore.storeSourceOrg(sourceOrg);

        List<SourceOrg> sourceOrgList = this.foodRecoveryOrchestrator.findDropOffOrganizations("microsoft");
        JsonUtil.print(JsonParser.parseString(sourceOrgList.toString()));
    }
}