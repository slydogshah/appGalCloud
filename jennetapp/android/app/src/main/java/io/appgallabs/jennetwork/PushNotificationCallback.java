package io.appgallabs.jennetwork;

import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import io.dreiklang.breadcast.annotation.Receive;
import io.dreiklang.breadcast.base.*;
import io.dreiklang.breadcast.base.exec.Execution;

public class PushNotificationCallback{
    private String email;
    private String token;

    public PushNotificationCallback(Context appContext){
        Breadcaster caster = new Breadcaster(appContext)
                .action("1", new Execution() {
                    @Override
                    public void exec(Context context, Intent intent) {
                        String email = intent.getStringExtra("email");
                        if(email != null){
                            PushNotificationCallback.this.email = email;
                        }
                        String token = intent.getStringExtra("token");
                        if(token != null){
                            PushNotificationCallback.this.token = token;
                        }
                        if(PushNotificationCallback.this.email == null || PushNotificationCallback.this.token == null){
                            return;
                        }
                        Thread t = new Thread(()->{
                            URL url = null;
                            HttpsURLConnection urlConnection = null;
                            String json = null;
                            OutputStreamWriter writer = null;
                            BufferedReader reader = null;
                            try {
                                //TODO
                                url = new URL("https://appgal-cloud-do2cwgwhja-uc.a.run.app/activeNetwork/registerPush");
                                //url = new URL("https://10.0.2.2/activeNetwork/registerPush");
                                urlConnection = (HttpsURLConnection) url.openConnection();
                                urlConnection.setDoOutput(true);

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("email",PushNotificationCallback.this.email);
                                jsonObject.put("pushToken",PushNotificationCallback.this.token);
                                json = jsonObject.toString();

                                writer = new OutputStreamWriter(urlConnection.getOutputStream());
                                writer.write(json);
                                writer.flush();

                                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                                StringBuilder sb = new StringBuilder();
                                String line = null;
                                // Read Server Response
                                while((line = reader.readLine()) != null)
                                {
                                    // Append server response in string
                                    sb.append(line + "\n");
                                }
                                System.out.println("TOKEN_REGISTRATION_SUCCESS: "+sb);
                            }catch(Exception e){
                                throw new RuntimeException(e);
                            }finally {
                                urlConnection.disconnect();
                                try {
                                    writer.close();
                                    reader.close();
                                }catch(Exception e){}
                            }
                        });
                        t.start();
                    }
                }).register();
    }
}