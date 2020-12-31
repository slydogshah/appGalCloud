package io.appgal.cloud.app.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.app.services.AuthenticationException;
import io.appgal.cloud.app.services.ProfileRegistrationService;
import io.appgal.cloud.app.services.ResourceExistsException;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.model.SourceOrg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.stream.Collectors;

@Path("registration")
public class Registration {
    private static Logger logger = LoggerFactory.getLogger(Registration.class);

    @Inject
    private Validator validator;

    @Inject
    private ProfileRegistrationService profileRegistrationService;

    @Path("profile")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile(@QueryParam("email") String email)
    {
        Profile profile = this.profileRegistrationService.getProfile(email);
        if(profile == null)
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", "profile_not_found");
            jsonObject.addProperty("email", email);
            return Response.status(404).entity(jsonObject.toString()).build();
        }
        return Response.ok(profile.toString()).build();
    }

    @Path("profile")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@RequestBody String profileJson)
    {
        try {
            JsonObject jsonObject = JsonParser.parseString(profileJson).getAsJsonObject();
            Profile profile = Profile.parse(jsonObject.toString());

            Set<ConstraintViolation<Profile>> violations = validator.validate(profile);
            if(!violations.isEmpty())
            {
                String message = violations.stream()
                        .map(cv -> cv.getMessage())
                        .collect(Collectors.joining(", "));
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("violations", message);
                return Response.status(400).entity(responseJson.toString()).build();
            }

            this.profileRegistrationService.register(profile);

            return Response.ok().build();
        }
        catch(ResourceExistsException rxe)
        {
            logger.error(rxe.getMessage(), rxe);
            return Response.status(409).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(500).build();
        }
    }

    @Path("login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@RequestBody String credentialsJson)
    {
        JsonObject jsonObject = JsonParser.parseString(credentialsJson).getAsJsonObject();

        String email = jsonObject.get("email").getAsString();
        String password = jsonObject.get("password").getAsString();

        try {
            JsonObject result = this.profileRegistrationService.login(email, password);
            String json = result.toString();
            return Response.ok(json).build();
        }
        catch(AuthenticationException authenticationException)
        {
            return Response.status(401).entity(authenticationException.toString()).build();
        }
    }
}