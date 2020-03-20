package io.appgal.cloud.rest.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.oauth2.sdk.http.*;
import com.nimbusds.oauth2.sdk.id.*;
import com.nimbusds.oauth2.sdk.token.*;

@ApplicationScoped
public class GoogleFireBaseClient {
    private static Logger logger = LoggerFactory.getLogger(GoogleFireBaseClient.class);

    public void registerToken(String token)
    {
        try {
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            jsonArray.add("f21wRZI7lDQ:APA91bGWYODRf8vuJIq73cA5E7O5n5rZ8V4HSCsmrhU2iabKoEt_K4bSNYGwHaGVGMdjZllxmI7Qwvar_tnE2c9-b7lWsZRPQxMDuMSFeuKQhaNkRMC8k6ygBefyAJ4xApYwwZWaqnR0");
            jsonObject.add("registration_ids", jsonArray);

            jsonObject.addProperty("title", "testTitle");
            jsonObject.addProperty("body", "testBody");
            jsonObject.addProperty("topic", "weather");

            String body = jsonObject.toString();
            logger.info("....");
            logger.info(body);
            logger.info("....");

            //String registrationUrl = "https://fcm.googleapis.com/fcm/send";
            String registrationUrl = "https://fcm.googleapis.com/v1/projects/appgalfoodrunnerapp/messages:send";
            URI requestUri = new URI(registrationUrl);

            final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            final HttpClient httpClient = HttpClientBuilder.create()
                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .build();
            factory.setHttpClient(httpClient);

            OAuth2RestTemplate restTemplate = this.oauth2RestTemplate();
            restTemplate.setRequestFactory(factory);

            RequestEntity<String> requestEntity = RequestEntity.post(requestUri)
                    .header("Content-Type","application/json")
                    .body("{}");
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

            logger.info("**RESPONSE**");
            logger.info(responseEntity.toString());
            logger.info("****");
        }
        catch(URISyntaxException e)
        {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);

            if(e instanceof UserRedirectRequiredException)
            {
                UserRedirectRequiredException redirectException = (UserRedirectRequiredException) e;
                logger.info("*************");
                logger.info("RedirectUri: "+redirectException.getRedirectUri());
                logger.info("Status Code: "+redirectException.getStateKey());
                logger.info("*************");
            }

            throw new RuntimeException(e);
        }
    }

    private OAuth2RestTemplate oauth2RestTemplate()
    {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setAccessTokenUri("https://oauth2.googleapis.com/token");
        details.setUserAuthorizationUri("https://accounts.google.com/o/oauth2/auth");
        details.setClientId("107945977384061736446");
        details.setClientSecret("18be8094aeb23b35702d70d804513b7acb5caef7");


        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(details);
        return oAuth2RestTemplate;
    }

    public void checkNimbusOAuth2Flow() throws Exception
    {
        // Construct the client credentials grant
        AuthorizationGrant clientGrant = new ClientCredentialsGrant();

        // The credentials to authenticate the client at the token endpoint
        ClientID clientID = new ClientID("107945977384061736446");
        Secret clientSecret = new Secret("18be8094aeb23b35702d70d804513b7acb5caef7");
        ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);

        // The request scope for the token (may be optional)
        Scope scope = new Scope("read", "write");

        // The token endpoint
        URI tokenEndpoint = new URI("https://oauth2.googleapis.com/token");

        // Make the token request
        TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, clientGrant, scope);

        TokenErrorResponse errorResponse = null;
        TokenResponse successResponse = null;
        Object response = TokenResponse.parse(request.toHTTPRequest().send());
        if(response instanceof TokenErrorResponse)
        {
            errorResponse = (TokenErrorResponse) response;
            logger.info("....");
            logger.info(errorResponse.toJSONObject().toJSONString());
            logger.info("....");
        }
        else
        {
            successResponse = (TokenResponse) response;

            AccessTokenResponse tokenSuccessResponse = successResponse.toSuccessResponse();

            // Get the access token
            AccessToken accessToken = tokenSuccessResponse.getTokens().getAccessToken();
        }
    }
}









/*private class AppGalAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

        PasswordEncoder passwordEncoder() {
            return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }

        public void configure(ClientDetailsServiceConfigurer clients) {
            try {
                clients
                        .inMemory()
                        .withClient("first-client")
                        .secret(passwordEncoder().encode("noonewilleverguess"))
                        .scopes("resource:read")
                        .authorizedGrantTypes("authorization_code")
                        .redirectUris("http://localhost:8081/oauth/login/client-app");
            }
            catch(Exception e){}
        }
}*/
