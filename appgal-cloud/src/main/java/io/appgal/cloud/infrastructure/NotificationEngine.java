package io.appgal.cloud.infrastructure;

import io.appgal.cloud.model.SchedulePickUpNotification;
import io.appgal.cloud.network.services.DropOffPipeline;
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
    private DropOffPipeline dropOffPipeline;

    public NotificationEngine(SecurityTokenContainer securityTokenContainer, RequestPipeline requestPipeline, DropOffPipeline dropOffPipeline, MongoDBJsonStore mongoDBJsonStore)
    {
        this.securityTokenContainer = securityTokenContainer;
        this.securityToken = this.securityTokenContainer.getSecurityToken();
        this.requestPipeline = requestPipeline;
        this.dropOffPipeline = dropOffPipeline;
        this.mongoDBJsonStore = mongoDBJsonStore;
    }

    public void start()
    {
        this.timer = new Timer(true);
        this.timer.schedule(this, 10000, 10000);
    }

    public RequestPipeline getRequestPipeline()
    {
        return this.requestPipeline;
    }

    public DropOffPipeline getDropOffPipeline()
    {
        return this.dropOffPipeline;
    }

    @Override
    public void run()
    {
        try
        {
            //logger.info("***********NOTIFICATION_ENGINE**********");
            while(true)
            {
                this.requestPipeline.process();
                this.dropOffPipeline.process();
            }
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }
}
