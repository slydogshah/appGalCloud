package io.appgal.cloud.app.services;

import com.google.gson.JsonObject;

public class AuthenticationException extends Exception {
    private String email;

    public AuthenticationException() {
        super();
    }

    public AuthenticationException(String email)
    {
        this();
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", this.email);
        return jsonObject.toString();
    }
}
