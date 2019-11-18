package com.mrex.ncs;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FMService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("FMService:", "onMessageReceived");

        String title = "title";
        String text = "text";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            text = remoteMessage.getNotification().getBody();
        }

        Log.e("FMService:", "title:" + title + "/text:" + text);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("ch01", "channel 01", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, "ch01");
        } else {
            builder = new NotificationCompat.Builder(this, null);
        }

        builder.setSmallIcon(R.drawable.ic_home);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setAutoCancel(true);

        Map<String, String> datas = remoteMessage.getData();
        if (datas != null) {
            String data1 = null;
            String data2 = null;
            if (datas.size() > 0) {
                data1 = datas.get("data1");
                data2 = datas.get("data2");
                Log.e("FMService:", "data1:" + data1 + "/data2:" + data2);
            }
        }

        Notification notification = builder.build();
        notificationManager.notify(10, notification);

    }

//    @Override
//    public void onNewToken(String token) {
//        Log.d(TAG, "Refreshed token: " + token);
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // Instance ID token to your app server.
//        sendRegistrationToServer(token);
//    }
}
