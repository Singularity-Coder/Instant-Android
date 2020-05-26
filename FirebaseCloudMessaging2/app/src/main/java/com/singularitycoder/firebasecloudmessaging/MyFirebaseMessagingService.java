package com.singularitycoder.firebasecloudmessaging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (null == remoteMessage) return;
        checkNotificationInMessage(remoteMessage);
    }

    private void checkNotificationInMessage(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            Map<String, String> data = remoteMessage.getData();
            if (data.containsKey("title") && data.containsKey("message")) {
                showNotification(data.get("title"), data.get("message"));
            }
        }
    }

    private void showNotification(final String title, final String message) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "channelId";
            final int id = 0;
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setChannelId(CHANNEL_ID);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Channel Title", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (null != notificationManager) {
                notificationManager.createNotificationChannel(notificationChannel);
                notificationManager.notify(id, mBuilder.build());
            }
        } else {
            // Android 4 to Android 7
            final int id = 0;
            Notification.Builder mBuilder =
                    new Notification.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(message);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (null != notificationManager) {
                notificationManager.notify(id, mBuilder.build());
            }
        }
    }
}