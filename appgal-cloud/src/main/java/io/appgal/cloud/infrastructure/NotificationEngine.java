package io.appgal.cloud.infrastructure;

import io.appgal.cloud.model.SchedulePickUpNotification;
import io.appgal.cloud.network.services.RequestPipeline;
import io.appgal.cloud.util.JsonUtil;
import io.bugsbunny.preprocess.SecurityToken;
import io.bugsbunny.preprocess.SecurityTokenContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class NotificationEngine extends TimerTask {
    private static Logger logger = LoggerFactory.getLogger(NotificationEngine.class);

    private Timer timer;
    private SecurityTokenContainer securityTokenContainer;
    private SecurityToken securityToken;

    private RequestPipeline requestPipeline;
    private MongoDBJsonStore mongoDBJsonStore;

    public NotificationEngine(SecurityTokenContainer securityTokenContainer, RequestPipeline requestPipeline, MongoDBJsonStore mongoDBJsonStore)
    {
        this.securityTokenContainer = securityTokenContainer;
        this.securityToken = this.securityTokenContainer.getSecurityToken();
        this.requestPipeline = requestPipeline;
        this.mongoDBJsonStore = mongoDBJsonStore;
    }

    public void start()
    {
        this.timer = new Timer(true);
        this.timer.schedule(this, new Date(), 5000);
    }

    @Override
    public void run()
    {
        try
        {
            //logger.info("***********NOTIFICATION_ENGINE**********");
            while(true) {
                SchedulePickUpNotification notification = this.requestPipeline.peek();
                if (notification == null) {
                    //logger.info("*******1*********");
                    return;
                }
                //Check
                if (!notification.activateNotification()) {
                    //logger.info("*******2*********");
                    this.requestPipeline.remove(notification);
                    return;
                }

                //logger.info("*******3*********");
                notification = this.requestPipeline.next();
                notification.setNotificationSent(true);

                //logger.info("*******4*********");
                //Send
                this.mongoDBJsonStore.updateScheduledPickUpNotification(notification);
            }
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }
}
