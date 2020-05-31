package io.appgal.cloud.services;

import com.google.gson.JsonObject;
import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.model.Location;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.persistence.MongoDBJsonStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class ProfileRegistrationService {
    private static Logger logger = LoggerFactory.getLogger(ProfileRegistrationService.class);

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    private ActiveNetwork activeNetwork;

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    public Profile getProfile(String email)
    {
        Profile profile = this.mongoDBJsonStore.getProfile(email);
        return profile;
    }

    public void register(Profile profile)
    {
        //TODO: Add validation..Add proper response
        Profile exists = this.mongoDBJsonStore.getProfile(profile.getEmail());
        if(exists != null)
        {
            return;
        }

        profile.setId(UUID.randomUUID().toString());
        this.mongoDBJsonStore.storeProfile(profile);
    }

    public JsonObject login(String email, String password)
    {
        JsonObject reject = new JsonObject();
        reject.addProperty("statusCode", 401);

        Profile profile = this.mongoDBJsonStore.getProfile(email);
        if(profile == null)
        {
            logger.info("PROFILE_NOT_FOUND");
            return reject;
        }

        String registeredEmail = profile.getEmail();
        String registeredPassword = profile.getPassword();

        if(registeredEmail == null)
        {
            logger.info("EMAIL_NOT_FOUND");
            return reject;
        }

        logger.info(registeredEmail);
        logger.info(email);
        logger.info(registeredPassword);
        logger.info(password);

        if(registeredEmail.equals(email) && registeredPassword.equals(password))
        {
            JsonObject authResponse = new JsonObject();
            authResponse.addProperty("statusCode", 200);

            //TODO: Unmock
            Location location = new Location(30.25860595703125d, -97.74873352050781d);
            FoodRunner foodRunner = new FoodRunner(profile, location);
            this.networkOrchestrator.enterNetwork(foodRunner);
            foodRunner = this.activeNetwork.findFoodRunner(profile.getId());
            if(foodRunner != null) {
                authResponse.addProperty("latitude", foodRunner.getLocation().getLatitude());
                authResponse.addProperty("longitude", foodRunner.getLocation().getLongitude());
            }
            else
            {
                authResponse.addProperty("latitude", 30.25860595703125d);
                authResponse.addProperty("longitude", -97.74873352050781d);
            }

            profile.setLocation(location);
            authResponse.add("profile", profile.toJson());

            logger.info("AUTHENTICATION_SUCCESS");
            return authResponse;
        }

        logger.info("AUTHENTICATION_FAILED");
        return reject;
    }
}
