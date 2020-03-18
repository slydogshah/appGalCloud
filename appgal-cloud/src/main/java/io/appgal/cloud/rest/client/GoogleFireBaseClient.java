package io.appgal.cloud.rest.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.enterprise.context.ApplicationScoped;
import java.net.URI;
import java.net.URISyntaxException;

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

            String registrationUrl = "https://fcm.googleapis.com/fcm/send";
            //String registrationUrl = "https://fcm.googleapis.com/v1/projects/appgalMessagingExample/messages:send";
            URI requestUri = new URI(registrationUrl);

            RestTemplate restTemplate = new RestTemplate();
            RequestEntity<String> requestEntity = RequestEntity.post(requestUri)
                    .header("Authorization",
                            "key=AIzaSyAF5tpmmfkXGleTewkzy1ji7MTlCciOnfY")
                    .header("Content-Type","application/json")
                    .body(body);
            //RequestEntity<Void> requestEntity = RequestEntity.get(requestUri).build();
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

            logger.info("**RESPONSE**");
            logger.info(responseEntity.toString());
            logger.info("****");
        }
        catch(HttpClientErrorException e)
        {
            //logger.error(e.getMessage(), e);
            //throw new RuntimeException(e);

            logger.info("....");
            logger.info(e.getResponseBodyAsString());
            logger.info("....");
        }
        catch(URISyntaxException e)
        {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
