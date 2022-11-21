package com.example.synapse.screen.util.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.example.synapse.R;
import com.example.synapse.screen.senior.SeniorMainActivity;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Objects;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Uri defaultSoundUri;

    Intent takeIntent1;
    Intent takeIntent2;
    Intent takeIntent3;
    Intent takeIntent4;

    String channelId = "hello";
    String tag;
    String key;
    String title;
    String temp;

    int pill_shape_color = 0;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        builder = new NotificationCompat.Builder(FirebaseMessagingService.this, "hello");
        title = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();
        tag = remoteMessage.getNotification().getTag().split(" ")[0];
        temp = remoteMessage.getNotification().getTag();

        if(temp != null) key = remoteMessage.getNotification().getTag().split(" ")[1];

        tagBigPicture(tag);

        builder.setSmallIcon(R.drawable.ic_clock_notif);
        builder.setColor(ContextCompat.getColor(this, R.color.dark_green));
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_splash_logo));
        builder.setContentTitle(remoteMessage.getNotification().getTitle());
        builder.setContentText(remoteMessage.getNotification().getBody());

        // notification button per module
        if(remoteMessage.getNotification().getTitle().equals("Medicine Reminder")){
            takeIntent1 = new Intent(getApplicationContext(), SeniorMainActivity.class);
            takeIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            takeIntent1.putExtra("med_key", key);
            PendingIntent takePIntent = PendingIntent.getActivity(
                    this, 0, takeIntent1,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            builder.addAction(R.drawable.ic_clock_notif, "TAKE", takePIntent);
            builder.addAction(R.drawable.ic_clock_notif, "OPEN", null);

        }else if(remoteMessage.getNotification().getTitle().equals("Physical Activity Reminder")){
            takeIntent2 = new Intent(getApplicationContext(), SeniorMainActivity.class);
            takeIntent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            takeIntent2.putExtra("phy_key", key);
            PendingIntent takePIntent = PendingIntent.getActivity(
                    this, 0, takeIntent2,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            builder.addAction(R.drawable.ic_clock_notif, "DONE", takePIntent);
            builder.addAction(R.drawable.ic_clock_notif, "OPEN", null);
        }

        builder.setColorized(true);
        builder.setVibrate(new long[]{0, 1000, 500, 3000});
        builder.setColor(getResources().getColor(R.color.dark_green));
        builder.setLights(Color.RED, 3000, 3000);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setOnlyAlertOnce(true);
        builder.setStyle(new NotificationCompat.BigPictureStyle()
                .setBigContentTitle(remoteMessage.getNotification().getTitle())
                .bigPicture(BitmapFactory.decodeResource(this.getResources(), pill_shape_color)));
        builder.setSound(defaultSoundUri);
        builder.setAutoCancel(false);

        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    void playVoiceReminder(int mp3Voice) {
        MediaPlayer mp = MediaPlayer.create(this, mp3Voice);
        mp.setLooping(false);
        mp.start();
    }

    // redirect carer user to their respective screen when notification is click
   // void setContentIntent(Context context, Class className, String putExtraKey, String module_id) {
   //     Intent appActivityIntent = new Intent(context, className);
   //     appActivityIntent.putExtra(putExtraKey, module_id);
   //     PendingIntent contentAppActivityIntent =
   //             PendingIntent.getActivity(
   //                     context,
   //                     0,
   //                     appActivityIntent,
   //                     PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
   //     builder.setContentIntent(contentAppActivityIntent);
   // }

    void tagBigPicture(String name_tag){
        switch (tag) {
            case "Pill1White":
                pill_shape_color = R.drawable.pill1_white_horizontal;
                break;
            case "Pill1Blue":
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
            case "Tic-tac-toe":
                pill_shape_color = R.drawable.tic_tac_toe_logo;
                break;
            case "Trivia Quiz":
                pill_shape_color = R.drawable.ic_trivia_quiz;
                break;
            case "Math Game":
                pill_shape_color = R.drawable.ic_math_game;
                break;
            case "appointment":
                pill_shape_color = R.drawable.ic_appointment;
                break;
            case "physical":
                pill_shape_color = R.drawable.ic_bigpicture_workout;
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
    }


}

