package io.appgal.cloud.rest.client;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class FirebaseComponentTests {
    private static Logger logger = LoggerFactory.getLogger(FirebaseComponentTests.class);

    @Test
    public void test() throws Exception
    {
        // 10.0.2.2 is the special IP address to connect to the 'localhost' of
        // the host computer from an Android emulator.
        /*FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                //.setHost("10.0.2.2:8080")
                .setHost("127.0.0.1:8080")
                .setSslEnabled(false)
                .setPersistenceEnabled(false)
                .build();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(settings);*/
        //FirebaseFunctions.getInstance().useFunctionsEmulator("http://10.0.2.2:5001");
    }
}
