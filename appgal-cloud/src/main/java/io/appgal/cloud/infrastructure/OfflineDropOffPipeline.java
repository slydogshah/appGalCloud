package io.appgal.cloud.infrastructure;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.SchedulePickUpNotification;
import io.appgal.cloud.network.services.RequestPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class OfflineDropOffPipeline {
    private static Logger logger = LoggerFactory.getLogger(OfflineDropOffPipeline.class);

    @Inject
    private RequestPipeline requestPipeline;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    public JsonArray findRunnersWithDynamicDropOff()
    {
        List<SchedulePickUpNotification> notifications = this.mongoDBJsonStore.getPickUpNotificationsWithoutDropOff();
        return JsonParser.parseString(notifications.toString()).getAsJsonArray();
    }
}
