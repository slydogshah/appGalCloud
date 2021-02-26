package io.appgal.cloud.network.services;

import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.SchedulePickUpNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Singleton
public class RequestPipeline {
    private static Logger logger = LoggerFactory.getLogger(RequestPipeline.class);

    private PriorityQueue<SchedulePickUpNotification> queue;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    public RequestPipeline()
    {
        Comparator<SchedulePickUpNotification> comparator = new Comparator<SchedulePickUpNotification>() {
            @Override
            public int compare(SchedulePickUpNotification o1, SchedulePickUpNotification o2) {
                long left = o1.getStart().toEpochSecond();
                long right = o2.getStart().toEpochSecond();
                if(left < right)
                {
                    return -1;
                }
                else if(left > right)
                {
                    return 1;
                }
                return 0; //they are equal
            }
        };
        this.queue = new PriorityQueue<>(comparator);
    }

    @PostConstruct
    public void start()
    {
        List<SchedulePickUpNotification> notifications = this.mongoDBJsonStore.getSchedulePickUpNotifications();
        for(SchedulePickUpNotification schedulePickUpNotification:notifications)
        {
            this.add(schedulePickUpNotification);
        }
    }

    public void add(SchedulePickUpNotification schedulePickUpNotification)
    {
        this.queue.add(schedulePickUpNotification);
        logger.info("QUEUE_SIZE: "+this.queue.size());
    }

    public SchedulePickUpNotification next()
    {
        return this.queue.poll();
    }

    public SchedulePickUpNotification peek()
    {
        return this.queue.peek();
    }

    public int size()
    {
        return this.queue.size();
    }

    public void remove(SchedulePickUpNotification notification)
    {
        this.queue.remove(notification);
    }

    public void clear()
    {
        this.queue.clear();
    }

    @Override
    public String toString()
    {
        //return this.queue.toString();
        return "QUEUE_SIZE: "+this.queue.size();
    }

    public void process()
    {
        if(this.size() == 0)
        {
            //logger.info("NOTIFICATION_PIPELINE_IS_EMPTY");
            /*try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            return;
        }
        SchedulePickUpNotification notification = this.peek();
        /*if (notification == null) {
            //logger.info("*******1*********");
            return;
        }*/

        //Check
        if (!notification.activateNotification()) {
            logger.info("NOTIFICATION_NOT_READY_TO_BE_PROCESSED_YET");
            this.remove(notification);
            return;
        }

        //logger.info("*******3*********");
        notification = this.next();
        notification.setNotificationSent(true);

        //logger.info("*******4*********");
        //Send
        this.mongoDBJsonStore.storeScheduledPickUpNotification(notification);
    }
}
