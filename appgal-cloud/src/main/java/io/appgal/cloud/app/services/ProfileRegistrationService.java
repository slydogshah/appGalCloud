package io.appgal.cloud.app.services;

import com.google.gson.*;

import io.appgal.cloud.model.*;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;

import io.appgal.cloud.network.services.NetworkOrchestrator;
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
        SourceOrg exists = this.mongoDBJsonStore.getSourceOrg(sourceOrgId);
        if(exists != null)
        {
            JsonObject message = new JsonObject();
            message.addProperty("sourceOrgId",sourceOrgId);
            throw new ResourceExistsException(message.toString());
        }

        this.mongoDBJsonStore.storeSourceOrg(sourceOrg);
    }

    public JsonObject login(String userAgent, String email, String password)
            throws AuthenticationException
    {
        //logger.info("*****LOGIN_USER_AGENT******");
        //logger.info("USER_AGENT: "+userAgent);
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

        authFailure.addProperty("message", "password_mismatch");
        throw new AuthenticationException(authFailure);
    }

    public JsonArray orgLogin(String userAgent, String email, String password) throws AuthenticationException
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
            final JsonObject activeView = this.networkOrchestrator.getActiveView();
            JsonArray activeProfiles = new JsonArray();

            JsonArray activeFoodRunners = activeView.getAsJsonArray("activeFoodRunners");
            Iterator<JsonElement> itr = activeFoodRunners.iterator();
            while(itr.hasNext())
            {
                JsonObject cour = itr.next().getAsJsonObject();
                Profile courProfile = Profile.parse(cour.get("profile").getAsJsonObject().toString());
                activeProfiles.add(courProfile.toJson());
            }

            return activeProfiles;
        }

        authFailure.addProperty("message", "password_mismatch");
        throw new AuthenticationException(authFailure);
    }
}
