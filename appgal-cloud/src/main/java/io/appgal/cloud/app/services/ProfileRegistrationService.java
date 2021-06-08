package io.appgal.cloud.app.services;

import com.google.gson.*;

import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;

import io.appgal.cloud.network.services.NetworkOrchestrator;
import io.appgal.cloud.util.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.Locale;
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

    @Inject
    private MapUtils mapUtils;

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
        String sourceOrgId = sourceOrg.getOrgId();
        SourceOrg storedSourceOrg = this.mongoDBJsonStore.getSourceOrg(sourceOrgId);

        Location location = this.mapUtils.calculateCoordinates(sourceOrg.getAddress());
        sourceOrg.setLocation(location);

        if(storedSourceOrg == null)
        {
            this.mongoDBJsonStore.storeSourceOrg(sourceOrg);
            return;
        }

        Profile newProfile = sourceOrg.getProfiles().iterator().next();
        if(storedSourceOrg.getProfiles().contains(newProfile))
        {
            JsonObject message = new JsonObject();
            message.addProperty("email", newProfile.getEmail());
            throw new ResourceExistsException(message.toString());
        }

        this.mongoDBJsonStore.storeSourceOrg(sourceOrg);
    }

    public JsonObject login(String userAgent, String email, String password, Location location)
            throws AuthenticationException
    {
        JsonObject authFailure = new JsonObject();

        //logger.info("LOGIN_EMAIL: "+email);

        Profile profile = this.mongoDBJsonStore.getProfile(email);

        //logger.info("LOGIN_PROFILE: "+profile);

        if(profile == null)
        {
            authFailure.addProperty("message", "profile_not_found");
            throw new AuthenticationException(authFailure);
        }

        String registeredEmail = profile.getEmail();
        String registeredPassword = profile.getPassword();

        if(registeredEmail == null)
        {
            authFailure.addProperty("message", "profile_not_found");
            throw new AuthenticationException(authFailure);
        }

        if(registeredEmail.equals(email) && registeredPassword.equals(password))
        {
            JsonObject authResponse = new JsonObject();

            authResponse.add("profile", profile.toJson());

            FoodRunner foodRunner = this.activeNetwork.findFoodRunnerByEmail(email);
            if(foodRunner == null) {
                foodRunner = new FoodRunner();
                foodRunner.setProfile(profile);
                foodRunner.setLocation(location);
                this.networkOrchestrator.enterNetwork(foodRunner);
            }

            //logger.info("AUTHENTICATION_SUCCESS: "+email);
            return authResponse;
        }

        authFailure.addProperty("message", "password_mismatch");
        throw new AuthenticationException(authFailure);
    }

    public JsonObject orgLogin(String userAgent, String email, String password) throws AuthenticationException
    {
        JsonObject authFailure = new JsonObject();
        Profile profile = this.mongoDBJsonStore.getProfile(email);
        if(profile == null)
        {
            authFailure.addProperty("message", "profile_not_found");
            throw new AuthenticationException(authFailure);
        }

        String registeredEmail = profile.getEmail();
        String registeredPassword = profile.getPassword();

        if(registeredEmail == null)
        {
            authFailure.addProperty("message", "profile_not_found");
            throw new AuthenticationException(authFailure);
        }

        if(registeredEmail.equals(email) && registeredPassword.equals(password))
        {
            SourceOrg sourceOrg = this.mongoDBJsonStore.getSourceOrg(profile.getSourceOrgId());
            JsonObject jsonObject = new JsonObject();
            JsonObject profileJson = profile.toJson();
            profileJson.remove("password");
            jsonObject.add("profile", profileJson);
            jsonObject.add("sourceOrg", sourceOrg.toJson());
            return jsonObject;
        }

        authFailure.addProperty("message", "password_mismatch");
        throw new AuthenticationException(authFailure);
    }
}
