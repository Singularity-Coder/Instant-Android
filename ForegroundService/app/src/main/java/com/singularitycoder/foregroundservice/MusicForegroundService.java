package com.singularitycoder.foregroundservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class MusicForegroundService extends Service {

    private static final String TAG = "MusicForegroundService";

    public static final String ACTION_PLAY = "com.singularitycoder.foregroundservice.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.singularitycoder.foregroundservice.ACTION_PAUSE";
    public static final String ACTION_STOP = "com.singularitycoder.foregroundservice.ACTION_STOP";

    private static final int ID_NOTIFICATION = 111;
    private static final String ID_AUDIO_CHANNEL = "audio_playback_channel";

    @NonNull
    private final MediaPlayer mediaPlayer = new MediaPlayer();

    private Notification notification;
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) this;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaSession.release();
        removeNotification();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerMainActivityPauseReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mediaPlayer) {
            stopAudio();
            mediaPlayer.release();
        }
        removeNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String audioUrl = intent.getStringExtra("AUDIO_URL");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();    // Create channel if > API 26

        if (!("").equals(audioUrl)) {
            if (null == mediaSessionManager) {
                try {
                    setUpMediaSession();
                    setUpMediaPlayer(audioUrl);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    stopSelf();
                }
                buildNotification();
            }
        } else {
            stopSelf();
        }

        // Notif actions
        if (null != intent && null != intent.getAction()) {

            String actionString = intent.getAction();

            if ((ACTION_PLAY).equalsIgnoreCase(actionString)) {
                transportControls.play();
            }

            if ((ACTION_PAUSE).equalsIgnoreCase(actionString)) {
                transportControls.pause();
            }

            if ((ACTION_STOP).equalsIgnoreCase(actionString)) {
                transportControls.stop();
            }
        }

        startForeground(ID_NOTIFICATION, notification);

        return super.onStartCommand(intent, flags, startId);
    }

    private void setUpMediaPlayer(String audioUrl) {
        Log.d(TAG, "initMediaPlayer: " + audioUrl);

        if (("").equals(audioUrl)) return;

        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            stopAudio();
            removeNotification();
            stopSelf();  // Stop Service
        });

        mediaPlayer.setOnPreparedListener(mediaPlayer -> {
            playAudio();    // when audio source is ready to play this starts
        });

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(audioUrl);
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }

        mediaPlayer.prepareAsync();  // prepares stream asynchronously.
    }

    private void setUpMediaSession() throws RemoteException {
        if (null != mediaSessionManager) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);

        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {

            @Override
            public void onPlay() {
                super.onPlay();
                playAudio();
                buildNotification();
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseAudio();
                buildNotification();
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                stopSelf();
            }
        });
    }

    private void pauseAudio() {
        if (null == mediaPlayer) return;
        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

    private void playAudio() {
        if (null == mediaPlayer) return;
        if (!mediaPlayer.isPlaying()) mediaPlayer.start();
    }

    private void stopAudio() {
        if (null == mediaPlayer) return;
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
    }

    private BroadcastReceiver pauseAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pauseAudio();
        }
    };

    private void registerMainActivityPauseReceiver() {
        IntentFilter filter = new IntentFilter(MainActivity.BROADCAST_PAUSE);
        registerReceiver(pauseAudio, filter);
    }

    private void buildNotification() {
        int RC_PLAY = 0;
        int RC_PAUSE = 1;
        int RC_STOP = 2;

        Intent playbackAction = new Intent(this, MusicForegroundService.class);

        // PLAY
        playbackAction.setAction(ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getService(this, RC_PLAY, playbackAction, 0);

        // PAUSE
        playbackAction.setAction(ACTION_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getService(this, RC_PAUSE, playbackAction, 0);

        // STOP
        playbackAction.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, RC_STOP, playbackAction, 0);

        // Create a new Notification
        notification = new NotificationCompat.Builder(this, ID_AUDIO_CHANNEL)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()    // Set Notification style
                        .setMediaSession(mediaSession.getSessionToken())    // Attach MediaSession token
                        .setShowActionsInCompactView(RC_PLAY, RC_PAUSE, RC_STOP))   // Show playback controls in compat view
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Music Foreground Service")
                .setContentText("Foreground Service")
                .setContentInfo("Music")
                .addAction(android.R.drawable.ic_media_play, "play", playPendingIntent)
                .addAction(android.R.drawable.ic_media_pause, "pause", pausePendingIntent)
                .addAction(android.R.drawable.ic_menu_delete, "stop", stopPendingIntent)
                .build();

//        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(ID_NOTIFICATION);
    }

    private void createChannel() {
        int importance;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            importance = NotificationManager.IMPORTANCE_LOW;
        else importance = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notifChannel = new NotificationChannel(ID_AUDIO_CHANNEL, "Audio playback", importance);
            notifChannel.setDescription("Audio playback controls");
            notifChannel.setShowBadge(false);
            notifChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notifChannel);
        }
    }
}