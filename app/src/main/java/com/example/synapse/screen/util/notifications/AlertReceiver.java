package com.example.synapse.screen.util.notifications;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.example.synapse.R;
import com.example.synapse.screen.carer.modules.view.Sample;
import com.example.synapse.screen.carer.modules.view.ViewMedicine;

public class AlertReceiver extends BroadcastReceiver {

    String med_id;
   public Ringtone ringtoneAlarm;

    @Override
    public void onReceive(Context context, Intent intent) {

        int requestCode = intent.getIntExtra("REQUEST_CODE",-1);

        int medication = intent.getExtras().getInt("Medication");

        med_id = intent.getExtras().getString("med_id");

        int physical = intent.getExtras().getInt("PhysicalActivity");

        int appointment = intent.getExtras().getInt("Appointment");

        int games = intent.getExtras().getInt("Games");

        // check if the alert receiver's context is medication, physical activity, appointment or games

        if(medication == 1){

            MedicineNotificationHelper medicineNotificationHelper = new MedicineNotificationHelper(context);
            NotificationCompat.Builder nb = medicineNotificationHelper.getChannelNotification();
            Intent appActivityIntent = new Intent(context, ViewMedicine.class);
            appActivityIntent.putExtra("key", med_id);
            appActivityIntent.putExtra("clicked",true);
            PendingIntent contentAppActivityIntent =
                    PendingIntent.getActivity(
                            context,  // calling from Activity
                            0,
                            appActivityIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);

            nb.setContentIntent(contentAppActivityIntent);
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

      // play alarm sound
     public void notificationRingtone(Context context, int sound){
         // default alarm sound
         Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
         ringtoneAlarm = RingtoneManager.getRingtone(context, alarmTone);
         ringtoneAlarm.play();

         // speech alarm
          MediaPlayer mp = MediaPlayer.create(context, sound);
          mp.setLooping(false);
          mp.start();
    }
}
