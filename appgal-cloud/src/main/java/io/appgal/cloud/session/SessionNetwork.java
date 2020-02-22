package io.appgal.cloud.session;

import io.appgal.cloud.messaging.KafkaDaemon;
import io.appgal.cloud.messaging.KafkaDaemonListener;
import io.appgal.cloud.messaging.MessageWindow;
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
public class SessionNetwork implements KafkaDaemonListener {
    private static Logger logger = LoggerFactory.getLogger(SessionNetwork.class);

    @Inject
    private FoodRunnerSession foodRunnerSession;

    @Inject
    private KafkaDaemon kafkaDaemon;

    private TreeMap<String, FoodRunnerSession> foodRunnerSessions;

    public SessionNetwork()
    {
        this.foodRunnerSessions = new TreeMap<>();
    }

    @PostConstruct
    public void start()
    {
        this.kafkaDaemon.registerDaemonListener(this);
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

    @Override
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
    }
}
