package io.appgal.cloud.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulePickUpNotification implements Serializable
{
    private static Logger logger = LoggerFactory.getLogger(SchedulePickUpNotification.class);

    private SourceOrg sourceOrg;
    private OffsetDateTime start;
    private FoodRunner foodRunner;

    public SchedulePickUpNotification()
    {

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

    public static SchedulePickUpNotification parse(String json)
    {
        SchedulePickUpNotification schedulePickUpNotification = new SchedulePickUpNotification();

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        if(jsonObject.has("sourceOrg"))
        {
            JsonObject sourceOrgJson = jsonObject.get("sourceOrg").getAsJsonObject();
            schedulePickUpNotification.sourceOrg = SourceOrg.parse(sourceOrgJson.getAsString());
        }

        return schedulePickUpNotification;
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("sourceOrg", this.sourceOrg.toJson());

        return jsonObject;
    }
}
