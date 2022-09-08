package com.example.synapse.screen.util.notifications;

import android.app.Activity;
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
import com.example.synapse.screen.carer.modules.view.ViewAppointment;
import com.example.synapse.screen.carer.modules.view.ViewGame;
import com.example.synapse.screen.carer.modules.view.ViewMedicine;
import com.example.synapse.screen.carer.modules.view.ViewPhysicalActivity;

public class AlertReceiver extends BroadcastReceiver {

    String med_id, physical_id,
    appointment_id, game_id;
    Ringtone ringtoneAlarm;
    NotificationCompat.Builder nb;

    @Override
    public void onReceive(Context context, Intent intent) {

        int requestCode = intent.getIntExtra("REQUEST_CODE",-1);

        int medication = intent.getExtras().getInt("Medication");
        int physical = intent.getExtras().getInt("PhysicalActivity");
        int appointment = intent.getExtras().getInt("Appointment");
        int games = intent.getExtras().getInt("Games");

        med_id = intent.getExtras().getString("med_id");
        physical_id = intent.getExtras().getString("physical_id");
        appointment_id = intent.getExtras().getString("appointment_id");
        game_id = intent.getExtras().getString("game_id");

        // check if the alert receiver's context is medication, physical activity, appointment or games
        if(medication == 1){
            MedicineNotificationHelper medicineNotificationHelper = new MedicineNotificationHelper(context);
            nb = medicineNotificationHelper.getChannelNotification();
            setContentIntent(context, ViewMedicine.class, "key", med_id);
            medicineNotificationHelper.getManager().notify(requestCode, nb.build());
            context.sendBroadcast(new Intent("NOTIFY_MEDICINE"));
            notificationRingtone(context, R.raw.medicine_reminder);

        }else if(physical == 2){
            PhysicalActivityNotificationHelper physicalActivityNotificationHelper = new PhysicalActivityNotificationHelper(context);
            nb = physicalActivityNotificationHelper.getChannelNotification();
            setContentIntent(context, ViewPhysicalActivity.class, "key", physical_id);
            physicalActivityNotificationHelper.getManager().notify(requestCode, nb.build());
            context.sendBroadcast(new Intent("NOTIFY_PHYSICAL_ACTIVITY"));
            notificationRingtone(context, R.raw.physical_activity_reminder);

        }else if(appointment == 3){
            AppointmentNotificationHelper appointmentNotificationHelper = new AppointmentNotificationHelper(context);
            nb = appointmentNotificationHelper.getChannelNotification();
            setContentIntent(context, ViewAppointment.class, "key", appointment_id);
            appointmentNotificationHelper.getManager().notify(requestCode, nb.build());
            context.sendBroadcast(new Intent("NOTIFY_APPOINTMENT"));
            notificationRingtone(context, R.raw.appointment_tomorrow_reminder);

        }else if(games == 4){
            GamesNotificationHelper gamesNotificationHelper = new GamesNotificationHelper(context);
            nb = gamesNotificationHelper.getChannelNotification();
            setContentIntent(context, ViewGame.class, "key", game_id);
            gamesNotificationHelper.getManager().notify(requestCode, nb.build());
            context.sendBroadcast(new Intent("NOTIFY_GAMES"));
            notificationRingtone(context, R.raw.game_reminder);
        }

     }

     // play custom alarm sound
     public void notificationRingtone(Context context, int sound){
         // default alarm sound
         Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
         ringtoneAlarm = RingtoneManager.getRingtone(context, alarmTone);
         ringtoneAlarm.play();
         // speech alarm
          MediaPlayer mp = MediaPlayer.create(context, sound);
          mp.setLooping(false);
          mp.start();
    }

    // redirect user to respective screen when notification is click
    public void setContentIntent(Context context, Class className, String putExtraKey, String module_id){
        Intent appActivityIntent = new Intent(context,className);
        appActivityIntent.putExtra(putExtraKey,module_id);
        PendingIntent contentAppActivityIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        appActivityIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        nb.setContentIntent(contentAppActivityIntent);
    }

}
