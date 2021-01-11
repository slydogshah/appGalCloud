package io.appgal.cloud.model;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulePickUpNotification extends ScheduleNotification
{
    private static Logger logger = LoggerFactory.getLogger(SchedulePickUpNotification.class);

    public SchedulePickUpNotification(String id)
    {
        super(id);
    }

    public SchedulePickUpNotification(String id, SourceOrg sourceOrg,OffsetDateTime start)
    {
        this(id);
        this.sourceOrg = sourceOrg;
        this.start = start;
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
