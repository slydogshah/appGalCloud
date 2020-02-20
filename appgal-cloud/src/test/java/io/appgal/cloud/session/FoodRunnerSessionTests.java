package io.appgal.cloud.session;


import io.appgal.cloud.foodRunnerSync.protocol.ProcessIncomingPackets;
import io.appgal.cloud.messaging.KafkaDaemon;
import io.appgal.cloud.messaging.MessageWindow;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.*;

@QuarkusTest
public class FoodRunnerSessionTests {
    private static Logger logger = LoggerFactory.getLogger(FoodRunnerSessionTests.class);

    @Inject
    private FoodRunnerSession foodRunnerSession;

    @Inject
    private ProcessIncomingPackets processIncomingPackets;

    @Inject
    private KafkaDaemon kafkaDaemon;

    @BeforeEach
    private void setUp()
    {
    }

    @AfterEach
    private void tearDown()
    {
    }

    @Test
    public void testStart() throws Exception
    {
        this.kafkaDaemon.logStartUp();
        int counter=0;
        while(!this.kafkaDaemon.getActive()) {
            Thread.sleep(5000);
            if(counter++ == 3)
            {
                break;
            }
        }

        //OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        //OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        //MessageWindow messageWindow = new MessageWindow(start, end);
        LocalDateTime startOfLocalDateInUtc = LocalDate.now(ZoneOffset.UTC).atStartOfDay();
        OffsetDateTime startTime = OffsetDateTime.of(startOfLocalDateInUtc, ZoneOffset.UTC);
        OffsetDateTime endTime = OffsetDateTime.now(ZoneOffset.UTC);
        MessageWindow messageWindow = new MessageWindow(startTime, endTime);

        this.processIncomingPackets.processSourceNotification(messageWindow);

        this.foodRunnerSession.start();
    }
}
