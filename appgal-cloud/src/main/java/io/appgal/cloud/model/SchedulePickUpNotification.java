package io.appgal.cloud.model;

import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.appgal.cloud.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulePickUpNotification implements Serializable
{
    private static Logger logger = LoggerFactory.getLogger(SchedulePickUpNotification.class);

    private String id;
    private SourceOrg sourceOrg;
    private FoodRunner foodRunner;
    private OffsetDateTime start;
    private boolean notificationSent;

    public SchedulePickUpNotification(String id)
    {
        this.id = id;
    }

    public SchedulePickUpNotification(String id, SourceOrg sourceOrg,OffsetDateTime start)
    {
        this(id);
        this.sourceOrg = sourceOrg;
        this.start = start;
    }

    public SourceOrg getSourceOrg() {
        return sourceOrg;
    }

    public void setSourceOrg(SourceOrg sourceOrg) {
        this.sourceOrg = sourceOrg;
    }

    public OffsetDateTime getStart() {
        return start;
    }

    public void setStart(OffsetDateTime start) {
        this.start = start;
    }

    public FoodRunner getFoodRunner() {
        return foodRunner;
    }

    public void setFoodRunner(FoodRunner foodRunner) {
        this.foodRunner = foodRunner;
    }

    public boolean isNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public static SchedulePickUpNotification parse(String json)
    {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String id = jsonObject.get("id").getAsString();

        SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification(id);

        if(jsonObject.has("sourceOrg"))
        {
            JsonObject sourceOrgJson = jsonObject.get("sourceOrg").getAsJsonObject();
            schedulePickUpNotification.sourceOrg = SourceOrg.parse(sourceOrgJson.toString());
        }
        if(jsonObject.has("foodRunner"))
        {
            JsonObject foodRunnerJson = jsonObject.get("foodRunner").getAsJsonObject();
            schedulePickUpNotification.foodRunner = FoodRunner.parse(foodRunnerJson.toString());
        }
        if(jsonObject.has("start"))
        {
            long startEpochSecond = jsonObject.get("start").getAsLong();
            schedulePickUpNotification.start = OffsetDateTime.ofInstant(Instant.ofEpochSecond(startEpochSecond),ZoneOffset.UTC);
        }
        if(jsonObject.has("notificationSent"))
        {
            schedulePickUpNotification.notificationSent = jsonObject.get("notificationSent").getAsBoolean();
        }
        return schedulePickUpNotification;
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();

        if(this.id != null)
        {
            jsonObject.addProperty("id", this.id);
        }
        if(this.sourceOrg != null) {
            jsonObject.add("sourceOrg", this.sourceOrg.toJson());
        }
        if(this.foodRunner != null) {
            jsonObject.add("foodRunner", this.foodRunner.toJson());
        }
        if(this.start != null)
        {
            jsonObject.addProperty("start", this.start.toEpochSecond());
        }
        jsonObject.addProperty("notificationSent", this.notificationSent);

        return jsonObject;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
