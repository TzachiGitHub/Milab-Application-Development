package com.example.stocksfirebasepushnotifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMEssagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String FIREBASE_APPLICATION_ID_KEY = "BI1n6oDop7FyVOWS1fu8KFkNoxMDaocT4c0hW-2PA2rgFQ_cQjeMTRNgmOa3NqQK8-xRlJX6BcE2d4u-G-O1eh8";

    public MyFirebaseMEssagingService() {    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Messages", "Stocks Notifications", importance);
            channel.setDescription("All messages.");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "\n\n\n\nFrom: " + remoteMessage.getFrom());
        createNotificationChannel();

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            int icon = R.drawable.common_google_signin_btn_icon_dark;
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            Notification notification = new NotificationCompat.Builder(this, "Messages")
                    .setSmallIcon(icon)
                    .setContentText(remoteMessage.getData().get("MyKey1"))
                    .setContentTitle("And it will get higher and higher")
                    .build();
            manager.notify(0, notification);
        }
    }

    @Override
    public void onNewToken(String token){
        super.onNewToken(token);
    }



}
