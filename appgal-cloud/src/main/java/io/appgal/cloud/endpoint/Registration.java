package io.appgal.cloud.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.services.AuthenticationException;
import io.appgal.cloud.services.ProfileRegistrationService;
import io.appgal.cloud.model.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;

@Path("registration")
public class Registration {
    private static Logger logger = LoggerFactory.getLogger(Registration.class);

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
    public Serializable register(@RequestBody String profileJson)
    {
        JsonObject jsonObject = JsonParser.parseString(profileJson).getAsJsonObject();
        Profile profile = Profile.parse(jsonObject.toString());
        this.profileRegistrationService.register(profile);

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("statusCode", 200);
        return responseJson.toString();
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