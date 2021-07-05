package io.appgal.cloud.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.*;
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

        ZoneId id = ZoneId.of(sourceOrg.getAddress().getTimeZone());
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = localDateTime.atZone(id);
        ZoneOffset zoneOffset = zonedDateTime.getOffset();
        long current = OffsetDateTime.now(zoneOffset).toEpochSecond();

        if(epochSecond <= current)
        {
            return true;
        }

        return false;
    }

    public boolean isToday()
    {
        LocalDateTime startOfPickupDay = this.start.toLocalDate().atStartOfDay();

        ZoneId id = ZoneId.of(sourceOrg.getAddress().getTimeZone());
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = localDateTime.atZone(id);
        ZoneOffset zoneOffset = zonedDateTime.getOffset();

        ZoneOffset tomOffset = zoneOffset;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        LocalDateTime localDate = LocalDateTime.ofInstant(calendar.toInstant(),tomOffset);
        OffsetDateTime tomorrow = OffsetDateTime.ofInstant(localDate.plus(2, ChronoUnit.DAYS).
                toInstant(tomOffset),tomOffset);
        LocalDateTime startOfTomorrow = tomorrow.toLocalDate().atStartOfDay();


        //TODO
        /*for(int i=0; i<3; i++) {
            ZoneId testId = ZoneId.of("US/Pacific-New");
            // LocalDateTime -> ZonedDateTime
            LocalDateTime testTime = LocalDateTime.now();
            ZonedDateTime testDateTime = testTime.atZone(id);
            //ZoneOffset testOffset = ZoneOffset.UTC;
            ZoneOffset testOffset = zonedDateTime.getOffset();

            Calendar testCalendar = Calendar.getInstance();
            testCalendar.set(Calendar.HOUR_OF_DAY,0);
            testCalendar.set(Calendar.MINUTE,0);
            LocalDateTime test1 = LocalDateTime.ofInstant(testCalendar.toInstant(), testOffset);
            OffsetDateTime test1Tom = OffsetDateTime.ofInstant(test1.plus(i, ChronoUnit.DAYS).
                    toInstant(testOffset), testOffset);
            LocalDateTime test1Tomorrow = test1Tom.toLocalDate().atStartOfDay();
            System.out.println("TEST: " + test1Tomorrow.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }*/


        boolean isToday = false;
        long tomorrowEpoch = startOfTomorrow.toEpochSecond(tomOffset);
        long pickupEpoch = startOfPickupDay.toEpochSecond(zoneOffset);
        if( pickupEpoch < tomorrowEpoch )
        {
            isToday = true;
        }

        return isToday;

        /*ZoneId id = ZoneId.of(sourceOrg.getAddress().getTimeZone());
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = localDateTime.atZone(id);
        ZoneOffset zoneOffset = zonedDateTime.getOffset();
        //ZoneOffset zoneOffset = ZoneOffset.UTC;
        System.out.println(zoneOffset);
        System.out.println(start.toEpochSecond());
        System.out.println(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return false;*/
    }
}
