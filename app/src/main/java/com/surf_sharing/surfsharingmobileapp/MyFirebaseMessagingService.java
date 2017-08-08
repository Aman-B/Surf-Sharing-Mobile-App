package com.surf_sharing.surfsharingmobileapp;

/**
 * Created by lukeagnew on 25/06/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.Map;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        Map<String, String> data = remoteMessage.getData();
        String messageTitle = data.get("message_title");
        String message = data.get("message");

        sendNotification(this, messageTitle, message);
    }

    public static void sendNotification(Context context, String messageTitle, String messageText) {

        PendingIntent notificationIntent = PendingIntent.getActivities(context, 0, new Intent[]{new Intent(context, AppCompatActivity.class)}, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(messageTitle);
        builder.setTicker("Alert Message");
        builder.setVibrate(new long[]{0, 200, 200, 200, 200});
        builder.setLights(Color.BLUE, 3000, 3000);
        builder.setContentText(messageText);
        builder.setContentIntent(notificationIntent);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, builder.build());
    }
}
