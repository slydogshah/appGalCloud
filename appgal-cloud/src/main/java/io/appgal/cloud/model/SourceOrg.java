package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;

public class SourceOrg implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(SourceOrg.class);

    private String orgId;
    private String orgName;
    private String orgContactEmail;

    public SourceOrg()
    {

    }

    public SourceOrg(String orgId, String orgName, String orgContactEmail)
    {
        this.orgId = orgId;
        this.orgName = orgName;
        this.orgContactEmail = orgContactEmail;
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
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("orgId", this.orgId);
        jsonObject.addProperty("orgName", this.orgName);
        jsonObject.addProperty("orgContactEmail", this.orgContactEmail);

        return jsonObject.toString();
    }

    //Fix this shit...this is bullshit
    public static SourceOrg parseJson(JsonObject jsonObject)
    {
        SourceOrg sourceOrg = new SourceOrg();

        sourceOrg.orgId = jsonObject.get("orgId").getAsString();
        sourceOrg.orgName = jsonObject.get("orgName").getAsString();
        sourceOrg.orgContactEmail = jsonObject.get("orgContactEmail").getAsString();

        return sourceOrg;
    }
}
