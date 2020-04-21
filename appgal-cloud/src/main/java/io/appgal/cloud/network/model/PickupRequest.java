package io.appgal.cloud.network.model;

import com.google.gson.JsonObject;
import io.appgal.cloud.model.Location;
import io.appgal.cloud.model.SourceOrg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class PickupRequest implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(PickupRequest.class);

    private String requestId;
    private SourceOrg sourceOrg;

    public PickupRequest() {
    }

    public PickupRequest(String requestId, SourceOrg sourceOrg) {
        this.requestId = requestId;
        this.sourceOrg = sourceOrg;
    }

    public String getRequestId() {
        return requestId;
    }

    public SourceOrg getSourceOrg() {
        return sourceOrg;
    }

    public void setSourceOrg(SourceOrg sourceOrg) {
        this.sourceOrg = sourceOrg;
    }

    @Override
    public String toString()
    {
        return this.toJson().toString();
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("sourceOrg", this.sourceOrg.toJson());

        return jsonObject;
    }
}
