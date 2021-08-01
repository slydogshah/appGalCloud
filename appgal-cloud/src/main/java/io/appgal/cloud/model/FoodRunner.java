package io.appgal.cloud.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public class FoodRunner implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(FoodRunner.class);

    private Profile profile;
    private Location location;
    private SourceOrg pickUpOrg;
    private Set<String> pushTokens;

    public FoodRunner(){
        this.pushTokens = new HashSet<>();
    }

    public FoodRunner(Profile profile) {
        this();
        this.profile = profile;
    }

    public FoodRunner(Profile profile, Location location) {
        this(profile);
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public SourceOrg getPickUpOrg() {
        return pickUpOrg;
    }

    public void setPickUpOrg(SourceOrg pickUpOrg) {
        this.pickUpOrg = pickUpOrg;
    }


    public boolean isOfflineCommunitySupport() {
        return this.profile.isOfflineCommunitySupport();
    }

    public void setOfflineCommunitySupport(boolean offlineCommunitySupport) {
        this.profile.setOfflineCommunitySupport(offlineCommunitySupport);
    }

    public Set<String> getPushTokens() {
        return pushTokens;
    }

    public void setPushTokens(Set<String> pushTokens) {
        this.pushTokens = pushTokens;
    }

    public void addPushToken(String pushToken)
    {
        this.pushTokens.add(pushToken);
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    public static FoodRunner parse(String json)
    {
        FoodRunner foodRunner = new FoodRunner();
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        if(jsonObject.has("profile")) {
            Profile profile = Profile.parse(jsonObject.get("profile").toString());
            foodRunner.setProfile(profile);
        }
        if(jsonObject.has("location")) {
            Location location = Location.parse(jsonObject.get("location").toString());
            foodRunner.setLocation(location);
        }
        if(jsonObject.has("pickUpOrg")) {
            foodRunner.setPickUpOrg(SourceOrg.parse(jsonObject.get("pickUpOrg").getAsJsonObject().toString()));
        }
        if(jsonObject.has("pushTokens")) {
            foodRunner.pushTokens = new HashSet<>();
            JsonArray array = jsonObject.getAsJsonArray("pushTokens");
            Iterator<JsonElement> itr = array.iterator();
            while(itr.hasNext()){
                foodRunner.addPushToken(itr.next().getAsString());
            }
        }
        return foodRunner;
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();

        if(this.profile != null) {
            jsonObject.add("profile", this.profile.toJson());
        }
        if(this.location != null) {
            jsonObject.add("location", this.location.toJson());
        }
        if(this.pickUpOrg != null) {
            jsonObject.add("pickUpOrg", this.pickUpOrg.toJson());
        }
        if(this.pushTokens != null && !this.pushTokens.isEmpty()){
            JsonArray jsonArray = new JsonArray();
            for(String pushToken:this.pushTokens){
                jsonArray.add(pushToken);
            }
            jsonObject.add("pushTokens",jsonArray);
        }


        return jsonObject;
    }
}
