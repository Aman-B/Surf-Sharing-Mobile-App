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
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Data Message Body: " + remoteMessage.getData());

        Map<String, String> data = remoteMessage.getData();
        String messageTitle = data.get("message_title");
        String message = data.get("message");

        sendNotification(this, messageTitle, message);
    }

    public static void sendNotification(Context context, String messageTitle, String messageText) {

        Intent resultIntent = new Intent(context, LoginActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // PendingIntent notificationIntent = PendingIntent.getActivities(context, 0, new Intent[]{new Intent(context, AppCompatActivity.class)}, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(messageTitle);
        builder.setTicker("Alert Message");
        builder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(messageText));
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
