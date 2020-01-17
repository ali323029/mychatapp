package com.example.mychatapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FBMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        int Channlid =  (int)System.currentTimeMillis();


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Channlid")
                .setSmallIcon(R.drawable.appicon)
                .setContentTitle("Friend Request")
                .setContentText("You've recived a new friend request")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        int notificationID = (int) System.currentTimeMillis();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationID, builder.build());

    }
}
