package io.appgal.cloud.preprocess;

import io.appgal.cloud.app.services.ProfileRegistrationService;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.ProfileType;

import io.appgal.cloud.model.SourceOrg;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class PreProcessor implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(PreProcessor.class);

    @Inject
    private ProfileRegistrationService profileRegistrationService;

    @ConfigProperty(name = "admin")
    private String admin;

    @ConfigProperty(name = "password")
    private String password;

    @Override
    public void filter(ContainerRequestContext context) {
        if(this.profileRegistrationService.getProfile(this.admin) == null) {
            logger.info("CREATING_ADMIN_PROFILE");
            try
            {
                /*SourceOrg sourceOrg = new SourceOrg();
                sourceOrg.setOrgId("AppGal Labs");
                sourceOrg.setOrgName("AppGal Labs");
                sourceOrg.setProducer(true);
                sourceOrg.setOrgContactEmail(this.admin);
                Profile profile = new Profile();
                profile.setEmail(this.admin);
                profile.setPassword(this.password);
                profile.setMobile(123);
                profile.setProfileType(ProfileType.ORG);
                sourceOrg.addProfile(profile);
                this.profileRegistrationService.registerSourceOrg(sourceOrg);*/


                SourceOrg dropOffOrg = new SourceOrg();
                dropOffOrg.setOrgId("Church");
                dropOffOrg.setOrgName("Church");
                dropOffOrg.setProducer(false);
                dropOffOrg.setOrgContactEmail("church@gmail.com");
                this.profileRegistrationService.registerSourceOrg(dropOffOrg);

            }
            catch (Exception e)
            {
            }
        }
        else
        {
            logger.info("ADMIN_PROFILE_ACTIVE");
        }
    }
}
