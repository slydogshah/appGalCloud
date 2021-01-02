package io.appgal.cloud.app.services;

import com.google.gson.JsonObject;
import io.appgal.cloud.model.ProfileType;

public class DifferentContextAuthException extends Exception {
    private String email;
    private ProfileType profileType;

    public DifferentContextAuthException() {
        super();
    }

    public DifferentContextAuthException(String email, ProfileType profileType)
    {
        this();
        this.email = email;
        this.profileType = profileType;
    }

    public String getEmail() {
        return email;
    }

    public ProfileType getProfileType() {
        return profileType;
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", this.email);
        jsonObject.addProperty("profileType", this.profileType.name());
        return jsonObject.toString();
    }
}
