package io.appgal.cloud.model;

import java.time.*;
import java.time.format.DateTimeFormatter;
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
    private SourceOrg dropOffOrg;

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

    public SourceOrg getDropOffOrg() {
        return dropOffOrg;
    }

    public void setDropOffOrg(SourceOrg dropOffOrg) {
        this.dropOffOrg = dropOffOrg;
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
            ZoneId zoneId = ZoneId.of(schedulePickUpNotification.sourceOrg.getAddress().getTimeZone());
            LocalDateTime localDateTime = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
            ZoneOffset zoneOffset = zonedDateTime.getOffset();
            long startEpochSecond = jsonObject.get("start").getAsLong();
            schedulePickUpNotification.start = OffsetDateTime.ofInstant(Instant.ofEpochSecond(startEpochSecond),
                    zoneOffset);
        }
        if(jsonObject.has("scheduled"))
        {
            schedulePickUpNotification.scheduledStartTime = jsonObject.get("scheduled").getAsString();
        }
        if(jsonObject.has("notificationSent"))
        {
            schedulePickUpNotification.setNotificationSent(jsonObject.get("notificationSent").getAsBoolean());
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

        if(jsonObject.has("dropOffOrg"))
        {
            JsonObject sourceOrgJson = jsonObject.get("dropOffOrg").getAsJsonObject();
            schedulePickUpNotification.dropOffOrg = SourceOrg.parse(sourceOrgJson.toString());
        }

        return schedulePickUpNotification;
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();

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

        if(this.scheduledStartTime != null){
            jsonObject.addProperty("scheduled", this.scheduledStartTime);
        }
        else
        {
            jsonObject.addProperty("scheduled", "Not Available");
        }

        if(this.pickupNotes != null) {
            jsonObject.add("pickupNotes", JsonParser.parseString(this.pickupNotes.toString()));
        }
        if(this.foodDetails != null)
        {
            jsonObject.add("foodDetails", this.foodDetails.toJson());
        }

        jsonObject.addProperty("notificationSent", this.isNotificationSent());
        jsonObject.addProperty("isDropOffDynamic", this.isDropOffDynamic);

        if(this.id != null)
        {
            jsonObject.addProperty("id", this.id);
        }
        if(this.dropOffOrg != null)
        {
            jsonObject.add("dropOffOrg", this.dropOffOrg.toJson());
        }

        return jsonObject;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
