package io.appgal.cloud.model;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class Address implements Serializable {

    private String street;
    private String zip;
    private String timeZone;

    public Address() {
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public JsonObject toJson()
    {
        JsonObject json = new JsonObject();
        json.addProperty("street", this.street);
        json.addProperty("zip", this.zip);
        json.addProperty("timeZone", this.timeZone);
        return json;
    }
}
