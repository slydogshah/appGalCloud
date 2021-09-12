package io.appgal.cloud.preprocess;

import com.google.gson.JsonObject;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Provider
public class SecurityTokenProcessor implements ContainerRequestFilter
{
    private static Logger logger = LoggerFactory.getLogger(SecurityTokenProcessor.class);

    @Inject
    private SecurityTokenContainer securityTokenContainer;

    @Inject
    private MongoDBJsonStore mongoDBJsonStore;

    private List<String> whiteList = new ArrayList<>();

    private boolean active;

    @PostConstruct
    public void onStart(){
        this.active = true;
        whiteList.add("/registration/login");
        whiteList.add("/registration/timezones");
        whiteList.add("/registration/org");
        whiteList.add("/registration/sendResetCode");
        whiteList.add("/registration/verifyResetCode");
        whiteList.add("/registration/resetPassword");
    }

    public void deactivate(){
        this.active = false;
    }

    private boolean isDeactivated()
    {
        return !this.active;
    }

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        String path = context.getUriInfo().getRequestUri().getPath();
        String method = context.getMethod();

        if(this.isDeactivated()){
            return;
        }

        System.out.println("******INVOKED********");
        String principal = context.getHeaderString("Principal");
        String bearerToken = context.getHeaderString("Bearer");
        System.out.println("******FILTER*********: "+Thread.currentThread());
        System.out.println("Path: "+context.getUriInfo().getRequestUri().getPath());
        System.out.println("Method: "+context.getMethod());
        System.out.println("Principal: "+principal);
        System.out.println("BearerToken: "+bearerToken);

        if(bearerToken != null)
        {
            //logger.info("*****************************************************************");
            //logger.info("(SecurityToken): " + this.securityTokenContainer.getSecurityToken().getToken());
            //logger.info("*****************************************************************");

            JsonObject json = new JsonObject();
            json.addProperty("access_token", bearerToken);
            SecurityToken securityToken = SecurityToken.fromJson(json.toString());
            this.securityTokenContainer.setSecurityToken(securityToken);

            if(!path.startsWith("/registration/login") && !path.startsWith("/registration/org"))
            {
                boolean verification = this.verifyBearerToken(context,bearerToken);
                if(!verification){
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("message", "403: Access Denied");
                    Response response = Response.status(403).entity(jsonObject.toString()).build();
                    context.abortWith(response);
                    return;
                }
            }
        }
        else {
            System.out.println("PATH: "+path);
            System.out.println("METHOD: "+method);
            System.out.println(this.whiteList);

            if(path.startsWith("/registration/profile") && method.equalsIgnoreCase("post")){
                //This is FoodRunner registration
                this.whiteList.add("/registration/profile");
            }

            boolean isPathWhiteListed = this.isPathWhiteListed(path);
            System.out.println("IS_PATH_WHITE_LISTED: "+isPathWhiteListed);
            if(!isPathWhiteListed) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("message", "403: Access Denied");
                Response response = Response.status(403).entity(jsonObject.toString()).build();
                context.abortWith(response);
                return;
            }
        }
    }

    private boolean isPathWhiteListed(String path){
        for(String whiteListedPath:this.whiteList){
            if(path.startsWith(whiteListedPath)){
                return true;
            }
        }
        return false;
    }

    private boolean verifyBearerToken(ContainerRequestContext context,String bearerToken){
        String principal = context.getHeaderString("Principal");
        Profile profile = this.mongoDBJsonStore.getProfile(principal);
        System.out.println("******VERIFY*********");
        System.out.println("Path: "+context.getUriInfo().getRequestUri().getPath());
        System.out.println("Method: "+context.getMethod());
        System.out.println("Principal: "+principal);
        System.out.println("BearerToken: "+bearerToken);
        JsonUtil.print(this.getClass(),profile.toJson());
        if(profile.getBearerToken() != null && profile.getBearerToken().equals(bearerToken)){
            System.out.println("ACCESS_GRANTED");
            return true;
        }
        return false;
    }
}
