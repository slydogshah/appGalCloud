package io.appgal.cloud.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KafkaDaemonLogger {
    private static Logger logger = LoggerFactory.getLogger(KafkaDaemonLogger.class);
}