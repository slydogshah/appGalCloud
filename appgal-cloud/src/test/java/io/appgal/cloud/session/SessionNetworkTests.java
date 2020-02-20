package io.appgal.cloud.session;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@QuarkusTest
public class SessionNetworkTests {
    private static Logger logger = LoggerFactory.getLogger(SessionNetworkTests.class);

    @Inject
    private SessionNetwork sessionNetwork;

    @BeforeEach
    private void setUp()
    {
        this.sessionNetwork.start();
    }

    @AfterEach
    private void tearDown()
    {
        this.sessionNetwork.stop();
    }

    @Test
    public void testStart()
    {

    }
}
