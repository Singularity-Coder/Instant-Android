package com.singularitycoder.notificationgenerator;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // > 4, > 5, > 6, > 8
    // Heads-up notification - importance high, activity full screen, notif channel has high importance
    // Lock screen notifications - Android 5
    // App Icon Badge => 8
    // Expandable notification
    // Notification actions
    // Notif below Android 6
    // Notif above Android 6
    // Custom Notification layout
    // Mega notification with all features

    public void basicNotification(View view) {
        // For Android 8 and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "channelId1";
            int id = 0;
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            // Necessary - sets small icon
                            .setSmallIcon(R.mipmap.ic_launcher)
                            // Necessary - Channel ID is necessary for >= A8 and must be unique
                            .setChannelId(CHANNEL_ID)
                            // Optional - Add Notif Title
                            .setContentTitle("Andy >= 8")
                            // Optional - Add Notif description
                            .setContentText("Android 8 Notification");

            // To show as Heads-up notification set the channel importance to IMPORTANCE_HIGH
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel Title 1", NotificationManager.IMPORTANCE_HIGH);
            // Issue as notification
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                manager.notify(id, mBuilder.build());
            }
        } else {
            // btw Android 4 and Android 8. No channel ID
            int id = 0;
            Notification.Builder mBuilder =
                    new Notification.Builder(this)
                            // Necessary - sets small icon
                            .setSmallIcon(R.mipmap.ic_launcher)
                            // Optional - Add Notif Title
                            .setContentTitle("Btw Andy 4 to 7")
                            // Optional - Add Notif description
                            .setContentText("Between Android 4 to 7");
            // Issue as notification
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                mNotificationManager.notify(id, mBuilder.build());
            }
        }
    }

    public void fullyLoadedBasicNotification(View view) {
        // For Android 8 and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "channelId1";
            int id = 1;
            int notifCount = 0;
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            // Necessary - sets small icon
                            .setSmallIcon(R.mipmap.ic_launcher)
                            // Necessary - Channel ID is necessary for >= A8 and must be unique
                            .setChannelId(CHANNEL_ID)
                            // Optional - Add Notif Title
                            .setContentTitle("Andy >= 8")
                            // Optional - Add Notif description
                            .setContentText("Android 8 Notification")
                            // Optional - Add Notif Vibration - needs vibrate permission in manifest. 1000 to 0 milli sec delay. { delay, vibrate, sleep, vibrate, sleep } pattern
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                            // Optional - Add Notif LED. Set color if device supports multi color notif LED. 3000 to 0 milli sec on/off LED time
                            .setLights(Color.RED, 3000, 0)
                            // Optional - Add Default Notif Sound - Get custom sound from local storage also - Uri.parse("uri://sound.mp3").
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            // Optional - Increases notif count every time new notif arrives
                            .setNumber(notifCount)
                            .setContentInfo("INFO")
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setWhen(System.currentTimeMillis())
                            .setTicker("{your tiny message}")
                            .setDefaults(-1)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setColor(getResources().getColor(R.color.colorPrimary))
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                            .setAutoCancel(true)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setOngoing(false);

            // To show as Heads-up notification set the channel importance to IMPORTANCE_HIGH
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel Title 1", NotificationManager.IMPORTANCE_HIGH);
            // Issue as notification
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                manager.notify(id, mBuilder.build());
            }
        } else {
            // btw Android 4 and Android 8. No channel ID
            int id = 1;
            int notifCount = 0;
            Notification.Builder mBuilder =
                    new Notification.Builder(this)
                            // Necessary - sets small icon
                            .setSmallIcon(R.mipmap.ic_launcher)
                            // Optional - Add Notif Title
                            .setContentTitle("Btw Andy 4 to 7")
                            // Optional - Add Notif description
                            .setContentText("Between Android 4 to 7");

            // Issue as notification
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                mNotificationManager.notify(id, mBuilder.build());
            }
        }
    }

    public void notify3(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "channelId3";
            Intent intent = new Intent(this, JumpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri notificationsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Test Notification")
                            .setContentText("Hey hey hey, i am a notification!")
                            .setContentIntent(pendingIntent)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText("Hey hey hey, i am a notification!"))
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.header)).bigLargeIcon(null))
                            .addAction(R.mipmap.ic_launcher, "VIEW", pendingIntent);

            mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel human readable title", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Hey hey hey, i am a notification!");
            channel.enableLights(true);
            channel.setShowBadge(true);
            channel.setLightColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);
            mNotificationManager.notify(2, mBuilder.build());
        } else {
            Intent intent = new Intent(this, JumpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri notificationsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification.Builder mBuilder =
                    new Notification.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Test Notification")
                            .setContentText("Hey hey hey, i am a notification!")
                            .addAction(R.mipmap.ic_launcher, "VIEW", pendingIntent)
                            .setStyle(new Notification.BigTextStyle().bigText("Hey hey hey, i am a notification!"))
                            .setDefaults(-1)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                            .setSound(notificationsound)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                            .setAutoCancel(true)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent)
                            .setOngoing(false);
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(3, mBuilder.build());
        }
    }

    public void notify4(View view) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "cid4")
                        // Necessary - sets small icon
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        // Optional - sets title
                        .setContentTitle("Title")
                        // Optional - show or hide time of notif
                        .setShowWhen(false)
                        // Optional - set notif time
                        .setWhen(12)
                        // Optional - Set a large icon - ex: For chat or newsfeed pic update
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                        // Optional - sets body
                        .setContentText("This is the body of Notification")
                        // Optional - For longer text n Expandable notifications
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Much longer text that cannot fit one line..."))
                        // Optional - For big picture - Amazon n Flipkart notificaton Ads style
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)))
                        // Optional - How intrusive it must be
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(4, builder.build());
    }

    public void notify5(View view) {
        Notification notification = new NotificationCompat.Builder(this, "cid6")
                .setSmallIcon(R.drawable.header)
                .setContentTitle("Hello Ho")
                .setContentText("THis is a hello form new noti")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("cadc dsca cas ca casc acda ca c ac a cdas ca cdac ac adc va dva d sav a dsv asdv s av a vasv s vsd vnin"))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.header))
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.header))
                        .bigLargeIcon(null))
                .build();

        // Issue the notification.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(10, notification);
    }

    public void notify6(View view) {
        Intent intent = new Intent(this, JumpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri notificationsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Test Notification")
                        .setContentText("Hey hey hey, i am a notification!")
                        .addAction(R.mipmap.ic_launcher, "VIEW", pendingIntent)
                        .setStyle(new Notification.BigTextStyle().bigText("Hey hey hey, i am a notification!"))
                        .setDefaults(-1)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setSound(notificationsound)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setOngoing(false);
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(3, mBuilder.build());
    }

    public void notify7(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            String CHANNEL_ID = "cid3";
            Intent intent = new Intent(this, JumpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri notificationsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Test Notification")
                            .setContentText("Hey hey hey, i am a notification!")
                            .addAction(R.mipmap.ic_launcher, "VIEW", pendingIntent)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText("Hey hey hey, i am a notification!"))
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.header)).bigLargeIcon(null))
                            .setDefaults(-1)
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                            .setSound(notificationsound)
                            .setChannelId(CHANNEL_ID)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setColor(getResources().getColor(R.color.colorPrimary))
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                            .setAutoCancel(true)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent)
                            .setOngoing(false);
            mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationChannel channel = null;
            channel = new NotificationChannel(CHANNEL_ID, "Channel human readable title", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Hey hey hey, i am a notification!");
            channel.enableLights(true);
            channel.setShowBadge(true);
            channel.setLightColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);
            mNotificationManager.notify(2, mBuilder.build());
        }
    }
}

