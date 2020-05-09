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

    public JsonObject getProfile(String email)
    {
        Profile profile = this.mongoDBJsonStore.getProfile(email);

        FoodRunner foodRunner = this.activeNetwork.findFoodRunner(profile.getId());

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("profile", profile.toJson());
        if(foodRunner != null) {
            jsonObject.addProperty("latitude", foodRunner.getLocation().getLatitude());
            jsonObject.addProperty("longitude", foodRunner.getLocation().getLongitude());
        }

        return jsonObject;
    }

    public void register(Profile profile)
    {
        //TODO: Add validation
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
            return reject;
        }

        String registeredEmail = profile.getEmail();
        String registeredPassword = profile.getPassword();

        if(registeredEmail == null)
        {
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

            return authResponse;
        }
        return reject;
    }
}
