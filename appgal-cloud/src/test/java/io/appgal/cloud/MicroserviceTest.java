package io.appgal.cloud;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class MicroserviceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/microservice")
          .then()
             .statusCode(200);
    }

}