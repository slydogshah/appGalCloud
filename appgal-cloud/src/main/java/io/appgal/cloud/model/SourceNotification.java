package io.appgal.cloud.model;

import io.appgal.cloud.messaging.MessageWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceNotification {
    private static Logger logger = LoggerFactory.getLogger(SourceNotification.class);

    private String sourceNotificationId;
    private MessageWindow messageWindow;

    public String getSourceNotificationId() {
        return sourceNotificationId;
    }

    public void setSourceNotificationId(String sourceNotificationId) {
        this.sourceNotificationId = sourceNotificationId;
    }

    public MessageWindow getMessageWindow() {
        return messageWindow;
    }

    public void setMessageWindow(MessageWindow messageWindow) {
        this.messageWindow = messageWindow;
    }
}
