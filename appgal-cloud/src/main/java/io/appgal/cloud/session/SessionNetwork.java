package io.appgal.cloud.session;

import io.appgal.cloud.model.SourceNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@ApplicationScoped
public class SessionNetwork {
    private static Logger logger = LoggerFactory.getLogger(SessionNetwork.class);

    @Inject
    private FoodRunnerSession foodRunnerSession;

    private TreeMap<String, FoodRunnerSession> foodRunnerSessions;

    public SessionNetwork()
    {
        this.foodRunnerSessions = new TreeMap<>();
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
}
