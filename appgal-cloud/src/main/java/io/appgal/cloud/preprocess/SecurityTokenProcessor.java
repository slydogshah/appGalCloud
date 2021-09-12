package io.appgal.cloud.preprocess;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.infrastructure.MongoDBJsonStore;
import io.appgal.cloud.model.Profile;
import io.appgal.cloud.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Priority(0)
@Provider
public class SecurityTokenProcessor implements ContainerResponseFilter
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
    public void filter(ContainerRequestContext context, ContainerResponseContext containerResponseContext) throws IOException
    {
        if(this.isDeactivated()){
            return;
        }

        String bearerToken = context.getHeaderString("Bearer");
        if(bearerToken != null)
        {
            //logger.info("*****************************************************************");
            //logger.info("(SecurityToken): " + this.securityTokenContainer.getSecurityToken().getToken());
            //logger.info("*****************************************************************");

            JsonObject json = new JsonObject();
            json.addProperty("access_token", bearerToken);
            SecurityToken securityToken = SecurityToken.fromJson(json.toString());
            this.securityTokenContainer.setSecurityToken(securityToken);

            String path = context.getUriInfo().getRequestUri().getPath();
            if(!path.startsWith("/registration/login") && !path.startsWith("/registration/org"))
            {
                boolean verification = this.verifyBearerToken(context,bearerToken);
                if(!verification){
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("message", "403: Access Denied");
                    containerResponseContext.setEntity(jsonObject.toString());
                    containerResponseContext.setStatus(403);
                    return;
                }
            }
        }
        else {
            //System.out.println("PATH: "+path);
            //System.out.println("METHOD: "+method);
            //System.out.println(this.whiteList);

            String path = context.getUriInfo().getRequestUri().getPath();
            String method = context.getMethod();

            if(path.startsWith("/registration/profile") && method.equalsIgnoreCase("post")){
                //This is FoodRunner registration
                this.whiteList.add("/registration/profile");
            }

            if(!this.isPathWhiteListed(path)) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("message", "403: Access Denied");
                containerResponseContext.setEntity(jsonObject.toString());
                containerResponseContext.setStatus(403);
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
        System.out.println("***************");
        System.out.println("Path: "+context.getUriInfo().getRequestUri().getPath());
        System.out.println("Principal: "+principal);
        JsonUtil.print(this.getClass(),profile.toJson());
        if(profile.getBearerToken() != null && profile.getBearerToken().equals(bearerToken)){
            System.out.println("ACCESS_GRANTED");
            return true;
        }
        return false;
    }
}
