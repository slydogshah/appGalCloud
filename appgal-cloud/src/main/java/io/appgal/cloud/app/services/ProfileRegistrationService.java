package io.appgal.cloud.app.services;

import com.google.gson.*;

import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;

import io.appgal.cloud.network.services.NetworkOrchestrator;
import io.appgal.cloud.util.JsonUtil;
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

    public void registerStaff(String orgId,Profile profile) throws ResourceExistsException
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

        Profile storedProfile = this.mongoDBJsonStore.getProfile(profile.getEmail());
        SourceOrg sourceOrg = this.mongoDBJsonStore.getSourceOrg(orgId);
        sourceOrg.addProfile(storedProfile);
        this.mongoDBJsonStore.storeSourceOrg(sourceOrg);

        SourceOrg storedSourceOrg = this.mongoDBJsonStore.getSourceOrg(sourceOrg.getOrgId());
        System.out.println(storedProfile.toJson().toString());
        System.out.println(storedSourceOrg.toJson().toString());
    }

    public SourceOrg registerSourceOrg(String email,SourceOrg sourceOrg) throws ResourceExistsException
    {
        Profile storedProfile = this.mongoDBJsonStore.getProfile(email);
        if(storedProfile != null){
            JsonObject message = new JsonObject();
            message.addProperty("email", email);
            throw new ResourceExistsException(message.toString());
        }

        Location location = this.mapUtils.calculateCoordinates(sourceOrg.getAddress());
        sourceOrg.setLocation(location);
        SourceOrg storedSourceOrg = this.findSourceOrg(sourceOrg);
        if(storedSourceOrg == null)
        {
            this.mongoDBJsonStore.storeSourceOrg(sourceOrg);
            return sourceOrg;
        }

        Profile newProfile = sourceOrg.getProfiles().iterator().next();
        if(storedSourceOrg.getProfiles().contains(newProfile))
        {
            JsonObject message = new JsonObject();
            message.addProperty("email", newProfile.getEmail());
            throw new ResourceExistsException(message.toString());
        }
        storedSourceOrg.addProfile(newProfile);
        this.mongoDBJsonStore.storeSourceOrg(storedSourceOrg);
        return storedSourceOrg;
    }

    public SourceOrg findSourceOrg(SourceOrg newSourceOrg){
        Location newOrgLocation = newSourceOrg.getLocation();
        List<SourceOrg> all = this.mongoDBJsonStore.getSourceOrgs();
        for(SourceOrg cour:all){
            Location location = cour.getLocation();
            double distance = this.mapUtils.calculateDistance(newOrgLocation.getLatitude(),
                    newOrgLocation.getLongitude(),location.getLatitude(),location.getLongitude());

            if(distance <= 0.0d){
                return cour;
            }
        }

        return null;
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

            FoodRunner foodRunner = this.activeNetwork.findFoodRunnerByEmail(email);
            if(foodRunner == null) {
                foodRunner = new FoodRunner();
            }
            foodRunner.setProfile(profile);
            if(location != null && location.getLatitude() != 0.0d) {
                foodRunner.setLocation(location);
            }
            this.networkOrchestrator.enterNetwork(foodRunner);

            authResponse.addProperty("offlineCommunitySupport",foodRunner.isOfflineCommunitySupport());

            String bearerToken = UUID.randomUUID().toString();
            profile.setBearerToken(bearerToken);
            this.mongoDBJsonStore.updateProfile(profile);
            authResponse.add("profile", profile.toJson());
            authResponse.addProperty("bearerToken",profile.getBearerToken());

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

            String bearerToken = UUID.randomUUID().toString();
            profile.setBearerToken(bearerToken);
            this.mongoDBJsonStore.updateProfile(profile);
            jsonObject.addProperty("bearerToken",bearerToken);

            return jsonObject;
        }

        authFailure.addProperty("message", "password_mismatch");
        throw new AuthenticationException(authFailure);
    }
}
