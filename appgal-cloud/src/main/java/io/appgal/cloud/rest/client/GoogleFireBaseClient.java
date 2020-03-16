package io.appgal.cloud.rest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.enterprise.context.ApplicationScoped;
import java.net.URI;

@ApplicationScoped
public class GoogleFireBaseClient {
    private static Logger logger = LoggerFactory.getLogger(GoogleFireBaseClient.class);

    public void registerToken(String token)
    {
        try {
            //String registrationUrl = "https://fcm.googleapis.com/v1/appgalMessagingExample/projects.messages";
            String registrationUrl = "https://appgalMessagingExample.firebaseio.com/users/";
            URI requestUri = new URI(registrationUrl);

            RestTemplate restTemplate = new RestTemplate();
            //RequestEntity<String> requestEntity = RequestEntity.post(requestUri).body("{}");
            RequestEntity<Void> requestEntity = RequestEntity.get(requestUri).build();
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

            logger.info("****");
            logger.info(responseEntity.getBody());
            logger.info("****");
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
