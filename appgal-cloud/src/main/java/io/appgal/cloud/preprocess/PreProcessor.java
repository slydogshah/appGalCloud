package io.appgal.cloud.preprocess;

import io.appgal.cloud.app.services.ProfileRegistrationService;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.Address;
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
    private MongoDBJsonStore mongoDBJsonStore;

    @Inject
    private ProfileRegistrationService profileRegistrationService;

    @ConfigProperty(name = "admin")
    private String admin;

    @ConfigProperty(name = "password")
    private String password;

    @Override
    public void filter(ContainerRequestContext context) {
        if(this.mongoDBJsonStore.getSourceOrg("church") == null) {
            try
            {
                SourceOrg dropOffOrg = new SourceOrg();
                dropOffOrg.setOrgId("church");
                dropOffOrg.setOrgName("church");
                dropOffOrg.setProducer(false);
                dropOffOrg.setOrgContactEmail("church@gmail.com");
                Address address = new Address();
                address.setStreet("801 West Fifth Street");
                address.setZip("78703");
                dropOffOrg.setAddress(address);
                this.profileRegistrationService.registerSourceOrg(dropOffOrg);
            }
            catch (Exception e)
            {
            }
        }
    }
}
