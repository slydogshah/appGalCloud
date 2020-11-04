package io.appgal.cloud.infrastructure.messaging;

public interface KafkaDaemonListener {
    public void receiveNotifications(MessageWindow messageWindow);
}
