package io.appgal.cloud.rest.client;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;


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
import javax.ws.rs.client.Client;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.*;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.oauth2.sdk.http.*;
import com.nimbusds.oauth2.sdk.id.*;
import com.nimbusds.oauth2.sdk.token.*;
import org.springframework.web.util.UriComponentsBuilder;

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

    public void checkNimbusOAuthCredentialsGrant() throws Exception
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

    public void checkNimbusOAuthCodeGrant() throws Exception
    {
        // Construct the code grant from the code obtained from the authz endpoint
        // and the original callback URI used at the authz endpoint
        AuthorizationCode code = new AuthorizationCode("9IQLWG");
        URI callback = new URI("https://accounts.google.com/o/oauth2/auth");
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(code, callback);
        final Map<String, List<String>> parameters = codeGrant.toParameters();
        Set<String> keys = parameters.keySet();
        for(String key:keys)
        {
            logger.info(key+":"+parameters.get(key).get(0));
        }


        // The credentials to authenticate the client at the token endpoint
        ClientID clientID = new ClientID("107945977384061736446");
        Secret clientSecret = new Secret("18be8094aeb23b35702d70d804513b7acb5caef7");
        ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);

        // The token endpoint
        URI tokenEndpoint = new URI("https://oauth2.googleapis.com/token");

        // Make the token request
        TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, codeGrant);

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

    public void nimbusImpliciFlow() throws Exception
    {
        // The authorisation endpoint of the server
        URI authzEndpoint = new URI("https://oauth2.googleapis.com/token");

        // The client identifier provisioned by the server
                ClientID clientID = new ClientID("107945977384061736446");

        // The requested scope values for the token
                Scope scope = new Scope("read", "write");

        // The client callback URI, typically pre-registered with the server
                URI callback = new URI("https://accounts.google.com/o/oauth2/auth");

        // Generate random state string for pairing the response to the request
                State state = new State();

        // Build the request
                AuthorizationRequest request = new AuthorizationRequest.Builder(
                        new ResponseType(ResponseType.Value.TOKEN), clientID)
                        .scope(scope)
                        .state(state)
                        .redirectionURI(callback)
                        .endpointURI(authzEndpoint)
                        .build();

        // Use this URI to send the end-user's browser to the server
        URI requestURI = request.toURI();

        // Parse the authorisation response from the callback URI
        AuthorizationResponse response = AuthorizationResponse.parse(requestURI);

        logger.info("**RESPONSE**");
        logger.info(response.toString());
        logger.info("****");

        if (! response.indicatesSuccess()) {
            // The request was denied or some error may have occurred
        }


        AuthorizationSuccessResponse successResponse = (AuthorizationSuccessResponse)response;

        // The returned state parameter must match the one send with the request
                if (! state.equals(successResponse.getState())) {
                // Unexpected or tampered response, stop!!!
            }


        // Retrieve the token, which can now be used to make requests to the
        // protected resource (web API)
        AccessToken token = successResponse.getAccessToken();

                logger.info("****");
                System.out.println(token);
                logger.info("****");
    }

    public void checkGoogleOAuthCodeGrant() throws Exception
    {
        try {
            List<String> scopes = new ArrayList<>();
            scopes.add("read");

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
                    "107945977384061736446", "18be8094aeb23b35702d70d804513b7acb5caef7", scopes)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(".")))
                    .build();

            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            GoogleAuthorizationCodeFlow flow2 = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
                    jsonFactory, "107945977384061736446", "18be8094aeb23b35702d70d804513b7acb5caef7",
                    scopes).setAccessType("offline").setApprovalPrompt("auto").build();
            String url = flow2.newAuthorizationUrl().setRedirectUri("https://appgalfoodrunnerapp.firebaseapp.com/__/auth/handler").build();
            logger.info("URL: "+url);

            String authorizationCode = "blah";
            final GoogleAuthorizationCodeTokenRequest googleAuthorizationCodeTokenRequest = flow.newTokenRequest(authorizationCode);
            final GoogleTokenResponse tokenResponse = googleAuthorizationCodeTokenRequest.execute();
            logger.info("....");
            logger.info(tokenResponse.getAccessToken());
            logger.info("....");
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);

            if(e instanceof TokenResponseException)
            {
                TokenResponseException tokenResponseException = (TokenResponseException) e;
                logger.info("*************");
                logger.info("RedirectUri: "+tokenResponseException.getMessage());
                logger.info("*************");
            }

            throw new RuntimeException(e);
        }
    }

    public void getOAuthToken() throws Exception
    {
        try {
            String authenticationUrl = "https://accounts.google.com/o/oauth2/v2/auth";
            //String redirectUri = "https://accounts.google.com/o/oauth2/auth";
            String redirectUri = "https://appgalfoodrunnerapp.firebaseapp.com/__/auth/handler";
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(authenticationUrl)
                    .queryParam("response_type", "code")
                    .queryParam("client_id", "107945977384061736446")
                    .queryParam("scope", "openid")
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("state", new BigInteger(130, new SecureRandom()).toString(32))
                    .queryParam("nonce", UUID.randomUUID().toString());

            RestTemplate restTemplate = new RestTemplate();
            RequestEntity<Void> get = RequestEntity.get(new URI(builder.toUriString())).build();
            final ResponseEntity<String> responseEntity = restTemplate.exchange(get, String.class);

            logger.info("*******");
            logger.info(responseEntity.getBody());
            logger.info("*******");
        }
        catch(HttpClientErrorException e)
        {
            logger.error(e.getMessage(), e);

            logger.info("*************");
            logger.info(e.getResponseBodyAsString());
            logger.info("*************");

            throw new RuntimeException(e);
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
