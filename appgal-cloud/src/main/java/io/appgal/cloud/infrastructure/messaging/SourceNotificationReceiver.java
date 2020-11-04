package io.appgal.cloud.infrastructure.messaging;

import com.google.gson.JsonObject;
import io.appgal.cloud.model.SourceNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SourceNotificationReceiver {
    private static Logger logger = LoggerFactory.getLogger(SourceNotificationReceiver.class);

    @Inject
    private KafkaDaemon kafkaDaemon;

    public void receive(SourceNotification sourceNotification)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sourceNotificationId", sourceNotification.getSourceNotificationId());

        this.kafkaDaemon.produceData(SourceNotification.TOPIC, jsonObject);
    }
}
