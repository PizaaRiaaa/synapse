package com.example.synapse.screen.util.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;

import com.example.synapse.R;
import com.example.synapse.screen.carer.CarerHome;
import com.example.synapse.screen.carer.modules.Medication;
import com.example.synapse.screen.carer.modules.PhysicalActivity;

import java.util.Calendar;
import java.util.Objects;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int requestCode = intent.getIntExtra("REQUEST_CODE",-1);
        int medication = intent.getExtras().getInt("Medication");
        int physical = intent.getExtras().getInt("PhysicalActivity");
        int appointment = intent.getExtras().getInt("Appointment");
        int games = intent.getExtras().getInt("Games");

        // check if the alert receiver's context is medication, physical activity, appointment or games
        if(medication == 1){

            MedicineNotificationHelper medicineNotificationHelper = new MedicineNotificationHelper(context);
            NotificationCompat.Builder nb = medicineNotificationHelper.getChannelNotification();
            medicineNotificationHelper.getManager().notify(requestCode, nb.build());
            context.sendBroadcast(new Intent("NOTIFY_MEDICINE"));
            notificationRingtone(context, R.raw.medicine_reminder);

        }else if(physical == 2){

            PhysicalActivityNotificationHelper physicalActivityNotificationHelper = new PhysicalActivityNotificationHelper(context);
            NotificationCompat.Builder nb = physicalActivityNotificationHelper.getChannelNotification();
            physicalActivityNotificationHelper.getManager().notify(requestCode, nb.build());
            context.sendBroadcast(new Intent("NOTIFY_PHYSICAL_ACTIVITY"));
            notificationRingtone(context, R.raw.physical_activity_reminder);

        }else if(appointment == 3){

            AppointmentNotificationHelper appointmentNotificationHelper = new AppointmentNotificationHelper(context);
            NotificationCompat.Builder nb = appointmentNotificationHelper.getChannelNotification();
            appointmentNotificationHelper.getManager().notify(requestCode, nb.build());
            context.sendBroadcast(new Intent("NOTIFY_APPOINTMENT"));
            notificationRingtone(context, R.raw.appointment_tomorrow_reminder);


        }else if(games == 4){

            GamesNotificationHelper gamesNotificationHelper = new GamesNotificationHelper(context);
            NotificationCompat.Builder nb = gamesNotificationHelper.getChannelNotification();
            gamesNotificationHelper.getManager().notify(requestCode, nb.build());
            context.sendBroadcast(new Intent("NOTIFY_GAMES"));
            notificationRingtone(context, R.raw.game_reminder);

        }

     }

      // play custom alarm sound
     public void notificationRingtone(Context context, int sound){
          MediaPlayer mp = MediaPlayer.create(context, sound);
          mp.setLooping(false);
          mp.start();
    }

}
