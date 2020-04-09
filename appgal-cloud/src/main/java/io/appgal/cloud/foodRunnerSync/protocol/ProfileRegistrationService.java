package io.appgal.cloud.foodRunnerSync.protocol;

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
}
