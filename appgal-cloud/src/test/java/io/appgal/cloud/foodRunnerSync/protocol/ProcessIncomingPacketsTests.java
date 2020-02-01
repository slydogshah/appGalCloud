package io.appgal.cloud.foodRunnerSync.protocol;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@QuarkusTest
public class ProcessIncomingPacketsTests {
    private static Logger logger = LoggerFactory.getLogger(ProcessIncomingPacketsTests.class);

    @Inject
    private ProcessIncomingPackets processIncomingPackets;

    @Test
    public void testProcessSourceNotification()
    {
        this.processIncomingPackets.processSourceNotification();
    }
}
