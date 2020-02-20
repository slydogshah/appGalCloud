package io.appgal.cloud.session;

import io.appgal.cloud.model.SourceNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FoodRunnerSession {
    private static Logger logger = LoggerFactory.getLogger(FoodRunnerSession.class);

    private String foodRunnerId;
    private Map<String, List<SourceNotification>> sourceNotifications;

    public FoodRunnerSession()
    {
        this.sourceNotifications = new HashMap<>();
    }

    @PostConstruct
    public void start()
    {

    }
}
