package io.appgal.cloud.messaging;

public class NotificationContext {
    private String topic;
    private MessageWindow messageWindow;

    public NotificationContext(String topic, MessageWindow messageWindow) {
        this.topic = topic;
        this.messageWindow = messageWindow;
    }

    public String getTopic() {
        return topic;
    }

    public MessageWindow getMessageWindow() {
        return messageWindow;
    }
}
