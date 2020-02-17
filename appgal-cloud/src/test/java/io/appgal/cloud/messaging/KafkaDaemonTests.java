package io.appgal.cloud.messaging;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class KafkaDaemonTests {
    private static Logger logger = LoggerFactory.getLogger(KafkaDaemonTests.class);

    @Test
    public void testRun()
    {
        logger.info("****");
        logger.info("TEST_RUN");
        logger.info("****");
    }
}
