package io.appgal.cloud.app.services;

import com.google.gson.*;

import io.appgal.cloud.model.ActiveNetwork;
import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.model.Location;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;

import io.appgal.cloud.network.services.NetworkOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;
import java.util.List;

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

    public void register(Profile profile) throws ResourceExistsException
    {
        String email = profile.getEmail();
        Profile exists = this.mongoDBJsonStore.getProfile(email);
        if(exists != null)
        {
            JsonObject message = new JsonObject();
            message.addProperty("email",email);
            throw new ResourceExistsException(message.toString());
        }

        profile.setId(UUID.randomUUID().toString());
        this.mongoDBJsonStore.storeProfile(profile);
    }

    public void registerSourceOrg(SourceOrg sourceOrg) throws ResourceExistsException
    {
        //TODO: Add validation..Add proper response
        String sourceOrgId = sourceOrg.getOrgId();
        SourceOrg exists = this.mongoDBJsonStore.getSourceOrg(sourceOrgId);
        if(exists != null)
        {
            JsonObject message = new JsonObject();
            message.addProperty("sourceOrgId",sourceOrgId);
            throw new ResourceExistsException(message.toString());
        }

        this.mongoDBJsonStore.storeSourceOrg(sourceOrg);
    }

    public JsonObject login(String email, String password) throws AuthenticationException
    {
        logger.info("******LOGIN*******");
        logger.info("Email:"+email);
        logger.info("Password:"+password);
        logger.info("*************************");

        Profile profile = this.mongoDBJsonStore.getProfile(email);
        if(profile == null)
        {
            logger.info("PROFILE_NOT_FOUND: "+email);
            throw new AuthenticationException(email);
        }

        String registeredEmail = profile.getEmail();
        String registeredPassword = profile.getPassword();

        if(registeredEmail == null)
        {
            logger.info("EMAIL_NOT_FOUND");
            throw new AuthenticationException(email);
        }

        //logger.info(registeredEmail);
        //logger.info(email);
        //logger.info(registeredPassword);
        //logger.info(password);

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

            this.networkOrchestrator.enterNetwork(foodRunner);
            List<SourceOrg> match = this.activeNetwork.matchFoodRunner(foodRunner);
            JsonArray matchArray = new JsonArray();
            for(SourceOrg sourceOrg:match)
            {
                if(sourceOrg.isProducer()) {
                    matchArray.add(sourceOrg.toJson());
                }
            }
            authResponse.add("sourceOrgs", matchArray);

            //logger.info("AUTHENTICATION_SUCCESS");
            return authResponse;
        }

        logger.info("AUTHENTICATION_FAILED");
        throw new AuthenticationException(email);
    }
}
