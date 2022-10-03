package com.example.synapse.screen.util.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import com.example.synapse.R;
import com.example.synapse.screen.carer.modules.fragments.HomeFragment;
import com.example.synapse.screen.carer.modules.fragments.MedicationFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

     NotificationManager mNotificationManager;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();
        switch (Objects.requireNonNull(title)) {
            case "Medicine Reminder":
                playVoiceReminder(R.raw.medicine_reminder);
                break;
            case "Physical Activity Reminder":
                playVoiceReminder(R.raw.physical_activity_reminder);
                break;
            case "Game Reminder":
                playVoiceReminder(R.raw.game_reminder);
                break;
            default:
                playVoiceReminder(R.raw.appointment_tomorrow_reminder);
                break;
        }

        // playing audio and vibration
         Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
         Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
         r.play();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.setLooping(false);
        }

        // vibration
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 300, 300, 300};
        v.vibrate(pattern, -1);

        int resourceImage =
                getResources()
                .getIdentifier(remoteMessage
                        .getNotification()
                        .getIcon(),
                        "drawable",
                        getPackageName());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "hello");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(resourceImage);
        } else {
            builder.setSmallIcon(resourceImage);
        }

        Intent resultIntent = new Intent(this, HomeFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_IMMUTABLE);

        builder.setContentTitle(remoteMessage.getNotification().getTitle());
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
        builder.setOnlyAlertOnce(true);
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX|Notification.FLAG_ONLY_ALERT_ONCE);

        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "hello";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        // notificationId is a unique int for each notification that you must define
        mNotificationManager.notify(100, builder.build());
    }

    void playVoiceReminder(int mp3Voice){
        MediaPlayer mp = MediaPlayer.create(this, mp3Voice);
        mp.setLooping(false);
        mp.start();
    }
}


