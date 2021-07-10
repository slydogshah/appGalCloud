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
import io.appgal.cloud.restclient.TwilioClient;
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
import java.util.UUID;

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

    @Inject
    private TwilioClient twilioClient;

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

            JsonObject json = profile.toJson();

            if(profile.getProfileType() == ProfileType.ORG)
            {
                SourceOrg sourceOrg = this.mongoDBJsonStore.getSourceOrg(profile.getSourceOrgId());
                sourceOrg.deleteProfile(email);
                json.add("orgProfiles",JsonParser.parseString(sourceOrg.getProfiles().toString()).getAsJsonArray());
            }


            return Response.ok(json.toString()).build();
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            JsonObject error = new JsonObject();
            error.addProperty("exception", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @Path("orgs")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response orgs()
    {
        try {
            List<SourceOrg> orgs = this.mongoDBJsonStore.getSourceOrgs();
            return Response.ok(JsonParser.parseString(orgs.toString()).getAsJsonArray().toString()).build();
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

            return Response.ok(sourceOrg.toString()).build();
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

    @Path("staff")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerStaff(@RequestBody String json){
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

            String orgId = jsonObject.get("orgId").getAsString();
            String caller = jsonObject.get("caller").getAsString();
            String email = null;
            if(jsonObject.has("email"))
            {
                email = jsonObject.get("email").getAsString();
            }
            String password = null;
            if(jsonObject.has("password"))
            {
                password = jsonObject.get("password").getAsString();
            }


            SourceOrg sourceOrg = this.mongoDBJsonStore.getSourceOrg(orgId);
            if(sourceOrg == null)
            {
                JsonObject error = new JsonObject();
                error.addProperty("message", "ORG_NOT_FOUND");
                return Response.status(404).entity(error.toString()).build();
            }

            Profile profile = new Profile();
            profile.setEmail(email);
            profile.setPassword(password);
            profile.setMobile(123);
            profile.setSourceOrgId(orgId);
            profile.setLocation(sourceOrg.getLocation());
            profile.setProfileType(ProfileType.ORG);
            profile.setResetPasswordActive(true);

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

            this.profileRegistrationService.registerStaff(orgId, profile);

            sourceOrg = this.mongoDBJsonStore.getSourceOrg(orgId);
            sourceOrg.deleteProfile(caller);
            JsonObject responseJson = profile.toJson();
            responseJson.add("orgProfiles",JsonParser.parseString(sourceOrg.getProfiles().toString()).getAsJsonArray());

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

    @Path("deleteStaff")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteStaff(@RequestBody String jsonBody){
        try {
            JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();

            String caller = json.get("caller").getAsString();
            String orgId = json.get("orgId").getAsString();
            String email = json.get("email").getAsString();

            this.mongoDBJsonStore.deleteProfile(email);

            SourceOrg sourceOrg = this.mongoDBJsonStore.getSourceOrg(orgId);
            sourceOrg.deleteProfile(email);
            this.mongoDBJsonStore.storeSourceOrg(sourceOrg);

            sourceOrg = this.mongoDBJsonStore.getSourceOrg(orgId);
            sourceOrg.deleteProfile(caller);
            JsonArray responseJson = JsonParser.parseString(sourceOrg.getProfiles().toString()).getAsJsonArray();

            return Response.ok(responseJson.toString()).build();
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
            logger.info("******USER_AGENT********");
            logger.info(userAgent);
            boolean appLogin = false;
            if(userAgent.contains("Dart"))
            {
                appLogin = true;
            }

            String email = jsonObject.get("email").getAsString();
            String password = jsonObject.get("password").getAsString();

            Profile profile = this.profileRegistrationService.getProfile(email);


            if(profile == null)
            {
                JsonObject profileNotFound = new JsonObject();
                profileNotFound.addProperty("message", "profile_not_found");
                return Response.status(401).entity(profileNotFound.toString()).build();
            }

            JsonObject responseJson = null;
            if(profile.getProfileType() == ProfileType.FOOD_RUNNER) {
                if(appLogin) {
                    Location location = Location.parse(credentialsJson);
                    responseJson = this.profileRegistrationService.login(userAgent, email, password, location);
                }
                else
                {
                    JsonObject forbidden = new JsonObject();
                    forbidden.addProperty("message", "access_denied");
                    return Response.status(403).entity(forbidden.toString()).build();
                }
            }
            else
            {
                if(!appLogin) {
                    responseJson = this.profileRegistrationService.orgLogin(userAgent, email, password);
                }
                else
                {
                    JsonObject forbidden = new JsonObject();
                    forbidden.addProperty("message", "access_denied");
                    return Response.status(403).entity(forbidden.toString()).build();
                }
            }

            return Response.ok(responseJson.toString()).build();
        }
        catch(AuthenticationException authenticationException)
        {
            logger.error(authenticationException.getMessage(), authenticationException);
            return Response.status(401).entity(authenticationException.toString()).build();
        }
    }

    @Path("sendResetCode")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendResetCode(@RequestBody String jsonBody)
    {
        try {
            JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();

            String email = json.get("email").getAsString();
            String mobileNumber = json.get("mobileNumber").getAsString();

            Profile profile = this.mongoDBJsonStore.getProfile(email);
            if(profile == null || profile.getProfileType() == ProfileType.FOOD_RUNNER)
            {
                JsonObject error = new JsonObject();
                if(profile == null) {
                    error.addProperty("message", "EMAIL_NOT_FOUND");
                    return Response.status(404).entity(error.toString()).build();
                }
                else
                {
                    error.addProperty("message", "ACCESS_DENIED_FOR_PROFILE_TYPE");
                    return Response.status(403).entity(error.toString()).build();
                }
            }

            String resetCode = UUID.randomUUID().toString().substring(0,6);
            //logger.info("RESET_CODE: "+resetCode);

            profile.setResetCode(resetCode);
            this.mongoDBJsonStore.updateProfile(profile);

            this.twilioClient.sendResetCode(mobileNumber,resetCode);

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

    @Path("verifyResetCode")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyResetCode(@RequestBody String jsonBody)
    {
        try {
            JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();

            String email = json.get("email").getAsString();
            String resetCode = json.get("resetCode").getAsString();

            Profile profile = this.mongoDBJsonStore.getProfile(email);
            JsonObject error = new JsonObject();
            if(profile == null || profile.getProfileType() == ProfileType.FOOD_RUNNER)
            {
                if(profile == null) {
                    error.addProperty("message", "EMAIL_NOT_FOUND");
                    return Response.status(404).entity(error.toString()).build();
                }
                else
                {
                    error.addProperty("message", "ACCESS_DENIED_FOR_PROFILE_TYPE");
                    return Response.status(403).entity(error.toString()).build();
                }
            }

            if(!profile.getResetCode().equals(resetCode))
            {
                error.addProperty("message", "INVALID_RESET_CODE");
                return Response.status(401).entity(error.toString()).build();
            }

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

    //TODO: Validation Hardening
    @Path("newPassword")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response newPassword(@RequestBody String jsonBody)
    {
        return this.resetPassword(jsonBody);
    }

    @Path("resetPassword")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(@RequestBody String jsonBody)
    {
        try {
            JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();

            String email = json.get("email").getAsString();
            String newPassword = json.get("newPassword").getAsString();
            String confirmPassword = json.get("confirmNewPassword").getAsString();

            if(!newPassword.equals(confirmPassword))
            {
                JsonObject error = new JsonObject();
                error.addProperty("message", "PASSWORDS_DONT_MATCH");
                return Response.status(400).entity(error.toString()).build();
            }

            Profile profile = this.mongoDBJsonStore.getProfile(email);
            profile.setPassword(newPassword);
            profile.setResetPasswordActive(false);
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