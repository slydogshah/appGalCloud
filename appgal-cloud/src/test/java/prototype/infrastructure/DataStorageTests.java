package prototype.infrastructure;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.bugsbunny.data.history.service.DataReplayService;

import io.bugsbunny.test.components.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.appgal.cloud.model.*;
import io.appgal.cloud.model.MessageWindow;

@QuarkusTest
public class DataStorageTests extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(DataStorageTests.class);

    @Inject
    private DataReplayService dataReplayService;

    //@Test
    public void testAddingSourceNotifications() throws InterruptedException {
        List<String> notificationIds = new ArrayList<>();
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime end = start.plusMinutes(Duration.ofMinutes(10).toMinutes());
        MessageWindow messageWindow = new MessageWindow();
        messageWindow.setStart(start);
        messageWindow.setEnd(end);
        Random random = new Random();
        for(int i=0; i<10; i++)
        {
            String sourceNotificationId = UUID.randomUUID().toString();
            SourceNotification sourceNotification = new SourceNotification();
            sourceNotification.setSourceNotificationId(sourceNotificationId);
            sourceNotification.setMessageWindow(messageWindow);

            notificationIds.add(sourceNotificationId);

            JsonObject jsonObject = JsonParser.parseString(sourceNotification.toString()).getAsJsonObject();

            JsonObject modelChain = new JsonObject();
            modelChain.addProperty("modelId", random.nextLong());
            modelChain.add("payload", jsonObject);
            String oid = this.dataReplayService.generateDiffChain(modelChain);
            logger.info("ChainId: "+oid);

            Response response = given().get("/replay/chain/?oid=" + oid).andReturn();
            logger.info("************************");
            logger.info(response.statusLine());
            response.body().prettyPrint();
            logger.info("************************");
            assertEquals(200, response.getStatusCode());
        }
    }
}
