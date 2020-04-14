package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class Profile implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(Profile.class);

    private String id;
    private String email;
    private String mobile;
    private String photo;
    private String password;

    public Profile()
    {

    }

    public Profile(String id, String email, String mobile, String photo)
    {
        this.id = id;
        this.email = email;
        this.mobile = mobile;
        this.photo = photo;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getPhoto()
    {
        return photo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoto(String photo)
    {
        this.photo = photo;
    }



    @Override
    public String toString()
    {
        return this.toJson().toString();
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", this.id);
        jsonObject.addProperty("email", this.email);
        jsonObject.addProperty("mobile", this.mobile);
        jsonObject.addProperty("photo", this.photo);

        return jsonObject;
    }

    public static Profile parseProfile(JsonObject jsonObject)
    {
        Profile profile = new Profile();

        profile.id = jsonObject.get("id").getAsString();
        profile.email = jsonObject.get("email").getAsString();
        profile.mobile = jsonObject.get("mobile").getAsString();
        profile.photo = jsonObject.get("photo").getAsString();
        if(jsonObject.has("password")) {
            profile.password = jsonObject.get("password").getAsString();
        }

        return profile;
    }
}
