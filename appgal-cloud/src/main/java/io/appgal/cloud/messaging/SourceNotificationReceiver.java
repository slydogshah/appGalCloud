package io.appgal.cloud.messaging;

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
    private KafkaDaemonClient kafkaDaemonClient;

    public void receive(SourceNotification sourceNotification)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sourceNotificationId", sourceNotification.getSourceNotificationId());

        this.kafkaDaemonClient.produceData(jsonObject);
    }
}
