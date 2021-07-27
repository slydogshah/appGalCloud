package io.appgal.cloud.infrastructure;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.appgal.cloud.model.ScheduleNotification;
import io.appgal.cloud.model.SchedulePickUpNotification;
import io.appgal.cloud.network.services.NetworkOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

@Singleton
public class PushNotificationDaemon {
    private static Logger logger = LoggerFactory.getLogger(PushNotificationDaemon.class);

    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @PostConstruct
    public void onStart(){
        this.threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "PushNotificationDaemon");
        this.threadPoolTaskScheduler.initialize();
    }


    public void sendNotifications(SchedulePickUpNotification pickUpNotification){
        try {
            this.threadPoolTaskScheduler.schedule(
                    new RunnableTask(this.networkOrchestrator,pickUpNotification),
                    Date.from(pickUpNotification.getStartTimeInEpoch())
            );
        }catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
