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
    int pill_shape_color = 0;
    String channelId = "hello";

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


        builder = new NotificationCompat.Builder(this, "hello");
        Intent resultIntent = new Intent(this, HomeFragment.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                this, 1,
                resultIntent, PendingIntent.FLAG_IMMUTABLE);


       // String pill_color = resultIntent.getExtras().getString("pill_color");
       // String pill_shape = resultIntent.getExtras().getString("pill_shape");

//        Toast.makeText(this, "pill color" + pill_color , Toast.LENGTH_SHORT).show();

 ///       displayMedicine(pill_color, pill_shape);

        String tag = remoteMessage.getNotification().getTag();
        int pill_shape_color = 0;

        if(tag.equals("Pill1White")){
            pill_shape_color = R.drawable.pill1_white_horizontal;
        }

        builder.setSmallIcon(R.drawable.ic_pill2);
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_splash_logo));
        builder.setContentTitle(remoteMessage.getNotification().getTitle());
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setContentIntent(pendingIntent);
        builder.setColorized(true);
        builder.setVibrate(new long[]{0, 1000, 500, 3000});
        builder.setLights(Color.RED, 3000, 3000);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setOnlyAlertOnce(true);
        builder.setStyle(new NotificationCompat.BigPictureStyle()
                .setBigContentTitle(remoteMessage.getNotification().getTitle())
                .bigPicture(BitmapFactory.decodeResource(this.getResources(), pill_shape_color)));
        builder.setSound(defaultSoundUri);
        builder.setAutoCancel(true);

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

    void displayMedicine(String color, String shape){

        if(color.equals("White") && shape.equals("Pill1")){
            pill_shape_color = R.drawable.pill1_white_horizontal;
        }else if(color.equals("Blue") && shape.equals("Pill1")){
            pill_shape_color = R.drawable.pill1_blue_horizontal;
        }else if(color.equals("Brown") && shape.equals("Pill1")){
            pill_shape_color = R.drawable.pill1_brown_horizontal;
        }else if(color.equals("Green") && shape.equals("Pill1")){
            pill_shape_color = R.drawable.pill1_green_horizontal;
        }else if(color.equals("Pink") && shape.equals("Pill1")){
            pill_shape_color = R.drawable.pill1_pink_horizontal;
        }else if(color.equals("Red") && shape.equals("Pill1")){
            pill_shape_color = R.drawable.pill1_red_horizontal;
        }

        if(color.equals("White") && shape.equals("Pill2")){
            pill_shape_color = R.drawable.pill2_white;
        }else if(color.equals("Blue") && shape.equals("Pill2")){
            pill_shape_color = R.drawable.pill2_blue;
        }else if(color.equals("Brown") && shape.equals("Pill2")){
            pill_shape_color = R.drawable.pill2_brown;
        }else if(color.equals("Green") && shape.equals("Pill2")){
            pill_shape_color = R.drawable.pill2_green;
        }else if(color.equals("Pink") && shape.equals("Pill2")){
            pill_shape_color = R.drawable.pill2_pink;
        }else if(color.equals("Red") && shape.equals("Pill2")){
            pill_shape_color = R.drawable.pill2_red;
        }

        if(color.equals("White") && shape.equals("Pill3")){
            pill_shape_color = R.drawable.pill3_white_horizontal;
        }else if(color.equals("Blue") && shape.equals("Pill3")){
            pill_shape_color = R.drawable.pill3_blue_horizontal;
        }else if(color.equals("Brown") && shape.equals("Pill3")){
            pill_shape_color = R.drawable.pill3_brown_horizontal;
        }else if(color.equals("Green") && shape.equals("Pill3")){
            pill_shape_color = R.drawable.pill3_green_horizontal;
        }else if(color.equals("Pink") && shape.equals("Pill3")){
            pill_shape_color = R.drawable.pill3_pink_horizontal;
        }else if(color.equals("Red") && shape.equals("Pill3")){
            pill_shape_color = R.drawable.pill3_red_horizontal;
        }

        if(color.equals("White") && shape.equals("Pill4")){
            pill_shape_color = R.drawable.pill4_white_horizontal;
        }else if(color.equals("Blue") && shape.equals("Pill4")){
            pill_shape_color = R.drawable.pill4_blue_horizontal;
        }else if(color.equals("Brown") && shape.equals("Pill4")){
            pill_shape_color = R.drawable.pill4_brown_horizontal;
        }else if(color.equals("Green") && shape.equals("Pill4")){
            pill_shape_color = R.drawable.pill4_green_horizontal;
        }else if(color.equals("Pink") && shape.equals("Pill4")){
            pill_shape_color = R.drawable.pill4_pink_horizontal;
        }else if(color.equals("Red") && shape.equals("Pill4")){
            pill_shape_color = R.drawable.pill4_red_horizontal;
        }

    }
}


