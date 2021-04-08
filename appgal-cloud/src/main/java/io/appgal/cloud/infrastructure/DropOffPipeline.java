package io.appgal.cloud.infrastructure;

import io.appgal.cloud.model.ScheduleDropOffNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Singleton
public class DropOffPipeline {
    private static Logger logger = LoggerFactory.getLogger(DropOffPipeline.class);

    private PriorityQueue<ScheduleDropOffNotification> dropOffQueue;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    public DropOffPipeline()
    {
        Comparator<ScheduleDropOffNotification> dcomparator = new Comparator<ScheduleDropOffNotification>() {
            @Override
            public int compare(ScheduleDropOffNotification o1, ScheduleDropOffNotification o2) {
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
        this.dropOffQueue = new PriorityQueue<>(dcomparator);
    }

    @PostConstruct
    public void start()
    {
        Thread loader = new Thread(new Runnable() {
            @Override
            public void run() {
                List<ScheduleDropOffNotification> dnotifications = mongoDBJsonStore.getScheduledDropOffNotifications();
                for(ScheduleDropOffNotification scheduleDropOffNotification:dnotifications)
                {
                    add(scheduleDropOffNotification);
                }
            }
        });
        loader.start();
    }

    public void add(ScheduleDropOffNotification notification)
    {
        this.dropOffQueue.add(notification);
        //logger.info("DROPOFF_QUEUE_SIZE: "+this.dropOffQueue.size());
    }

    public ScheduleDropOffNotification next()
    {
        return this.dropOffQueue.poll();
    }

    public ScheduleDropOffNotification peek()
    {
        return this.dropOffQueue.peek();
    }

    public int size()
    {
        return this.dropOffQueue.size();
    }

    public void remove(ScheduleDropOffNotification notification)
    {
        this.dropOffQueue.remove(notification);
    }

    public void clear()
    {
        this.dropOffQueue.clear();
    }

    @Override
    public String toString()
    {
        return this.dropOffQueue.toString();
    }

    public void process()
    {
        ScheduleDropOffNotification notification = this.peek();
        if (notification == null) {
            return;
        }
        //Check
        if (!notification.activateNotification()) {
            this.remove(notification);
            return;
        }

        notification = this.next();
        //notification.setNotificationSent(true);

        //Send
        this.mongoDBJsonStore.updateScheduledDropOffNotification(notification);
    }

}
