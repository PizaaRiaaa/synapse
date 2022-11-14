package com.example.synapse.screen.util.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

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

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    NotificationManager mNotificationManager;
    Uri defaultSoundUri= RingtoneManager.getDefaultUri(R.raw.alarm);
    NotificationCompat.Builder builder;
    String channelId = "hello";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        builder = new NotificationCompat.Builder(this, "hello");
        Intent resultIntent = new Intent(this, HomeFragment.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                this, 1,
                resultIntent, PendingIntent.FLAG_IMMUTABLE);

        String tag = remoteMessage.getNotification().getTag();

        int pill_shape_color = 0;
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
            case "Appointment Reminder":
                playVoiceReminder(R.raw.appointment_tomorrow_reminder);
                break;
            default:
                break;
        }

       // displayMedicine(tag);

        switch (tag) {
            case "Pill1White":
                pill_shape_color = R.drawable.pill1_white_horizontal;
                break;
            case "Pill2Blue":
                pill_shape_color = R.drawable.pill1_blue_horizontal;
                break;
            case "Pill1Brown":
                pill_shape_color = R.drawable.pill1_brown_horizontal;
                break;
            case "Pill1Green":
                pill_shape_color = R.drawable.pill1_green_horizontal;
                break;
            case "Pill1Pink":
                pill_shape_color = R.drawable.pill1_pink_horizontal;
                break;
            case "Pill1Red":
                pill_shape_color = R.drawable.pill1_red_horizontal;
                break;
        }

        switch (tag) {
            case "Pill2White":
                pill_shape_color = R.drawable.pill2_white;
                break;
            case "Pill2Blue":
                pill_shape_color = R.drawable.pill2_blue;
                break;
            case "Pill2Brown":
                pill_shape_color = R.drawable.pill2_brown;
                break;
            case "Pill2Green":
                pill_shape_color = R.drawable.pill2_green;
                break;
            case "Pill2Pink":
                pill_shape_color = R.drawable.pill2_pink;
                break;
            case "Pill2Red":
                pill_shape_color = R.drawable.pill2_red;
                break;
        }

        switch (tag) {
            case "Pill3White":
                pill_shape_color = R.drawable.pill3_white_horizontal;
                break;
            case "Pill3Blue":
                pill_shape_color = R.drawable.pill3_blue_horizontal;
                break;
            case "Pill3Brown":
                pill_shape_color = R.drawable.pill3_brown_horizontal;
                break;
            case "Pill3Green":
                pill_shape_color = R.drawable.pill3_green_horizontal;
                break;
            case "Pill3Pink":
                pill_shape_color = R.drawable.pill3_pink_horizontal;
                break;
            case "Pill3Red":
                pill_shape_color = R.drawable.pill3_red_horizontal;
                break;
        }

        switch (tag) {
            case "Pill4White":
                pill_shape_color = R.drawable.pill4_white_horizontal;
                break;
            case "Pill4Blue":
                pill_shape_color = R.drawable.pill4_blue_horizontal;
                break;
            case "Pill4Brown":
                pill_shape_color = R.drawable.pill4_brown_horizontal;
                break;
            case "Pill4Green":
                pill_shape_color = R.drawable.pill4_green_horizontal;
                break;
            case "Pill4Pink":
                pill_shape_color = R.drawable.pill4_pink_horizontal;
                break;
            case "Pill4Red":
                pill_shape_color = R.drawable.pill4_red_horizontal;
                break;
        }

        builder.setSmallIcon(R.drawable.ic_splash_logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_splash_logo));
        builder.setContentTitle(remoteMessage.getNotification().getTitle());
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setContentIntent(pendingIntent);
        builder.setColorized(true);
        builder.setColor(getResources().getColor(R.color.white));
        builder.setVibrate(new long[]{0, 1000, 500, 3000});
        builder.setLights(Color.RED, 3000, 3000);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setOnlyAlertOnce(true);
        builder.setStyle(new NotificationCompat.BigPictureStyle()
                .setBigContentTitle(remoteMessage.getNotification().getTitle())
                .bigPicture(BitmapFactory.decodeResource(this.getResources(), pill_shape_color)));
        builder.setSound(defaultSoundUri);
        builder.setAutoCancel(false);

        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        mNotificationManager.notify(100, builder.build());
    }

    void playVoiceReminder(int mp3Voice){
        MediaPlayer mp = MediaPlayer.create(this, mp3Voice);
        mp.setLooping(false);
        mp.start();
    }

}


