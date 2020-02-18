package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class ConsumerTask extends RecursiveTask<JsonArray> {
    private static Logger logger = LoggerFactory.getLogger(ConsumerTask.class);

    private String topic;
    private MessageWindow messageWindow;
    private KafkaConsumer<String,String> kafkaConsumer;
    private ExecutorService executorService;
    private Queue<NotificationContext> readNotificationsQueue;

    public ConsumerTask(String topic, MessageWindow messageWindow,KafkaConsumer<String,String> kafkaConsumer,
                        ExecutorService executorService,Queue<NotificationContext> readNotificationsQueue) {
        this.topic = topic;
        this.messageWindow = messageWindow;
        this.kafkaConsumer = kafkaConsumer;
        this.executorService = executorService;
        this.readNotificationsQueue = readNotificationsQueue;
    }

    @Override
    protected JsonArray compute() {
        try {
            Future future = this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    //TODO: make this a synchronized write
                    NotificationContext notificationContext = new NotificationContext(topic, messageWindow);
                    readNotificationsQueue.add(notificationContext);
                }
            });
            future.get();

            JsonArray messages = new JsonArray();
            if (messageWindow.getMessages() != null) {
                messages = messageWindow.getMessages();
            }

            return messages;
        }
        catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
