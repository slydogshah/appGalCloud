package io.appgal.cloud.session;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@QuarkusTest
public class FoodRunnerSessionTests {
    private static Logger logger = LoggerFactory.getLogger(FoodRunnerSessionTests.class);

    @BeforeEach
    private void setUp()
    {

    }

    @AfterEach
    private void tearDown()
    {
    }

    @Test
    public void testStart()
    {
        FoodRunnerSession session = new FoodRunnerSession();
        session.start();
    }
}
