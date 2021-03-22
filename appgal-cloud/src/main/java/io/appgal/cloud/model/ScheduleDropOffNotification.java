package io.appgal.cloud.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScheduleDropOffNotification extends ScheduleNotification
{
    private static Logger logger = LoggerFactory.getLogger(ScheduleDropOffNotification.class);

    private List<Note> dropOffNotes;

    public ScheduleDropOffNotification(String id)
    {
        super(id);
        this.dropOffNotes = new ArrayList<>();
    }

    public ScheduleDropOffNotification(String id, SourceOrg sourceOrg, OffsetDateTime start)
    {
        this(id);
        this.sourceOrg = sourceOrg;
        this.start = start;
    }

    public List<Note> getDropOffNotes() {
        return dropOffNotes;
    }

    public void setDropOffNotes(List<Note> dropOffNotes) {
        this.dropOffNotes = dropOffNotes;
    }

    public void addDropOffNote(Note dropOffNote)
    {
        this.dropOffNotes.add(dropOffNote);
    }

    public static ScheduleDropOffNotification parse(String json)
    {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String id = jsonObject.get("id").getAsString();

        ScheduleDropOffNotification schedulePickUpNotification = new ScheduleDropOffNotification(id);

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
        if(jsonObject.has("dropOffNotes"))
        {
            JsonArray array = jsonObject.get("dropOffNotes").getAsJsonArray();
            Iterator<JsonElement> itr = array.iterator();
            while(itr.hasNext())
            {
                JsonObject noteJson = itr.next().getAsJsonObject();
                schedulePickUpNotification.dropOffNotes.add(Note.parse(noteJson.toString()));
            }
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
        jsonObject.add("dropOffNotes",JsonParser.parseString(this.dropOffNotes.toString()));

        return jsonObject;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
