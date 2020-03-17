package io.appgal.cloud.rest.client;

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
            jsonObject.addProperty("registration_id",
                    "AIzaSyAF5tpmmfkXGleTewkzy1ji7MTlCciOnfY");
            jsonObject.addProperty("to", "cL9pKU-1ikU:APA91bHz3-Gqjkuqu-vXCk7Za7_IRcRxAZl6zwes5-gqS1xnHG4dqggnJRUa0wJOx3x_rC2jKBWh7ka9XJvVKN38yN8t1T5gvYSdYwvUBKcRm3Hsfa8uJo6ntjdDpdDGCDloYkjraCsV");
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
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        catch(URISyntaxException e)
        {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
