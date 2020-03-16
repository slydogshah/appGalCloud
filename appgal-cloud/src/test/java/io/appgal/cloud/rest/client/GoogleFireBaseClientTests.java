package io.appgal.cloud.rest.client;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@QuarkusTest
public class GoogleFireBaseClientTests {
    private static Logger logger = LoggerFactory.getLogger(GoogleFireBaseClientTests.class);

    @Inject
    private GoogleFireBaseClient googleFireBaseClient;

    @Test
    public void testRegisterToken() throws Exception
    {
        googleFireBaseClient.registerToken("blahToken");
    }
}
