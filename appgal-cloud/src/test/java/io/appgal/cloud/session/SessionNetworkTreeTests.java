package io.appgal.cloud.session;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeMap;

public class SessionNetworkTreeTests {
    private static Logger logger = LoggerFactory.getLogger(SessionNetworkTreeTests.class);

    @Test
    public void testSessionNetworkLookup()
    {
        SessionNetwork sessionNetwork = new SessionNetwork();

        TreeMap<String, FoodRunnerSession> foodRunnerSessions = sessionNetwork.getFoodRunnerSessions();

        //Time to assert
    }
}
