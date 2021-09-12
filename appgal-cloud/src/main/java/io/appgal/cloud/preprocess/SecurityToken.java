package io.appgal.cloud.preprocess;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;

public class SecurityToken implements Serializable {
    private String token;

    public SecurityToken()
    {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public static SecurityToken fromJson(String jsonString)
    {
        SecurityToken securityToken = new SecurityToken();
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        securityToken.token = jsonObject.get("access_token").getAsString();
        return securityToken;
    }
}
