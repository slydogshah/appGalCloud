package io.appgal.cloud;

import com.google.gson.JsonObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

import io.appgal.cloud.app.services.ProfileRegistrationService;
import io.appgal.cloud.app.services.ResourceExistsException;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.ProfileType;
import io.bugsbunny.data.history.service.DataReplayService;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

@Path("/microservice")
public class Microservice {
    private static Logger logger = LoggerFactory.getLogger(Microservice.class);

    @Inject
    private DataReplayService dataReplayService;

    @Inject
    private ProfileRegistrationService profileRegistrationService;

    @ConfigProperty(name = "admin")
    private String admin;

    @ConfigProperty(name = "password")
    private String password;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response hello()
    {
        try {
            Profile profile = new Profile();
            profile.setEmail(this.admin);
            profile.setPassword(this.password);
            profile.setMobile(123);
            profile.setProfileType(ProfileType.FOOD_RUNNER);
            this.profileRegistrationService.register(profile);

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("product", "#Jen Network");
            jsonObject.addProperty("oid", UUID.randomUUID().toString());
            jsonObject.addProperty("message", "HELLO_TO_HUMANITY");

            return Response.ok(jsonObject.toString()).build();
        }
        catch(ResourceExistsException rxe)
        {
            logger.error(rxe.getMessage());
            return Response.status(409).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(500).build();
        }
    }
}