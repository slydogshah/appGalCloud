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
    private String url;

    public PushNotificationCallback(Context appContext){
        Breadcaster caster = new Breadcaster(appContext)
                .action("1", new Execution() {
                    @Override
                    public void exec(Context context, Intent intent) {
                        String email = intent.getStringExtra("email");
                        if(email != null){
                            PushNotificationCallback.this.email = email;
                            String url = intent.getStringExtra("url");
                            PushNotificationCallback.this.url = url;
                        }
                        String token = intent.getStringExtra("token");
                        if(token != null){
                            PushNotificationCallback.this.token = token;
                        }
                        if(PushNotificationCallback.this.email == null || PushNotificationCallback.this.token == null){
                            return;
                        }
                        Thread t = new Thread(()->{
                            URL urlValue = null;
                            HttpsURLConnection urlConnection = null;
                            String json = null;
                            OutputStreamWriter writer = null;
                            BufferedReader reader = null;
                            try {
                                urlValue = new URL(PushNotificationCallback.this.url);
                                urlConnection = (HttpsURLConnection) urlValue.openConnection();
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
                                if(urlConnection != null) {
                                    urlConnection.disconnect();
                                }
                                try {
                                    if(writer != null) {
                                        writer.close();
                                    }
                                    if(reader != null) {
                                        reader.close();
                                    }
                                }catch(Exception e){}
                            }
                        });
                        t.start();
                    }
                }).register();
    }
}