package io.appgal.cloud.network.model;

import io.appgal.cloud.model.Location;
import io.appgal.cloud.model.SourceOrg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class PickupRequest implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(PickupRequest.class);

    private SourceOrg sourceOrg;
    private Location location;

    public PickupRequest() {
    }

    public PickupRequest(SourceOrg sourceOrg, Location location) {
        this.sourceOrg = sourceOrg;
        this.location = location;
    }

    public SourceOrg getSourceOrg() {
        return sourceOrg;
    }

    public void setSourceOrg(SourceOrg sourceOrg) {
        this.sourceOrg = sourceOrg;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
