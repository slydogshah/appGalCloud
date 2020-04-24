package io.appgal.cloud.services;

import com.google.gson.JsonObject;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.persistence.MongoDBJsonStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ProfileRegistrationService {
    private static Logger logger = LoggerFactory.getLogger(ProfileRegistrationService.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    public Profile getProfile(String email)
    {
        Profile profile = this.mongoDBJsonStore.getProfile(email);
        return profile;
    }

    public void register(Profile profile)
    {
        this.mongoDBJsonStore.storeProfile(profile);
    }

    public JsonObject login(String email, String password)
    {
        Profile profile = this.getProfile(email);

        String registeredEmail = profile.getEmail();
        String registeredPassword = profile.getPassword();
        boolean authSuccess = false;
        if(registeredEmail.equals(email) && registeredPassword != null && registeredPassword.equals(password))
        {
            authSuccess = true;
        }

        JsonObject authResponse = new JsonObject();

        if(authSuccess) {
            authResponse.addProperty("statusCode", 200);
        }
        else
        {
            authResponse.addProperty("statusCode", 401);
        }

        return authResponse;
    }
}
