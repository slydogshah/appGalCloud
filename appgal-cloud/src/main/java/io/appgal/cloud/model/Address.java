package io.appgal.cloud.model;

import java.io.Serializable;

public class Address implements Serializable {

    private String street;
    private String zip;

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
}
