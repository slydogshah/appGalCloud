package io.appgal.cloud.app.services;

import com.google.gson.JsonObject;

public class AuthenticationException extends Exception {
    private JsonObject authFailure;


    public AuthenticationException(JsonObject authFailure)
    {
        super(authFailure.toString());
        this.authFailure = authFailure;
    }

    @Override
    public String toString() {
        return authFailure.toString();
    }
}
