package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.*;

public class ConsumerTask extends RecursiveTask<JsonArray> {
    private static Logger logger = LoggerFactory.getLogger(ConsumerTask.class);

    private String topic;
    private MessageWindow messageWindow;
    private KafkaConsumer<String,String> kafkaConsumer;
    private ExecutorService executorService;
    private Queue<NotificationContext> readNotificationsQueue;
    private ForkJoinPool commonPool;

    public ConsumerTask(String topic, MessageWindow messageWindow,
                        Queue<NotificationContext> readNotificationsQueue,ForkJoinPool commonPool) {
        this.topic = topic;
        this.messageWindow = messageWindow;
        this.executorService = Executors.newCachedThreadPool();
        this.readNotificationsQueue = readNotificationsQueue;
        this.commonPool = commonPool;
    }

    @Override
    protected JsonArray compute() {
        try {
            JsonArray messages = new JsonArray();
            Future future = this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    //TODO: make this a synchronized write
                    NotificationContext notificationContext = new NotificationContext(topic, messageWindow);
                    readNotificationsQueue.add(notificationContext);

                    ConsumerSubTask subTask = new ConsumerSubTask(messageWindow);
                    commonPool.execute(subTask);
                    subTask.join();
                }
            });
            future.get();

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
