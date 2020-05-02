package io.appgal.cloud.services;

import com.google.gson.JsonObject;
import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.model.FoodRunner;
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

    @Inject
    private ActiveNetwork activeNetwork;

    public JsonObject getProfile(String email)
    {
        Profile profile = this.mongoDBJsonStore.getProfile(email);

        FoodRunner foodRunner = this.activeNetwork.findFoodRunner(profile.getId());

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("profile", profile.toJson());
        jsonObject.addProperty("latitude", foodRunner.getLocation().getLatitude());
        jsonObject.addProperty("longitude", foodRunner.getLocation().getLongitude());

        return jsonObject;
    }

    public void register(Profile profile)
    {
        //TODO: Add validation
        this.mongoDBJsonStore.storeProfile(profile);
    }

    public JsonObject login(String email, String password)
    {
        JsonObject reject = new JsonObject();
        reject.addProperty("statusCode", "401");

        JsonObject jsonObject = this.getProfile(email);
        Profile profile = Profile.parse(jsonObject.toString());
        if(profile == null)
        {
            return reject;
        }

        String registeredEmail = profile.getEmail();
        String registeredPassword = profile.getPassword();
        if(registeredEmail == null)
        {
            return reject;
        }

        if(registeredEmail.equals(email) && registeredPassword.equals(password))
        {
            JsonObject authResponse = new JsonObject();
            authResponse.addProperty("statusCode", 200);

            FoodRunner foodRunner = this.activeNetwork.findFoodRunner(profile.getId());
            authResponse.addProperty("latitude", foodRunner.getLocation().getLatitude());
            authResponse.addProperty("longitude", foodRunner.getLocation().getLongitude());

            return authResponse;
        }
        return reject;
    }
}
