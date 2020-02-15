package io.appgal.cloud.persistence;

import com.google.gson.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MongoDBJsonStore {
    private static Logger logger = LoggerFactory.getLogger(MongoDBJsonStore.class);

    @PostConstruct
    public void start()
    {

    }

    @PreDestroy
    public void stop()
    {

    }

    public JsonArray findDestinationNotifications()
    {
        JsonArray destinationNotifications = new JsonArray();

        return destinationNotifications;
    }
}
