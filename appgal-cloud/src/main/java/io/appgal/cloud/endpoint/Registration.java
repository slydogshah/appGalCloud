package io.appgal.cloud.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.services.ProfileRegistrationService;
import io.appgal.cloud.model.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("registration")
public class Registration {
    private static Logger logger = LoggerFactory.getLogger(Registration.class);

    @Inject
    private ProfileRegistrationService profileRegistrationService;

    @Path("profile")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getProfile(@QueryParam("email") String email)
    {
        JsonObject profile = this.profileRegistrationService.getProfile(email);
        return profile.toString();
    }

    @Path("profile")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String register(@RequestBody String profileJson)
    {
        JsonObject jsonObject = JsonParser.parseString(profileJson).getAsJsonObject();
        Profile profile = Profile.parse(jsonObject.toString());
        this.profileRegistrationService.register(profile);

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("statusCode", "0");
        return responseJson.toString();
    }

    @Path("login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String login(@RequestBody String credentialsJson)
    {
        JsonObject jsonObject = JsonParser.parseString(credentialsJson).getAsJsonObject();

        String email = jsonObject.get("email").getAsString();
        String password = jsonObject.get("password").getAsString();

        return this.profileRegistrationService.login(email, password).toString();
    }
}