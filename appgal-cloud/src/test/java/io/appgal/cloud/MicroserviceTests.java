package io.appgal.cloud;

import com.google.gson.JsonObject;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class MicroserviceTests {

    //@Test
    public void testHelloEndpoint() {
        given()
          .when().get("/microservice")
          .then()
             .statusCode(200);
    }

    //@Test
    public void test() throws Exception{
        String request = "/registration/login/";
        JsonObject json = new JsonObject();
        json.addProperty("email","jen@app.io");
        json.addProperty("password","jen");
        json.addProperty("latitude",30.2698104d);
        json.addProperty("longitude",-97.75115579999999d);
        Response response = given().header("User-Agent","Dart").body(json.toString()).when().post(request).andReturn();
        System.out.println(response.getStatusCode());
        response.getBody().prettyPrint();
    }
}