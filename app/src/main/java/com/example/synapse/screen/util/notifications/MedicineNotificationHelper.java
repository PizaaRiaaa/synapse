package com.example.synapse.screen.util.notifications;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import com.example.synapse.R;
import com.example.synapse.screen.carer.modules.fragments.MedicationFragment;
import com.example.synapse.screen.carer.modules.view.Sample;
import com.example.synapse.screen.carer.modules.view.ViewAppointment;
import com.example.synapse.screen.carer.modules.view.ViewMedicine;

import androidx.core.app.NotificationCompat;

public class MedicineNotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";

    private NotificationManager mManager;

    public MedicineNotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
       // notificationIntent.putExtra("MedicationID",alertReceiver.retrieveRequestCode());
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
                .setContentTitle("Medicine Reminder")
                .setContentText("It's time for your senior to take a medicine")
                .setAutoCancel(true)
                .setColorized(true)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(Color.RED, 3000, 3000)
                .setSmallIcon(R.drawable.ic_splash_logo);


    }
}