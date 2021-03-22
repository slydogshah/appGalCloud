package io.appgal.cloud.model;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulePickUpNotification extends ScheduleNotification
{
    private static Logger logger = LoggerFactory.getLogger(SchedulePickUpNotification.class);

    private List<Note> pickupNotes;
    private boolean isDropOffDynamic;

    public SchedulePickUpNotification(String id)
    {
        super(id);
        this.pickupNotes = new ArrayList<>();
    }

    public SchedulePickUpNotification(String id, SourceOrg sourceOrg,OffsetDateTime start)
    {
        this(id);
        this.sourceOrg = sourceOrg;
        this.start = start;
    }

    public List<Note> getPickupNotes() {
        return pickupNotes;
    }

    public void setPickupNotes(List<Note> pickupNotes) {
        this.pickupNotes = pickupNotes;
    }

    public void addPickupNote(Note pickupNote)
    {
        this.pickupNotes.add(pickupNote);
    }

    public boolean isDropOffDynamic() {
        return isDropOffDynamic;
    }

    public void setDropOffDynamic(boolean dropOffDynamic) {
        isDropOffDynamic = dropOffDynamic;
    }

    public static SchedulePickUpNotification parse(String json)
    {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String id;
        if(jsonObject.has("id"))
        {
            id = jsonObject.get("id").getAsString();
        }
        else
        {
            id = UUID.randomUUID().toString();
        }

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
        if(jsonObject.has("pickupNotes"))
        {
            JsonArray array = jsonObject.get("pickupNotes").getAsJsonArray();
            Iterator<JsonElement> itr = array.iterator();
            while(itr.hasNext())
            {
                JsonObject noteJson = itr.next().getAsJsonObject();
                schedulePickUpNotification.pickupNotes.add(Note.parse(noteJson.toString()));
            }
        }
        if(jsonObject.has("foodDetails"))
        {
            schedulePickUpNotification.foodDetails = FoodDetails.parse(jsonObject.get("foodDetails").toString());
        }
        if(jsonObject.has("isDropOffDynamic"))
        {
            schedulePickUpNotification.isDropOffDynamic = jsonObject.get("isDropOffDynamic").getAsBoolean();
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
        if(this.pickupNotes != null) {
            jsonObject.add("pickupNotes", JsonParser.parseString(this.pickupNotes.toString()));
        }
        if(this.foodDetails != null)
        {
            jsonObject.add("foodDetails", JsonParser.parseString(this.foodDetails.toString()));
        }

        jsonObject.addProperty("notificationSent", this.notificationSent);
        jsonObject.addProperty("isDropOffDynamic", this.isDropOffDynamic);

        return jsonObject;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
