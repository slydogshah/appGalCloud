package io.appgal.cloud.messaging;

import io.appgal.cloud.model.SourceNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SourceNotificationEmitter {
    private static Logger logger = LoggerFactory.getLogger(SourceNotificationEmitter.class);

    @Inject
    private KafkaDaemonClient kafkaDaemonClient;

    public void emit(SourceNotification sourceNotification)
    {

    }
}
