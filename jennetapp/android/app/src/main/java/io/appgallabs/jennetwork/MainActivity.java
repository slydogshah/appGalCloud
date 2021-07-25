package io.appgallabs.jennetwork;


import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugins.GeneratedPluginRegistrant;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;


import android.widget.Toast;

public class MainActivity extends FlutterActivity {

    private static final String TAG = "MainActivity";

    @Override
    public void configureFlutterEngine(FlutterEngine flutterEngine){
        GeneratedPluginRegistrant.registerWith(this.getFlutterEngine());
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "dexterx.dev/flutter_local_notifications_example").
                setMethodCallHandler { call, result ->
            if ("drawableToUri" == call.method) {
                val resourceId = this@MainActivity.resources.getIdentifier(call.arguments as String, "drawable", this@MainActivity.packageName)
                        result.success(resourceToUriString(this@MainActivity.applicationContext, resourceId))
            }
            if ("getAlarmUri" == call.method) {
                result.success(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString())
            }
            if ("getTimeZoneName" == call.method) {
                result.success(TimeZone.getDefault().id)
            }
        }

        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this.getContext());

        //Initialize Firebase
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        Log.d(TAG, "Subscribing to weather topic");
        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic("weather")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // [END subscribe_topics]

        // Get token
        // [START log_reg_token]
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // [END log_reg_token]


    }*/

    /*private String resourceToUriString(Context context, int resId) {
        return (ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + context.resources.getResourcePackageName(resId)
                + "/"
                + context.resources.getResourceTypeName(resId)
                + "/"
                + context.resources.getResourceEntryName(resId));
    }*/
}
