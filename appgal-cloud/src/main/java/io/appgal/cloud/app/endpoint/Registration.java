package io.appgal.cloud.app.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.app.services.AuthenticationException;
import io.appgal.cloud.app.services.ProfileRegistrationService;
import io.appgal.cloud.app.services.ResourceExistsException;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.*;
import io.appgal.cloud.util.JsonUtil;
import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

@Path("registration")
public class Registration {
    private static Logger logger = LoggerFactory.getLogger(Registration.class);

    @Inject
    private Validator validator;

    @Inject
    private ProfileRegistrationService profileRegistrationService;

    @Context
    private HttpServerRequest request;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    @Path("profile")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile(@QueryParam("email") String email)
    {
        try {
            Profile profile = this.profileRegistrationService.getProfile(email);
            if (profile == null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("message", "profile_not_found");
                jsonObject.addProperty("email", email);
                return Response.status(404).entity(jsonObject.toString()).build();
            }
            return Response.ok(profile.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
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
                JsonObject responseJson = new JsonObject();
                JsonArray violationsArray = new JsonArray();
                for(ConstraintViolation violation:violations)
                {
                    violationsArray.add(violation.getMessage());
                }
                responseJson.add("violations", violationsArray);

                JsonUtil.print(this.getClass(),responseJson);

                return Response.status(400).entity(responseJson.toString()).build();
            }

            this.profileRegistrationService.register(profile);

            return Response.ok(profileJson).build();
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

    @Path("org")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerOrg(@RequestBody String json)
    {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            jsonObject.remove("httpsAgent");

            Profile profile = Profile.parse(jsonObject.toString());
            SourceOrg sourceOrg = SourceOrg.parse(jsonObject.toString());
            sourceOrg.addProfile(profile);
            profile.setSourceOrgId(sourceOrg.getOrgId());

            Set<ConstraintViolation<Profile>> violations = validator.validate(profile);
            if(!violations.isEmpty())
            {
                JsonObject responseJson = new JsonObject();
                JsonArray violationsArray = new JsonArray();
                for(ConstraintViolation violation:violations)
                {
                    logger.info("VIOLATION: "+violation.getMessage());
                    violationsArray.add(violation.getMessage());
                }
                responseJson.add("violations", violationsArray);
                return Response.status(400).entity(responseJson.toString()).build();
            }

            this.profileRegistrationService.registerSourceOrg(sourceOrg);

            JsonObject responseJson = new JsonObject();
            responseJson.add("success", sourceOrg.toJson());
            return Response.ok(responseJson.toString()).build();
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
        try {
            JsonObject jsonObject = JsonParser.parseString(credentialsJson).getAsJsonObject();

            if(jsonObject.get("email").isJsonNull() || jsonObject.get("password").isJsonNull())
            {
                JsonObject authFailure = new JsonObject();
                throw new AuthenticationException(authFailure);
            }

            String userAgent = request.getHeader("User-Agent");

            String email = jsonObject.get("email").getAsString();
            String password = jsonObject.get("password").getAsString();

            Profile profile = this.profileRegistrationService.getProfile(email);


            if(profile == null)
            {
                JsonObject profileNotFound = new JsonObject();
                profileNotFound.addProperty("message", "profile_not_found");
                return Response.status(401).entity(profileNotFound.toString()).build();
            }

            JsonObject responseJson;
            if(profile.getProfileType() == ProfileType.FOOD_RUNNER) {
                Location location = Location.parse(credentialsJson);
                responseJson = this.profileRegistrationService.login(userAgent, email, password, location);
            }
            else
            {
                responseJson = this.profileRegistrationService.orgLogin(userAgent, email, password);
            }

            return Response.ok(responseJson.toString()).build();
        }
        catch(AuthenticationException authenticationException)
        {
            logger.error(authenticationException.getMessage(), authenticationException);
            return Response.status(401).entity(authenticationException.toString()).build();
        }
    }

    @Path("newPassword")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response newPassword(@RequestBody String jsonBody)
    {
        try {
            JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();

            String email = json.get("email").getAsString();
            String password = json.get("password").getAsString();

            Profile profile = this.mongoDBJsonStore.getProfile(email);
            profile.setPassword(password);
            this.mongoDBJsonStore.updateProfile(profile);

            JsonObject success = new JsonObject();
            success.addProperty("success",true);
            return Response.ok(success.toString()).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(500).build();
        }
    }
}