package io.appgal.cloud.app.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.SourceOrg;
import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class CustomerTests extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(CustomerTests.class);

    @Test
    public void testCustomerLifeCycle() {
        String oid = UUID.randomUUID().toString();
        SourceOrg sourceOrg = new SourceOrg();
        String orgId = oid+"/orgId";
        sourceOrg.setOrgId(orgId);
        sourceOrg.setOrgName("microsoft");
        sourceOrg.setOrgContactEmail("blah@microsoft.com");
        sourceOrg.setProducer(true);

        Response response = given().body(sourceOrg.toJson().toString()).when().post("/customer/save")
                .andReturn();

        response = given().when().get("/customer/get?orgId="+orgId)
                .andReturn();

        String json = response.getBody().prettyPrint();
        logger.info("****");
        logger.info(json);
        logger.info("****");

        //assert the body
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        assertEquals(orgId, jsonObject.get("orgId").getAsString());
    }
}