package io.appgal.cloud.messaging;

import io.appgal.cloud.model.SourceNotification;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@QuarkusTest
public class SourceNotificationEmitterTests {
    private static Logger logger = LoggerFactory.getLogger(SourceNotificationEmitterTests.class);

    @Inject
    private SourceNotificationEmitter sourceNotificationEmitter;

    @Test
    public void testEmit()
    {

    }
}
