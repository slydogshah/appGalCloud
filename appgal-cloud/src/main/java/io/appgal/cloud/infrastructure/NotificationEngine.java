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

    public NotificationEngine(SecurityTokenContainer securityTokenContainer, RequestPipeline requestPipeline)
    {
        this.securityTokenContainer = securityTokenContainer;
        this.securityToken = this.securityTokenContainer.getSecurityToken();
        this.requestPipeline = requestPipeline;
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
            logger.info("*****NOTIFICATION_ENGINE**********");
            SchedulePickUpNotification notification = this.requestPipeline.next();
            if(notification != null) {
                JsonUtil.print(notification.toJson());
            }
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }
}
