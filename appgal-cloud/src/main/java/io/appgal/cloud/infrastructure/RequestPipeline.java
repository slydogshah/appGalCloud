package io.appgal.cloud.infrastructure;

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
        Thread loader = new Thread(new Runnable() {
            @Override
            public void run() {
                List<SchedulePickUpNotification> notifications = mongoDBJsonStore.getSchedulePickUpNotifications();
                for(SchedulePickUpNotification schedulePickUpNotification:notifications)
                {
                    add(schedulePickUpNotification);
                }
            }
        });
        loader.start();
    }

    public void add(SchedulePickUpNotification schedulePickUpNotification)
    {
        //logger.info("NOTIFICATION: "+schedulePickUpNotification.toString());

        this.queue.add(schedulePickUpNotification);
        //logger.info("PICKUP_QUEUE_SIZE: "+this.queue.size());
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
        return "QUEUE_SIZE: "+this.queue.size();
    }

    public void process()
    {
        if(this.size() == 0)
        {
            return;
        }
        SchedulePickUpNotification notification = this.peek();

        //Check
        if (!notification.activateNotification()) {
            logger.info("NOTIFICATION_NOT_READY_TO_BE_PROCESSED_YET");
            this.remove(notification);
            return;
        }

        notification = this.next();
        //notification.setNotificationSent(true);

        //Send
        this.mongoDBJsonStore.storeScheduledPickUpNotification(notification);
    }
}
