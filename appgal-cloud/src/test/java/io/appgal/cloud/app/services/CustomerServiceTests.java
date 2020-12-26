package io.appgal.cloud.app.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

@QuarkusTest
public class CustomerServiceTests extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(CustomerServiceTests.class);

    @Inject
    private CustomerService customerService;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Test
    public void testSourceOrgCycle()
    {
        SourceOrg pickUp1 = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com", true);
        customerService.storeSourceOrg(pickUp1);

        List<SourceOrg> sourceOrgs = this.mongoDBJsonStore.getSourceOrgs();
        String sourceId = sourceOrgs.get(0).getOrgId();
        SourceOrg stored = customerService.getSourceOrg(sourceId);
        logger.info("******");
        logger.info(this.gson.toJson(stored.toJson()));
    }
}
