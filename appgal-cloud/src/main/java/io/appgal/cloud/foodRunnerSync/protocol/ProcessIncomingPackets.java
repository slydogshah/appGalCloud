package io.appgal.cloud.foodRunnerSync.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProcessIncomingPackets {
    private static Logger logger = LoggerFactory.getLogger(ProcessIncomingPackets.class);

    public void processSourceNotification()
    {
        logger.info("....");
        logger.info("PROCESS_SOURCE_NOTIFICATION");
        logger.info("....");
    }
}
