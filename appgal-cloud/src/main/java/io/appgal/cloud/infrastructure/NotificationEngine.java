package io.appgal.cloud.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;


@Singleton
public class NotificationEngine extends TimerTask {
    private static Logger logger = LoggerFactory.getLogger(NotificationEngine.class);

    private Timer timer;

    @Inject
    private RequestPipeline requestPipeline;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    public void start()
    {
        this.timer = new Timer(true);
        this.timer.schedule(this, 10000, 10000);
    }

    public RequestPipeline getRequestPipeline()
    {
        return this.requestPipeline;
    }

    @Override
    public void run()
    {
        try
        {
            while(true)
            {
                this.requestPipeline.process();
            }
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }
}
