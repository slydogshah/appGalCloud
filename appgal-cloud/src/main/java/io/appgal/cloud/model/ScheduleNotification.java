package io.appgal.cloud.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public abstract class ScheduleNotification implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(ScheduleNotification.class);

    protected String id;
    protected SourceOrg sourceOrg;
    protected FoodRunner foodRunner;
    protected OffsetDateTime start;
    protected boolean notificationSent = false;
    protected FoodDetails foodDetails;
    protected String scheduledStartTime;

    public ScheduleNotification(String id)
    {
        this.id = id;
        this.start = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public ScheduleNotification(String id, SourceOrg sourceOrg,OffsetDateTime start)
    {
        this(id);
        this.sourceOrg = sourceOrg;
        this.start = start;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SourceOrg getSourceOrg() {
        return sourceOrg;
    }

    public void setSourceOrg(SourceOrg sourceOrg) {
        this.sourceOrg = sourceOrg;
    }

    public FoodRunner getFoodRunner() {
        return foodRunner;
    }

    public void setFoodRunner(FoodRunner foodRunner) {
        this.foodRunner = foodRunner;
    }

    public OffsetDateTime getStart() {
        return start;
    }

    public void setStart(OffsetDateTime start) {
        this.start = start;
    }

    public String getScheduledStartTime() {
        return scheduledStartTime;
    }

    public void setScheduledStartTime(String scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    public boolean isNotificationSent()
    {
        return this.notificationSent;
    }

    public void setNotificationSent(boolean notificationSent) {

        this.notificationSent = notificationSent;
    }

    public FoodDetails getFoodDetails() {
        return foodDetails;
    }

    public void setFoodDetails(FoodDetails foodDetails) {
        this.foodDetails = foodDetails;
    }

    public boolean activateNotification()
    {
        long epochSecond = this.start.toEpochSecond();
        long current = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();

        if(epochSecond <= current)
        {
            return true;
        }

        return false;
    }

    public boolean isToday()
    {
        LocalDateTime startOfPickupDay = this.start.toLocalDate().atStartOfDay();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        LocalDateTime localDate = LocalDateTime.ofInstant(calendar.toInstant(),ZoneOffset.UTC);
        OffsetDateTime tomorrow = OffsetDateTime.ofInstant(localDate.plus(1, ChronoUnit.DAYS).toInstant(ZoneOffset.UTC),ZoneOffset.UTC);
        LocalDateTime startOfTomorrow = tomorrow.toLocalDate().atStartOfDay();

        boolean isToday = false;
        long tomorrowEpoch = startOfTomorrow.toEpochSecond(ZoneOffset.UTC);
        long pickupEpoch = startOfPickupDay.toEpochSecond(ZoneOffset.UTC);
        if( pickupEpoch < tomorrowEpoch )
        {
            isToday = true;
        }

        return isToday;
    }
}
