package com.mrex.ncs;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FMService extends FirebaseMessagingService {

    public static Boolean isChatForeground;

    private String what, data1, data2, chatUID;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("FMService:", "onMessageReceived");
        Log.e("FMService:", "remoteMessage.getFrom(): " + remoteMessage.getFrom());

        Map<String, String> datas = remoteMessage.getData();
        if (datas != null) {
            what = null;
            data1 = null;
            data2 = null;
            if (datas.size() > 0) {
                what = datas.get("what");
                data1 = datas.get("data1");
                data2 = datas.get("data2");
                if (what.equals("chat")) {
                    chatUID = datas.get("chatUID");
                    Log.e("FMService:", "chatUID:" + chatUID);
                }
                Log.e("FMService:", "what:" + what + "  data1:" + data1 + "  data2:" + data2);
            }
        }

        if (isChatForeground == null) {
            isChatForeground = false;
        }
        if (what.equals("chat")) {
            if (isChatForeground) {
                return;
            }
        }


//        String title = "title";
//        String text = "text";
//
//        if (remoteMessage.getNotification() != null) {
//            title = remoteMessage.getNotification().getTitle();
//            text = remoteMessage.getNotification().getBody();
//        }
//
//        Log.e("FMService:", "title:" + title + "/text:" + text);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("ch01", "channel 01", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, "ch01");
        } else {
            builder = new NotificationCompat.Builder(this, null);
        }

        builder.setSmallIcon(R.drawable.ic_home_white);
        builder.setContentTitle(data1);
        builder.setContentText(data2);
        builder.setAutoCancel(true);

        if (what.equals("chat")) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("goTo", "chat");
            intent.putExtra("chatUID", chatUID);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

        } else if (what.equals("reservation")) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("goTo", "reservation");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        }

        Notification notification = builder.build();
        notificationManager.notify(10, notification);

    }

}
