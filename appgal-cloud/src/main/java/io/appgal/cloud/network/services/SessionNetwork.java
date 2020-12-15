package io.appgal.cloud.network.services;

import io.appgal.cloud.model.MessageWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.TreeMap;

@ApplicationScoped
public class SessionNetwork  {
    private static Logger logger = LoggerFactory.getLogger(SessionNetwork.class);

    @Inject
    private FoodRunnerSession foodRunnerSession;

    private TreeMap<String, FoodRunnerSession> foodRunnerSessions; //foodRunnerSessionId -> foodRunnerSession

    public SessionNetwork()
    {
        this.foodRunnerSessions = new TreeMap<>();
    }

    TreeMap<String, FoodRunnerSession> getFoodRunnerSessions() {
        return foodRunnerSessions;
    }

    @PostConstruct
    public void start()
    {
        logger.info("****");
        logger.info("SESSION_NETWORK_START: (SUCCESS)");
        logger.info("****");
    }

    @PreDestroy
    public void stop()
    {
        logger.info("****");
        logger.info("SESSION_NETWORK_STOP: (SUCCESS)");
        logger.info("****");
    }

    public void receiveNotifications(MessageWindow messageWindow) {
        logger.info("****KAFKA_DAEMON_LISTENER*********");
        if(messageWindow.getMessages() != null) {
            logger.info(messageWindow.getMessages().toString());
        }
        else
        {
            logger.info(messageWindow.toString());
        }
        logger.info("**********************************");

        this.foodRunnerSession.receiveNotifications(messageWindow);
    }
}
