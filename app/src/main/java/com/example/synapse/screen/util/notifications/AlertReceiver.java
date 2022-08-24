package com.example.synapse.screen.util.notifications;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;

import com.example.synapse.R;
import com.example.synapse.screen.carer.CarerHome;
import com.example.synapse.screen.carer.modules.Medication;
import com.example.synapse.screen.carer.modules.PhysicalActivity;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int requestCode = intent.getIntExtra("REQUEST_CODE",-1);
        int medication = intent.getExtras().getInt("Medication");
        int physical = intent.getExtras().getInt("PhysicalActivity");
        int appointment = intent.getExtras().getInt("Appointment");
        int games = intent.getExtras().getInt("Games");

        MediaPlayer mp = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);

        // check if context is medication, physical activity, appointment or games
        if(medication == 1){
            MedicineNotificationHelper medicineNotificationHelper = new MedicineNotificationHelper(context);
            NotificationCompat.Builder nb = medicineNotificationHelper.getChannelNotification();
            medicineNotificationHelper.getManager().notify(requestCode, nb.build());

            context.sendBroadcast(new Intent("NOTIFY_MEDICINE"));

        }else if(physical == 2){

            PhysicalActivityNotificationHelper physicalActivityNotificationHelper = new PhysicalActivityNotificationHelper(context, requestCode, mp);
            NotificationCompat.Builder nb = physicalActivityNotificationHelper.getChannelNotification();
            physicalActivityNotificationHelper.getManager().notify(requestCode, nb.build());

            context.sendBroadcast(new Intent("NOTIFY_PHYSICAL_ACTIVITY"));
            mp.setLooping(true);
            mp.start();

        }else if(appointment == 3){
            AppointmentNotificationHelper appointmentNotificationHelper = new AppointmentNotificationHelper(context);
            NotificationCompat.Builder nb = appointmentNotificationHelper.getChannelNotification();
            appointmentNotificationHelper.getManager().notify(requestCode, nb.build());

            context.sendBroadcast(new Intent("NOTIFY_APPOINTMENT"));

        }else if(games == 4){
            GamesNotificationHelper gamesNotificationHelper = new GamesNotificationHelper(context);
            NotificationCompat.Builder nb = gamesNotificationHelper.getChannelNotification();
            gamesNotificationHelper.getManager().notify(requestCode, nb.build());

            context.sendBroadcast(new Intent("NOTIFY_GAMES"));


        }

       }
}
