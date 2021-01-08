package io.appgal.cloud.network.services;

import io.appgal.cloud.model.SchedulePickUpNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Comparator;
import java.util.PriorityQueue;

@Singleton
public class RequestPipeline {
    private static Logger logger = LoggerFactory.getLogger(RequestPipeline.class);

    private PriorityQueue<SchedulePickUpNotification> queue;

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

    public void add(SchedulePickUpNotification schedulePickUpNotification)
    {
        this.queue.add(schedulePickUpNotification);
    }

    public SchedulePickUpNotification next()
    {
        return this.queue.poll();
    }

    public void clear()
    {
        this.queue.clear();
    }

    @Override
    public String toString()
    {
        return this.queue.toString();
    }
}
