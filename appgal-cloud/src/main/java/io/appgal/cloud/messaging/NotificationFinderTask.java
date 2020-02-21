package io.appgal.cloud.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RecursiveAction;

public class NotificationFinderTask extends RecursiveAction {
    private static Logger logger = LoggerFactory.getLogger(NotificationFinderTask.class);

    private NotificationContext notificationContext;

    public NotificationFinderTask(NotificationContext notificationContext) {
        this.notificationContext = notificationContext;
    }

    @Override
    protected void compute() {

    }
}
