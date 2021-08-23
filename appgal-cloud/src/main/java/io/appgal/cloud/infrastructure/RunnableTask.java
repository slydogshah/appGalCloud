package io.appgal.cloud.infrastructure;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import io.appgal.cloud.model.FoodRunner;
import io.appgal.cloud.model.SchedulePickUpNotification;
import io.appgal.cloud.model.SourceOrg;
import io.appgal.cloud.network.services.NetworkOrchestrator;

import io.appgal.cloud.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class RunnableTask implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(RunnableTask.class);

    private SchedulePickUpNotification pickUpNotification;
    private NetworkOrchestrator networkOrchestrator;

    private static final String PROJECT_ID = "appgallabs-271922";
    private static final String BASE_URL = "https://fcm.googleapis.com";
    private static final String FCM_SEND_ENDPOINT = "/v1/projects/" + PROJECT_ID + "/messages:send";

    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = { MESSAGING_SCOPE };

    private static final String TITLE = "Pickup Notification";
    public static final String MESSAGE_KEY = "message";

    public RunnableTask(NetworkOrchestrator networkOrchestrator,SchedulePickUpNotification pickUpNotification)
    {
        System.out.println("********1************");
        this.networkOrchestrator = networkOrchestrator;
        this.pickUpNotification = pickUpNotification;
    }

    @Override
    public void run() {
        try {
            System.out.println("************2*********");
            SourceOrg pickupOrg = this.pickUpNotification.getSourceOrg();
            String messageBody = "You have a new pickup request for ["+pickupOrg.getOrgName()+"]";


            logger.info(new Date() + " Runnable Task with " + messageBody
                    + " on thread " + Thread.currentThread().getName());

            System.out.println("*****CALLING*******");
            List<FoodRunner> qualified = this.networkOrchestrator.notifyFoodRunners(this.pickUpNotification);
            System.out.println(qualified);

            for(FoodRunner foodRunner:qualified) {
                this.sendCommonMessage(messageBody,foodRunner);
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
        }

        /*try {
            String messageBody = "You have a new pickup request for [BLAH]";


            logger.info(new Date() + " Runnable Task with " + messageBody
                    + " on thread " + Thread.currentThread().getName());

            String topic = "weather";
            this.sendCommonMessage(messageBody,topic);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
        }*/
    }

    /**
     * Send notification message to FCM for delivery to registered devices.
     *
     * @throws IOException
     */
    public void sendCommonMessage(String message,FoodRunner foodRunner) throws IOException {
        List<JsonObject> messages = buildNotificationMessage(message,foodRunner);

        for(JsonObject cour:messages) {
            prettyPrint(cour);
            sendMessage(cour);
        }
    }

    /**
     * Construct the body of a notification message request.
     *
     * @return JSON of notification message.
     */
    private List<JsonObject> buildNotificationMessage(String message,FoodRunner foodRunner) {
        System.out.println(foodRunner);
        List<JsonObject> messages = new ArrayList<>();

        System.out.println("****GETTING_TOKENS*******");
        Set<String> tokens = foodRunner.getPushTokens();
        System.out.println(tokens);
        for(String token:tokens) {
            JsonObject jNotification = new JsonObject();
            jNotification.addProperty("title", TITLE);
            jNotification.addProperty("body", message);

            JsonObject jMessage = new JsonObject();
            jMessage.add("notification", jNotification);
            jMessage.addProperty("token", token);

            JsonObject jFcm = new JsonObject();
            jFcm.add(MESSAGE_KEY, jMessage);

            messages.add(jFcm);
        }

        System.out.println("******PUSH_MESSAGES*****");
        System.out.println(messages);

        return messages;
    }

    /**
     * Retrieve a valid access token that can be use to authorize requests to the FCM REST
     * API.
     *
     * @return Access token.
     * @throws IOException
     */
    // [START retrieve_access_token]
    private String getAccessToken() throws IOException {
        //File file = new File(Thread.currentThread().getContextClassLoader().getResource("service-account.json").getFile());
        //System.out.println("******SERVICE_ACCOUNT*******");
        //System.out.println(file.getAbsolutePath());
        //InputStream inputStream = new ByteArrayInputStream(new byte[0]);
        System.out.println("******SERVICE_ACCOUNT_TEST_0*******");
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("service-account.json"))
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refresh();
        googleCredentials.refreshAccessToken();
        System.out.println("**********ACCESS_TOKEN_TEST_0******");
        String accessToken = googleCredentials.getAccessToken().getTokenValue();
        System.out.println(accessToken);
        return accessToken;
    }
    // [END retrieve_access_token]

    /**
     * Create HttpURLConnection that can be used for both retrieving and publishing.
     *
     * @return Base HttpURLConnection.
     * @throws IOException
     */
    private HttpURLConnection getConnection() throws IOException {
        // [START use_access_token]
        URL url = new URL(BASE_URL + FCM_SEND_ENDPOINT);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        String accessToken = this.getAccessToken();
        System.out.println("**********ACCESS_TOKEN_TEST1******");
        System.out.println(accessToken);
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
        return httpURLConnection;
        // [END use_access_token]
    }

    /**
     * Send request to FCM message using HTTP.
     * Encoded with UTF-8 and support special characters.
     *
     * @param fcmMessage Body of the HTTP request.
     * @throws IOException
     */
    private void sendMessage(JsonObject fcmMessage) throws IOException {
        JsonUtil.print(this.getClass(), fcmMessage);
        HttpURLConnection connection = getConnection();
        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        writer.write(fcmMessage.toString());
        writer.flush();
        writer.close();

        System.out.println("******SEND_MESSAGE_RESPONSE_0*******");
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            String response = inputstreamToString(connection.getInputStream());
            System.out.print(response);
        } else {
            String response = inputstreamToString(connection.getErrorStream());
            System.out.print(response);
        }
    }

    /**
     * Build the body of an FCM request. This body defines the common notification object
     * as well as platform specific customizations using the android and apns objects.
     *
     * @return JSON representation of the FCM request body.
     */
    /*private JsonObject buildOverrideMessage() {
        JsonObject jNotificationMessage = buildNotificationMessage();

        JsonObject messagePayload = jNotificationMessage.get(MESSAGE_KEY).getAsJsonObject();
        messagePayload.add("android", buildAndroidOverridePayload());

        JsonObject apnsPayload = new JsonObject();
        apnsPayload.add("headers", buildApnsHeadersOverridePayload());
        apnsPayload.add("payload", buildApsOverridePayload());

        messagePayload.add("apns", apnsPayload);

        jNotificationMessage.add(MESSAGE_KEY, messagePayload);

        return jNotificationMessage;
    }*/

    /**
     * Build the android payload that will customize how a message is received on Android.
     *
     * @return android payload of an FCM request.
     */
    private JsonObject buildAndroidOverridePayload() {
        JsonObject androidNotification = new JsonObject();
        androidNotification.addProperty("click_action", "android.intent.action.MAIN");

        JsonObject androidNotificationPayload = new JsonObject();
        androidNotificationPayload.add("notification", androidNotification);

        return androidNotificationPayload;
    }

    /**
     * Build the apns payload that will customize how a message is received on iOS.
     *
     * @return apns payload of an FCM request.
     */
    private JsonObject buildApnsHeadersOverridePayload() {
        JsonObject apnsHeaders = new JsonObject();
        apnsHeaders.addProperty("apns-priority", "10");

        return apnsHeaders;
    }

    /**
     * Build aps payload that will add a badge field to the message being sent to
     * iOS devices.
     *
     * @return JSON object with aps payload defined.
     */
    private JsonObject buildApsOverridePayload() {
        JsonObject badgePayload = new JsonObject();
        badgePayload.addProperty("badge", 1);

        JsonObject apsPayload = new JsonObject();
        apsPayload.add("aps", badgePayload);

        return apsPayload;
    }

    /**
     * Read contents of InputStream into String.
     *
     * @param inputStream InputStream to read.
     * @return String containing contents of InputStream.
     * @throws IOException
     */
    private String inputstreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        return stringBuilder.toString();
    }

    /**
     * Pretty print a JsonObject.
     *
     * @param jsonObject JsonObject to pretty print.
     */
    private void prettyPrint(JsonObject jsonObject) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(jsonObject) + "\n");
    }
}