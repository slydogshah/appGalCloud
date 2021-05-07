package io.appgal.cloud.restclient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.*;
import io.appgal.cloud.util.JsonUtil;
import io.appgal.cloud.util.MapUtils;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class GoogleApiClientTests {
    private static Logger logger = LoggerFactory.getLogger(GoogleApiClientTests.class);

    @Inject
    private GoogleApiClient googleApiClient;

    @Test
    public void estimateTime() throws Exception {

        JsonObject json = this.googleApiClient.estimateTime(null,null);
        JsonUtil.print(this.getClass(),json);

    }

}