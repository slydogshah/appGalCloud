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

public class ConsumerSubTask extends RecursiveTask<JsonArray> {
    private static Logger logger = LoggerFactory.getLogger(ConsumerSubTask.class);

    private MessageWindow messageWindow;

    public ConsumerSubTask(MessageWindow messageWindow) {
        this.messageWindow = messageWindow;
    }

    @Override
    protected JsonArray compute() {

        /*while(messageWindow.getMessages() == null)
        {
            logger.info("WAITING_ON_DATA");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }*/

        return messageWindow.getMessages();
    }
}
