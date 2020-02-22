package io.appgal.cloud.messaging;

public interface KafkaDaemonListener {
    public void receiveNotifications(MessageWindow messageWindow);
}
