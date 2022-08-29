package com.example.synapse.screen.util.notifications;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.synapse.R;
import com.example.synapse.screen.carer.CarerHome;
import com.example.synapse.screen.carer.modules.PhysicalActivity;

public class PhysicalActivityNotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private NotificationManager mManager;

    PendingIntent pendingIntent;


    //public PhysicalActivityNotificationHelper(Context base, int requestCode, MediaPlayer mp) {
    //    super(base);
    //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    //        createChannel();
    //    }

    public PhysicalActivityNotificationHelper(Context base, int requestCode) {
            super(base);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel();
            }

      // Intent intent1 = new Intent(base, CarerHome.class);
      // pendingIntent = PendingIntent.getActivity(
      //          base,
      //          requestCode,
      //          intent1,
      //          PendingIntent.FLAG_IMMUTABLE);

      // mp.stop();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle("Physical Activity Reminder")
                .setContentText("It's time for your senior to do physical activity")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_splash_logo);
    }
}