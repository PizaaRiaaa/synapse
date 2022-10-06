package com.example.synapse.screen.util.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.synapse.R;
import com.example.synapse.screen.carer.modules.view.ViewAppointment;
import com.example.synapse.screen.carer.modules.view.ViewGame;
import com.example.synapse.screen.carer.modules.view.ViewMedicine;
import com.example.synapse.screen.carer.modules.view.ViewPhysicalActivity;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.readwrite.ReadWriteAppointment;
import com.example.synapse.screen.util.readwrite.ReadWriteGames;
import com.example.synapse.screen.util.readwrite.ReadWriteMedication;
import com.example.synapse.screen.util.readwrite.ReadWritePhysicalActivity;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class AlertReceiver extends BroadcastReceiver {

    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference referenceProfile,medicationReminder, referenceCompanion,
            physicalActivityReminder, appointmentReminder, gameReminder;
    String med_id, physical_id, appointment_id, game_id, seniorID, token, pill_color, pill_name;
    PromptMessage promptMessage = new PromptMessage();
    NotificationCompat.Builder nb;
    MedicineNotificationHelper medicineNotificationHelper;
    PhysicalActivityNotificationHelper physicalActivityNotificationHelper;
    AppointmentNotificationHelper appointmentNotificationHelper;
    GamesNotificationHelper gamesNotificationHelper;
    RequestQueue requestQueue;
    Bitmap bmp = null;
    int pill_shape_color = 0;
    int requestCode1;
    int requestCode2;

    @Override
    public void onReceive(Context context, Intent intent) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        requestQueue = Volley.newRequestQueue(context);
        medicationReminder = FirebaseDatabase.getInstance().getReference().child("Medication Reminders");
        physicalActivityReminder = FirebaseDatabase.getInstance().getReference().child("Physical Activity Reminders");
        appointmentReminder = FirebaseDatabase.getInstance().getReference().child("Appointment Reminders");
        gameReminder = FirebaseDatabase.getInstance().getReference().child("Games Reminders");
        referenceCompanion = FirebaseDatabase.getInstance().getReference().child("Companion");
        referenceProfile = FirebaseDatabase.getInstance().getReference().child("Users");

        int medication = intent.getExtras().getInt("Medication");
        int physical = intent.getExtras().getInt("PhysicalActivity");
        int appointment = intent.getExtras().getInt("Appointment");
        int games = intent.getExtras().getInt("Games");

        requestCode1 = intent.getIntExtra("REQUEST_CODE",-1);
        med_id = intent.getExtras().getString("med_id");
        physical_id = intent.getExtras().getString("physical_id");
        appointment_id = intent.getExtras().getString("appointment_id");
        game_id = intent.getExtras().getString("game_id");
        requestCode2 = intent.getExtras().getInt("request_code");

        // check if the alert receiver's context is medication, physical activity, appointment or games
        if(medication == 1){
            displayMedicineNotification(requestCode2, context);
        }else if(physical == 2){
            displayPhysicalActivityNotification(requestCode2, context);
        }else if(appointment == 3){
            displayAppointmentNotification(requestCode2, context);
        }else if(games == 4){
            displayGameNotification(requestCode2, context);
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

    void displayMedicineNotification(int requestCode, Context context){
        medicationReminder.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds1:snapshot.getChildren()){
                    seniorID = ds1.getKey();
                    medicationReminder.child(mUser.getUid()).child(seniorID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ignored:snapshot.getChildren()){
                                medicationReminder.child(mUser.getUid()).child(seniorID).child(med_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ReadWriteMedication rm = snapshot.getValue(ReadWriteMedication.class);
                                        Long code = rm.getRequestCode();
                                        if(code == requestCode){
                                           String medicine_name = rm.getName();
                                           String dose = rm.getDose();
                                           String pill_shape = rm.getShape();
                                           String pill_color = rm.getColor();

                                           Intent intent = new Intent(context, FirebaseMessagingService.class);
                                           intent.putExtra("pill_shape", pill_shape);
                                           intent.putExtra("pill_color", pill_color);

                                            referenceProfile.child(seniorID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @RequiresApi(api = Build.VERSION_CODES.S)
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                        ReadWriteUserDetails user = snapshot.getValue(ReadWriteUserDetails.class);
                                                        String senior_name = user.getFirstName() + " " + user.getLastName();
                                                        displayMedicine(pill_color, pill_shape);

                                                        medicineNotificationHelper = new MedicineNotificationHelper(context);
                                                        nb = medicineNotificationHelper.getChannelNotification();
                                                        setContentIntent(context, ViewMedicine.class, "key", med_id);
                                                        nb.setSmallIcon(R.drawable.ic_pill2);
                                                        nb.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE);
                                                        nb.setContentTitle("Medicine Reminder");
                                                        nb.setContentText("It's time for your senior " + senior_name + " to take a medicine. " +
                                                                "( Medicine - " + medicine_name + ", Dose " + dose + " )" );

                                                        // retrieve senior's profile picture
                                                        try {
                                                            InputStream in = new URL(user.getImageURL()).openStream();
                                                            bmp = BitmapFactory.decodeStream(in);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();}
                                                        nb.setLargeIcon(GetBitmapClippedCircle(bmp));

                                                        nb.setStyle(new NotificationCompat.BigPictureStyle()
                                                                .setBigContentTitle("Medicine Reminder")
                                                                .bigPicture(BitmapFactory.decodeResource(context.getResources(), pill_shape_color)));


                                                        medicineNotificationHelper.getManager().notify(requestCode1, nb.build());
                                                        context.sendBroadcast(new Intent("NOTIFY_MEDICINE"));
                                                        notificationRingtone(context);

                                                        sendFCMtoSeniorNotification(context, "Medicine Reminder",
                                                                "Time to take your medicine " +
                                                                        medicine_name + ", take " + dose, pill_shape + pill_color );

                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    promptMessage.defaultErrorMessageContext(context);
                                                }
                                            });
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        promptMessage.defaultErrorMessageContext(context);
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            promptMessage.defaultErrorMessageContext(context);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessageContext(context);
            }
        });
    }


    void displayPhysicalActivityNotification(int requestCode, Context context){
        physicalActivityReminder.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds1:snapshot.getChildren()){
                    seniorID = ds1.getKey();
                    physicalActivityReminder.child(mUser.getUid()).child(seniorID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ignored:snapshot.getChildren()){
                                physicalActivityReminder.child(mUser.getUid()).child(seniorID).child(physical_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ReadWritePhysicalActivity rm = snapshot.getValue(ReadWritePhysicalActivity.class);
                                        Long code = rm.getRequestCode();
                                        if(code == requestCode){
                                            String activity_name = rm.getActivity();
                                            String duration = rm.getDuration();

                                            referenceProfile.child(seniorID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                        ReadWriteUserDetails user = snapshot.getValue(ReadWriteUserDetails.class);
                                                        String senior_name = user.getFirstName() + " " + user.getLastName();
                                                        physicalActivityNotificationHelper = new PhysicalActivityNotificationHelper(context);
                                                        nb = physicalActivityNotificationHelper.getChannelNotification();

                                                        setContentIntent(context, ViewPhysicalActivity.class, "key", physical_id);
                                                        nb.setSmallIcon(R.drawable.ic_splash_logo);
                                                        nb.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE);
                                                        nb.setContentTitle("Physical Activity Reminder");
                                                        nb.setContentText("It's time for your senior " + senior_name +  " to do physical activity. " +
                                                               "( " + duration + " " + activity_name + " )");

                                                        // retrieve senior's profile picture
                                                        try {
                                                            InputStream in = new URL(user.getImageURL()).openStream();
                                                            bmp = BitmapFactory.decodeStream(in);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();}
                                                        nb.setLargeIcon(GetBitmapClippedCircle(bmp));

                                                        physicalActivityNotificationHelper.getManager().notify(requestCode1, nb.build());
                                                        context.sendBroadcast(new Intent("NOTIFY_PHYSICAL_ACTIVITY"));
                                                        notificationRingtone(context);

                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    promptMessage.defaultErrorMessageContext(context);
                                                }
                                            });
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        promptMessage.defaultErrorMessageContext(context);
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            promptMessage.defaultErrorMessageContext(context);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessageContext(context);
            }
        });
    }

    void displayAppointmentNotification(int requestCode, Context context){
        appointmentReminder.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds1:snapshot.getChildren()){
                    seniorID = ds1.getKey();
                    appointmentReminder.child(mUser.getUid()).child(seniorID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ignored:snapshot.getChildren()){
                                appointmentReminder.child(mUser.getUid()).child(seniorID).child(appointment_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ReadWriteAppointment rm = snapshot.getValue(ReadWriteAppointment.class);
                                        Long code = rm.getRequestCode();
                                        if(code == requestCode){
                                            String time = rm.getTime();

                                            referenceProfile.child(seniorID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                        ReadWriteUserDetails user = snapshot.getValue(ReadWriteUserDetails.class);
                                                        String senior_name = user.getFirstName() + " " + user.getLastName();
                                                        appointmentNotificationHelper = new AppointmentNotificationHelper(context);
                                                        nb = appointmentNotificationHelper.getChannelNotification();

                                                        setContentIntent(context, ViewAppointment.class, "key", appointment_id);
                                                        nb.setSmallIcon(R.drawable.ic_splash_logo);
                                                        nb.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE);
                                                        nb.setContentTitle("Appointment Reminder");
                                                        nb.setContentText("This is a reminder that your senior " +
                                                                senior_name + "have an appointment scheduled for tomorrow.");
                                                        nb.setStyle(new NotificationCompat.BigTextStyle()
                                                                .bigText("This is a reminder that your senior " +
                                                                        senior_name +
                                                                        " have an appointment scheduled for tomorrow at " + time));

                                                        // retrieve senior's profile picture
                                                        try {
                                                            InputStream in = new URL(user.getImageURL()).openStream();
                                                            bmp = BitmapFactory.decodeStream(in);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();}
                                                        nb.setLargeIcon(GetBitmapClippedCircle(bmp));

                                                        appointmentNotificationHelper.getManager().notify(requestCode1, nb.build());
                                                        context.sendBroadcast(new Intent("NOTIFY_APPOINTMENT"));
                                                        notificationRingtone(context);
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    promptMessage.defaultErrorMessageContext(context);
                                                }
                                            });
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        promptMessage.defaultErrorMessageContext(context);
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            promptMessage.defaultErrorMessageContext(context);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessageContext(context);
            }
        });
    }

    void displayGameNotification(int requestCode, Context context){
        gameReminder.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds1:snapshot.getChildren()){
                    seniorID = ds1.getKey();
                    gameReminder.child(mUser.getUid()).child(seniorID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ignored:snapshot.getChildren()){
                               gameReminder.child(mUser.getUid()).child(seniorID).child(game_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ReadWriteGames rm = snapshot.getValue(ReadWriteGames.class);
                                        Long code = rm.getRequestCode();
                                        if(code == requestCode){
                                            String game = rm.getGame();

                                            referenceProfile.child(seniorID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){

                                                        ReadWriteUserDetails user = snapshot.getValue(ReadWriteUserDetails.class);
                                                        String senior_name = user.getFirstName() + " " + user.getLastName();

                                                        gamesNotificationHelper = new GamesNotificationHelper(context);
                                                        nb = gamesNotificationHelper.getChannelNotification();
                                                        setContentIntent(context, ViewGame.class, "key", game_id);
                                                        nb.setSmallIcon(R.drawable.ic_splash_logo);
                                                        nb.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE);
                                                        nb.setContentTitle("Game Reminder");
                                                        nb.setContentText("It's time for your senior " +
                                                                senior_name + " to Play " + game);
                                                        nb.setStyle(new NotificationCompat.BigTextStyle()
                                                                        .setBigContentTitle("Game Reminder")
                                                                .bigText("\nIt's time for your senior" + senior_name + " to play " + game));

                                                        // retrieve senior's profile picture
                                                        try {
                                                            InputStream in = new URL(user.getImageURL()).openStream();
                                                            bmp = BitmapFactory.decodeStream(in);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();}
                                                        nb.setLargeIcon(GetBitmapClippedCircle(bmp));

                                                        gamesNotificationHelper.getManager().notify(requestCode1, nb.build());
                                                        context.sendBroadcast(new Intent("NOTIFY_GAMES"));
                                                        notificationRingtone(context);

                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    promptMessage.defaultErrorMessageContext(context);
                                                }
                                            });
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        promptMessage.defaultErrorMessageContext(context);
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            promptMessage.defaultErrorMessageContext(context);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessageContext(context);
            }
        });
    }

    static Bitmap GetBitmapClippedCircle(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Path path = new Path();
        path.addCircle(
                (float)(width / 2)
                , (float)(height / 2)
                , (float) Math.min(width, (height / 2))
                , Path.Direction.CCW);

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }

    void sendFCMtoSeniorNotification(Context context, String title, String body, String tag){
        Intent fcm_intent = new Intent(context, FirebaseMessagingService.class);
        fcm_intent.putExtra("Medication",1);
        referenceCompanion.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    seniorID = ds.getKey();
                    referenceProfile.child(seniorID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ReadWriteUserDetails seniorProfile = snapshot.getValue(ReadWriteUserDetails.class);
                            token = seniorProfile.getToken();

                            FcmNotificationsSender notificationsSender =
                                    new FcmNotificationsSender(token, title, body, tag, context);
                           FirebaseMessagingService fs = new FirebaseMessagingService();
                            notificationsSender.SendNotifications();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            promptMessage.defaultErrorMessageContext(context);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessageContext(context);
            }
        });
    }
}

