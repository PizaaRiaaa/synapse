package com.example.synapse.screen.util.notifications;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.NotificationCompat;

import com.example.synapse.R;
import com.example.synapse.screen.carer.modules.view.ViewAppointment;
import com.example.synapse.screen.carer.modules.view.ViewGame;
import com.example.synapse.screen.carer.modules.view.ViewMedicine;
import com.example.synapse.screen.carer.modules.view.ViewPhysicalActivity;
import com.example.synapse.screen.util.readwrite.ReadWriteMedication;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.example.synapse.screen.util.viewholder.MedicationViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class AlertReceiver extends BroadcastReceiver {

    DatabaseReference  referenceReminders  = FirebaseDatabase.getInstance().getReference().child("Medication Reminders");;
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    MedicineNotificationHelper medicineNotificationHelper;
    String med_id, physical_id, appointment_id, game_id;
    NotificationCompat.Builder nb;

    public String medicine_name;
    int requestCode1;
    int requestCode2;
    @Override
    public void onReceive(Context context, Intent intent) {

        requestCode1 = intent.getIntExtra("REQUEST_CODE",-1);
        int medication = intent.getExtras().getInt("Medication");
        int physical = intent.getExtras().getInt("PhysicalActivity");
        int appointment = intent.getExtras().getInt("Appointment");
        int games = intent.getExtras().getInt("Games");
        med_id = intent.getExtras().getString("med_id");
        physical_id = intent.getExtras().getString("physical_id");
        appointment_id = intent.getExtras().getString("appointment_id");
        game_id = intent.getExtras().getString("game_id");

        requestCode2 = intent.getExtras().getInt("request_code");



        // check if the alert receiver's context is medication, physical activity, appointment or games
        if(medication == 1){
            getName(requestCode2, context);
        }else if(physical == 2){
            PhysicalActivityNotificationHelper physicalActivityNotificationHelper = new PhysicalActivityNotificationHelper(context);
            nb = physicalActivityNotificationHelper.getChannelNotification();
            setContentIntent(context, ViewPhysicalActivity.class, "key", physical_id);
            physicalActivityNotificationHelper.getManager().notify(requestCode1, nb.build());
            context.sendBroadcast(new Intent("NOTIFY_PHYSICAL_ACTIVITY"));
            notificationRingtone(context);

        }else if(appointment == 3){
            AppointmentNotificationHelper appointmentNotificationHelper = new AppointmentNotificationHelper(context);
            nb = appointmentNotificationHelper.getChannelNotification();
            setContentIntent(context, ViewAppointment.class, "key", appointment_id);
            appointmentNotificationHelper.getManager().notify(requestCode1, nb.build());
            context.sendBroadcast(new Intent("NOTIFY_APPOINTMENT"));
            notificationRingtone(context);

        }else if(games == 4){
            GamesNotificationHelper gamesNotificationHelper = new GamesNotificationHelper(context);
            nb = gamesNotificationHelper.getChannelNotification();
            setContentIntent(context, ViewGame.class, "key", game_id);
            gamesNotificationHelper.getManager().notify(requestCode1, nb.build());
            context.sendBroadcast(new Intent("NOTIFY_GAMES"));
            notificationRingtone(context);
        }

     }

     // play custom alarm sound
     void notificationRingtone(Context context){
          MediaPlayer mp = MediaPlayer.create(context, R.raw.alarm);
          mp.setLooping(false);
          mp.start();
    }

    // redirect carer user to their respective screen when notification is click
        void setContentIntent(Context context, Class className, String putExtraKey, String module_id){
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


    public void getName(int requestcode, Context context){
        referenceReminders.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds1:snapshot.getChildren()){
                    String key1 = ds1.getKey();
                    referenceReminders.child(mUser.getUid()).child(key1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ignored:snapshot.getChildren()){
                                referenceReminders.child(mUser.getUid()).child(key1).child(med_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ReadWriteMedication rm = snapshot.getValue(ReadWriteMedication.class);
                                        Long code = rm.getRequestCode();
                                        if(code == requestcode){
                                            medicine_name = rm.getName();

                                            medicineNotificationHelper = new MedicineNotificationHelper(context);
                                            nb = medicineNotificationHelper.getChannelNotification();
                                            setContentIntent(context, ViewMedicine.class, "key", med_id);
                                            nb.setContentTitle("Medicine Reminder");
                                            nb.setContentText("It's time for your senior to take a medicine" + medicine_name);
                                            medicineNotificationHelper.getManager().notify(requestCode1, nb.build());
                                            context.sendBroadcast(new Intent("NOTIFY_MEDICINE"));
                                            notificationRingtone(context);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
