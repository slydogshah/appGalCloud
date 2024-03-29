package io.appgal.cloud.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

public class SourceOrg implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(SourceOrg.class);

    private String oid;
    private String orgId;
    private String orgName;
    private String orgContactEmail;
    private DeliveryPreference deliveryPreference;
    private Set<Profile> profiles;
    private boolean isProducer;
    private Address address;
    private Location location;

    public SourceOrg()
    {
        this.profiles = new HashSet<>();
    }

    public SourceOrg(String orgId, String orgName, String orgContactEmail, DeliveryPreference deliveryPreference,
                     Set<Profile> profiles, Location location, boolean isProducer) {
        this();
        this.orgId = orgId;
        this.orgName = orgName;
        this.orgContactEmail = orgContactEmail;
        this.deliveryPreference = deliveryPreference;
        this.profiles = profiles;
        this.location = location;
        this.isProducer = isProducer;
    }

    public SourceOrg(String orgId, String orgName, String orgContactEmail, boolean isProducer)
    {
        this();
        this.orgId = orgId;
        this.orgName = orgName;
        this.orgContactEmail = orgContactEmail;
        this.deliveryPreference = new DeliveryPreference();
        this.isProducer = isProducer;
    }

    public SourceOrg(String orgId, String orgName, String orgContactEmail, Address address, boolean isProducer)
    {
        this();
        this.orgId = orgId;
        this.orgName = orgName;
        this.orgContactEmail = orgContactEmail;
        this.deliveryPreference = new DeliveryPreference();
        this.isProducer = isProducer;
        this.address = address;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgContactEmail() {
        return orgContactEmail;
    }

    public void setOrgContactEmail(String orgContactEmail) {
        this.orgContactEmail = orgContactEmail;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public DeliveryPreference getDeliveryPreference() {
        return deliveryPreference;
    }

    public void setDeliveryPreference(DeliveryPreference deliveryPreference) {
        this.deliveryPreference = deliveryPreference;
    }

    public Set<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }

    public void addProfile(Profile profile)
    {
        profile.setSourceOrgId(this.orgId);
        this.profiles.add(profile);
    }

    public void deleteProfile(String email)
    {
        Profile profile = null;
        for(Profile cour:this.profiles)
        {
            if(cour.getEmail().equals(email.trim()))
            {
                profile = cour;
            }
        }
        this.profiles.remove(profile);
    }

    public boolean isProducer() {
        return isProducer;
    }

    public void setProducer(boolean producer) {
        isProducer = producer;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceOrg sourceOrg = (SourceOrg) o;
        return orgId.equals(sourceOrg.orgId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orgId);
    }

    @Override
    public String toString()
    {
        return this.toJson().toString();
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();

        if(this.oid != null) {
            jsonObject.addProperty("oid", this.oid);
        }
        if(this.orgId != null) {
            jsonObject.addProperty("orgId", this.orgId);
        }
        if(this.orgName != null) {
            jsonObject.addProperty("orgName", this.orgName);
        }
        if(this.orgContactEmail != null) {
            jsonObject.addProperty("orgContactEmail", this.orgContactEmail);
        }

        if(!this.profiles.isEmpty())
        {
            JsonArray jsonArray = JsonParser.parseString(this.profiles.toString()).getAsJsonArray();
            jsonObject.add("profiles", jsonArray);
        }

        if(this.address!= null && this.address.getStreet() != null)
        {
            jsonObject.addProperty("street",this.address.getStreet());
        }
        if(this.address!= null && this.address.getZip() != null)
        {
            jsonObject.addProperty("zip",this.address.getZip());
        }
        if(this.address != null && this.address.getTimeZone()!=null)
        {
            jsonObject.addProperty("timeZone",this.address.getTimeZone());
        }

        if(this.location != null)
        {
            jsonObject.addProperty("latitude",this.location.getLatitude());
            jsonObject.addProperty("longitude",this.location.getLongitude());
        }

        jsonObject.addProperty("producer", this.isProducer);

        return jsonObject;
    }

    public static SourceOrg parse(String json)
    {
        SourceOrg sourceOrg = new SourceOrg();

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        if(!jsonObject.has("orgName") || !jsonObject.has("orgId")
        || !jsonObject.has("orgContactEmail")) {
            throw new IllegalStateException("INVALID_DATA: "+json);
        }


        if(jsonObject.has("oid")) {
            sourceOrg.oid = jsonObject.get("oid").getAsString();
        }
        if(jsonObject.has("orgId")) {
            sourceOrg.orgId = jsonObject.get("orgId").getAsString();
        }
        if(jsonObject.has("orgName")) {
            sourceOrg.orgName = jsonObject.get("orgName").getAsString();
        }
        if(jsonObject.has("orgContactEmail")) {
            sourceOrg.orgContactEmail = jsonObject.get("orgContactEmail").getAsString();
        }
        if(jsonObject.has("profiles"))
        {
            JsonArray profiles = jsonObject.get("profiles").getAsJsonArray();
            Iterator<JsonElement> itr = profiles.iterator();
            while(itr.hasNext())
            {
                JsonElement jsonElement = itr.next();
                sourceOrg.getProfiles().add(Profile.parse(jsonElement.toString()));
            }
        }
        if(jsonObject.has("deliveryPreference")) {
            JsonArray jsonArray = jsonObject.getAsJsonArray("deliveryPreference");
            sourceOrg.deliveryPreference = DeliveryPreference.parse(jsonArray.toString());
        }

        Address address = new Address();
        if(jsonObject.has("street"))
        {
            address.setStreet(jsonObject.get("street").getAsString());
        }
        if(jsonObject.has("zip"))
        {
            address.setZip(jsonObject.get("zip").getAsString());
        }
        if(jsonObject.has("timeZone")) {
            address.setTimeZone(jsonObject.get("timeZone").getAsString());
        }
        sourceOrg.address = address;

        Location location = new Location();
        if(jsonObject.has("latitude"))
        {
           location.setLatitude(jsonObject.get("latitude").getAsDouble());
        }
        if(jsonObject.has("longitude"))
        {
            location.setLongitude(jsonObject.get("longitude").getAsDouble());
        }
        sourceOrg.setLocation(location);

        sourceOrg.isProducer = jsonObject.get("producer").getAsBoolean();

        return sourceOrg;
    }

    public static String generateOrgId(String original)
    {
        try{
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);

            byte[] hashedOrgId = md.digest(original.getBytes(StandardCharsets.UTF_8));
            String orgId = Base64.getUrlEncoder().withoutPadding().encodeToString(hashedOrgId);

            return orgId;
        }
        catch(Exception e){
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }
}
