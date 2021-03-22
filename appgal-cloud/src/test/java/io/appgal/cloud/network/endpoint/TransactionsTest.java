package io.appgal.cloud.network.endpoint;

import com.google.gson.JsonParser;
import io.appgal.cloud.util.JsonUtil;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class TransactionsTest {
    private static Logger logger = LoggerFactory.getLogger(TransactionsTest.class);

    @Test
    public void testRecoveryEndpoint() throws Exception
    {
        String email = "bugs.bunny.shah@gmail.com";
        final Response response = given()
                .when().get("/tx/recovery/?email=" + email)
                .andReturn();

        JsonUtil.print(this.getClass(),JsonParser.parseString(response.getBody().asString()));
    }

    @Test
    public void testRecoveryHistoryEndpoint() throws Exception
    {
        String orgId = "microsoft";
        final Response response = given()
                .when().get("/tx/recovery/history/?orgId=" + orgId)
                .andReturn();

        JsonUtil.print(this.getClass(),JsonParser.parseString(response.getBody().asString()));
    }

    @Test
    public void testDropOffHistoryEndpoint() throws Exception
    {
        String orgId = "church";
        final Response response = given()
                .when().get("/tx/dropOff/history/?orgId=" + orgId)
                .andReturn();

        JsonUtil.print(this.getClass(),JsonParser.parseString(response.getBody().asString()));
    }
}