package com.dexterous.flutterlocalnotifications;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Keep;
import androidx.core.app.NotificationManagerCompat;

import com.dexterous.flutterlocalnotifications.models.NotificationDetails;
import com.dexterous.flutterlocalnotifications.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


@Keep
public class CheckNewDataReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, Intent intent) {
        /*try {
            if(true) {
                throw new RuntimeException("BLAH");
            }

            if (FlutterLocalNotificationsPlugin.eventSink != null) {
                Map<String, Object> event = new HashMap<>();
                FlutterLocalNotificationsPlugin.eventSink.success(event);
            }
        }
        finally {
            FlutterLocalNotificationsPlugin.startListening(context);
        }*/
    }
}
