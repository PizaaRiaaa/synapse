package com.example.synapse.screen.carer.modules.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.synapse.R;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.TimePickerFragment;
import com.example.synapse.screen.util.readwrite.ReadWritePhysicalActivity;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.example.synapse.screen.util.adapter.ItemPhysicalActivityAdapter;
import com.example.synapse.screen.util.notifications.AlertReceiver;
import com.example.synapse.screen.util.notifications.FcmNotificationsSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class ViewPhysicalActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener {

    private PromptMessage promptMessage = new PromptMessage();
    private FirebaseUser mUser;
    private DatabaseReference
            referenceReminders,
            referenceProfile,
            referenceCompanion;
    RequestQueue requestQueue;

    private final String[] PHYSICAL_ACTIVITY_NAME = {
            "Stretching", "Walking","Yoga","Aerobics"};
    private AppCompatEditText etDuration;
    private TextView tvAlarm, tvDelete;
    private Spinner spinner_physical_activity;
    ItemPhysicalActivityAdapter adapter;
    private final Calendar calendar = Calendar.getInstance();
    private GifImageView gifImageView;
    private int count = 0;
    private Intent intent;
    private Long request_code;
    int code, requestCode;
    String  time, token, key, time1, time2,
            seniorID, physicalActivityID, activity,
            duration, repeatMode, type_of_activity, clickedRepeatBtn;
    private MaterialCardView btn2hoursRepeat, btn4hoursRepeat, btnOnceADay, btnNever;
    TextView tvTime, tv2hours,tv4hours,tvOnceADay, tvNever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_physical);

        ImageButton ibBack = findViewById(R.id.ibBack);
        ImageButton btnHelp = findViewById(R.id.ibHelp);
        MaterialButton btnChangeTime = findViewById(R.id.btnChangeSchedule);
        AppCompatButton btnUpdate = findViewById(R.id.btnUpdate);
        tvDelete = findViewById(R.id.tvDelete);
        MaterialButton btnAdd = findViewById(R.id.btnAdd);
        MaterialButton btnMinus = findViewById(R.id.btnMinus);
        tvAlarm = findViewById(R.id.tvAlarmSub);
        tvDelete = findViewById(R.id.tvDelete);
        etDuration = findViewById(R.id.etDuration);
        spinner_physical_activity = findViewById(R.id.spinner_physical_activity);
        gifImageView = findViewById(R.id.gifImage);
        btn2hoursRepeat = findViewById(R.id.repeat2hours);
        btn4hoursRepeat = findViewById(R.id.repeat4hours);
        btnOnceADay = findViewById(R.id.repeatOnceADay);
        btnNever = findViewById(R.id.repeatNever);
        tv2hours = findViewById(R.id.tv2hours);
        tv4hours = findViewById(R.id.tv4hours);
        tvOnceADay = findViewById(R.id.tvOnceADay);
        tvNever = findViewById(R.id.tvRepeatNever);

        referenceCompanion = FirebaseDatabase.getInstance().getReference("Companion");
        referenceReminders = FirebaseDatabase.getInstance().getReference("Physical Activity Reminders");
        referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        requestQueue = Volley.newRequestQueue(ViewPhysicalActivity.this);

        spinner_physical_activity.setOnItemSelectedListener(ViewPhysicalActivity.this);
        adapter = new ItemPhysicalActivityAdapter(ViewPhysicalActivity.this, PHYSICAL_ACTIVITY_NAME);
        adapter.notifyDataSetChanged();
        spinner_physical_activity.setAdapter(adapter);

        // we need  to check if user clicked the notification
        // then retrieve id first
        // so we can display the right information with the right key
        physicalActivityID = getIntent().getStringExtra( "userKey");
        String key = getIntent().getStringExtra("key");
        if(key != null) showPhysicalActivityInfo(key);
        else showPhysicalActivityInfo(physicalActivityID);

        // back button
        ibBack.setOnClickListener(v -> finish());

        // prevent keyboard pop up
        etDuration.setShowSoftInputOnFocus(false);

        // help button
        btnHelp.setOnClickListener(v -> promptMessage.displayMessage(
                "To Update",
                "Please Select your  desired information",
                R.color.dark_green,
                ViewPhysicalActivity.this));

        btnMinus.setOnClickListener(this::decrement);

        btnAdd.setOnClickListener(this::increment);

        btnUpdate.setOnClickListener(v -> updatePhysicalActivity(physicalActivityID));

        deletePhysicalActivity();

        btn2hoursRepeat.setOnClickListener(v -> {
            clickedRepeatBtn = "2hours";
            displayClickedRepeatButton(btn2hoursRepeat, btn4hoursRepeat, btnOnceADay, btnNever, tv2hours, tv4hours, tvOnceADay, tvNever);
        });
        btn4hoursRepeat.setOnClickListener(v -> {
            clickedRepeatBtn = "4hours";
            displayClickedRepeatButton(btn4hoursRepeat, btn2hoursRepeat, btnOnceADay, btnNever, tv4hours, tv2hours, tvOnceADay, tvNever);
        });
        btnOnceADay.setOnClickListener(v -> {
            clickedRepeatBtn = "OnceADay";
            displayClickedRepeatButton(btnOnceADay, btn2hoursRepeat, btn4hoursRepeat, btnNever, tvOnceADay, tv2hours, tv4hours, tvNever);
        });
        btnNever.setOnClickListener(v -> {
            clickedRepeatBtn = "Never";
            displayClickedRepeatButton(btnNever, btn2hoursRepeat, btn4hoursRepeat, btnOnceADay, tvNever, tv2hours, tv4hours, tvOnceADay);
        });


        btnChangeTime.setOnClickListener(v -> {
                    DialogFragment timePicker = new TimePickerFragment(this::onTimeSet);
                    timePicker.show(getSupportFragmentManager(), "time picker");
        });
    }

    // =============================================================================================

    // change gif based on selected item on spinner
    public void displayPhysicalActivity(int gif1){
        new Handler().postDelayed(() -> {
            gifImageView.setImageResource(gif1);
        }, 200);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinner_physical_activity){
            type_of_activity = PHYSICAL_ACTIVITY_NAME[position];
            if(type_of_activity.equals(PHYSICAL_ACTIVITY_NAME[0])){
                displayPhysicalActivity(R.drawable.stretch5);
            }else if(type_of_activity.equals(PHYSICAL_ACTIVITY_NAME[1])){
                displayPhysicalActivity(R.drawable.walking);
            }else if(type_of_activity.equals(PHYSICAL_ACTIVITY_NAME[2])){
                displayPhysicalActivity(R.drawable.yoga1);
            }else{
                displayPhysicalActivity(R.drawable.aerobics1);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //  broadcast to listen if alarm is currently running so we can send notification to senior
//    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent != null) {
//
//                referenceCompanion.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            seniorID = ds.getKey();
//
//                            referenceProfile.child(seniorID).addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    ReadWriteUserDetails seniorProfile = snapshot.getValue(ReadWriteUserDetails.class);
//                                    token = seniorProfile.getToken();
//                                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token,
//                                            "Physical Activity Reminder",
//                                            "It's time to do your physical activity",
//                                            ViewPhysicalActivity.this);
//                                    notificationsSender.SendNotifications();
//                                }
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//                                    promptMessage.defaultErrorMessage(ViewPhysicalActivity.this);
//                                }
//                            });
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        promptMessage.defaultErrorMessage(ViewPhysicalActivity.this);
//                    }
//                });
//            }
//        }
//    };
//
    // decrement and increment for dose input
    public void increment(View v) {
        count++;
        etDuration.setText("");
        etDuration.setText("" + count + " minutes");
    }

    public void decrement(View v) {
        if (count <= 0) count = 0;
        else count--;
        etDuration.setText("");
        etDuration.setText("" + count + " minutes");
    }

    public void showPhysicalActivityInfo(String physicalActivityID){
        // userID >
        referenceReminders.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds1: snapshot.getChildren()){
                    String key1 = ds1.getKey();

                    // userID > seniorID
                    referenceReminders.child(mUser.getUid()).child(key1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ignored : snapshot.getChildren()) {

                                // userID > seniorID > medicineID
                                referenceReminders.child(mUser.getUid()).child(key1).child(physicalActivityID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @SuppressLint("UseCompatLoadingForDrawables")
                                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            ReadWritePhysicalActivity readWritePhysicalActivity = snapshot.getValue(ReadWritePhysicalActivity.class);
                                            adapter.notifyDataSetChanged();

                                            activity = readWritePhysicalActivity.getActivity();
                                            duration = readWritePhysicalActivity.getDuration();
                                            time = readWritePhysicalActivity.getTime();
                                            repeatMode = readWritePhysicalActivity.getRepeatMode();

                                            etDuration.setText(duration);
                                            tvAlarm.setText(time);

                                            switch (repeatMode){
                                                case "2hours":
                                                    displayClickedRepeatButton(btn2hoursRepeat, btn4hoursRepeat, btnOnceADay, btnNever, tv2hours, tv4hours, tvOnceADay, tvNever);
                                                       break;
                                                case "4hours":
                                                    displayClickedRepeatButton(btn4hoursRepeat, btn2hoursRepeat, btnOnceADay, btnNever, tv4hours, tv2hours, tvOnceADay, tvNever);
                                                    break;
                                                case "OnceADay":
                                                    displayClickedRepeatButton(btnOnceADay, btn2hoursRepeat, btn4hoursRepeat, btnNever, tvOnceADay, tv2hours, tv4hours, tvNever);
                                                    break;
                                                case "Never":
                                                    displayClickedRepeatButton(btnNever, btn2hoursRepeat, btn4hoursRepeat, btnOnceADay, tvNever, tv2hours, tv4hours, tvOnceADay);
                                                    break;
                                            }

                                            switch(activity){
                                                case "Stretching":
                                                    spinner_physical_activity.setSelection(0);
                                                    displayPhysicalActivity(R.drawable.stretch5);
                                                    break;
                                                case "Walking":
                                                    spinner_physical_activity.setSelection(1);
                                                    displayPhysicalActivity(R.drawable.walking);
                                                    break;
                                                case "Yoga":
                                                    displayPhysicalActivity(R.drawable.yoga1);
                                                    break;
                                                case "Aerobics":
                                                    spinner_physical_activity.setSelection(3);
                                                    displayPhysicalActivity(R.drawable.aerobics1);
                                                    break;
                                           }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        promptMessage.defaultErrorMessage(ViewPhysicalActivity.this);
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            promptMessage.defaultErrorMessage(ViewPhysicalActivity.this);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(ViewPhysicalActivity.this);
            }
        });
    }

    public void updatePhysicalActivity(String physicalActivityID){
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Activity", type_of_activity);
        hashMap.put("Duration", Objects.requireNonNull(etDuration.getText()).toString());
        hashMap.put("Time", tvAlarm.getText().toString());

        referenceReminders.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds1 : snapshot.getChildren()){
                    String seniorID = ds1.getKey();

                    referenceReminders.child(mUser.getUid()).child(seniorID).child(physicalActivityID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ignored : snapshot.getChildren()) {
                                ReadWritePhysicalActivity rw1 = snapshot.getValue(ReadWritePhysicalActivity.class);
                                Long carer_request_code = rw1.getRequestCode();
                                int int_request_code = rw1.getRequestCode().intValue();

                                time1 = rw1.getTime();
                                time2 = tvAlarm.getText().toString();
                                if (!time1.equals(time2)) {

                                    // if the alarm was updated by the user, then we need to cancel the old alarm
                                    cancelAlarm(int_request_code);

                                    // store the new requestCode
                                    hashMap.put("RequestCode", requestCode);

                                    // start the new alarm
                                    startAlarm(calendar, key);
                                }

                                referenceReminders.child(seniorID).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds3 : snapshot.getChildren()) {
                                            String senior_physicalID = ds3.getKey();

                                            referenceReminders.child(seniorID).child(mUser.getUid()).child(senior_physicalID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    ReadWritePhysicalActivity rw2 = snapshot.getValue(ReadWritePhysicalActivity.class);
                                                    Long senior_request_code = rw2.getRequestCode();

                                                    // update both nodes
                                                    if (senior_request_code.equals(carer_request_code)) {
                                                        referenceReminders.child(mUser.getUid()).child(seniorID).child(physicalActivityID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                referenceReminders.child(seniorID).child(mUser.getUid()).child(senior_physicalID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    promptMessage.defaultErrorMessage(ViewPhysicalActivity.this);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        promptMessage.defaultErrorMessage(ViewPhysicalActivity.this);
                                    }
                                });
                                }
                            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            promptMessage.defaultErrorMessage(ViewPhysicalActivity.this);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(ViewPhysicalActivity.this);

            }
        });

        promptMessage.displayMessage(
                "Physical Activity Info",
                "Successfully updated the physical activity information",
                R.color.dark_green,
                ViewPhysicalActivity.this);
    }

    // delete medicine for both carer and senior nodes
    public void deletePhysicalActivity(){
        tvDelete.setOnClickListener(v -> {
            referenceReminders.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds1: snapshot.getChildren()){
                        String key = ds1.getKey();

                        referenceReminders.child(mUser.getUid()).child(key).child(physicalActivityID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ReadWritePhysicalActivity readWritePhysicalActivity = snapshot.getValue(ReadWritePhysicalActivity.class);
                                request_code = readWritePhysicalActivity.getRequestCode();
                                code = request_code.intValue();
                                cancelAlarm(code);

                                referenceReminders.child(mUser.getUid()).child(key).child(physicalActivityID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){
                                            referenceReminders.child(key).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for(DataSnapshot ds2: snapshot.getChildren()){
                                                        String activity_key = ds2.getKey();

                                                        referenceReminders.child(key).child(mUser.getUid()).child(activity_key).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for(DataSnapshot ignored : snapshot.getChildren()){
                                                                    ReadWritePhysicalActivity rw = snapshot.getValue(ReadWritePhysicalActivity.class);
                                                                    Long request_code2 = rw.getRequestCode();
                                                                    if (request_code2.equals(request_code)) {

                                                                        referenceReminders.child(key).child(mUser.getUid()).child(activity_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                finish();
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                promptMessage.defaultErrorMessage(ViewPhysicalActivity.this);
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    promptMessage.defaultErrorMessage(ViewPhysicalActivity.this);
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                promptMessage.defaultErrorMessage(ViewPhysicalActivity.this);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    promptMessage.defaultErrorMessage(ViewPhysicalActivity.this);
                }
            });
        });
    }

    // set the alarm manager and listen for broadcast
    private void startAlarm(Calendar c, String key) {
        requestCode = (int)calendar.getTimeInMillis()/1000;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("PhysicalActivity", 2);
        intent.putExtra("physical_view_id", key);

        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_ONE_SHOT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
        }

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        // set alarm for everyday
        // alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
        //         calendar.getTimeInMillis(),
        //         AlarmManager.INTERVAL_DAY,
        //         pendingIntent);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        updateTimeText(calendar);
    }

    private void updateTimeText(Calendar c) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        tvAlarm.setText(simpleDateFormat.format(calendar.getTime()));
        time = simpleDateFormat.format(calendar.getTime());
    }


    public void cancelAlarm(int requestCode){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_ONE_SHOT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
        }

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    // change background of clicked repeat's button and textview
    public void displayClickedRepeatButton(MaterialCardView btnClicked, MaterialCardView btn1,
                                           MaterialCardView btn2, MaterialCardView btn3, TextView tvClicked, TextView tv1, TextView tv2, TextView tv3){
        btnClicked.setCardBackgroundColor(getResources().getColor(R.color.dark_violet));
        tvClicked.setTextColor(getResources().getColor(R.color.white));
        btn1.setCardBackgroundColor(getResources().getColor(R.color.grey2));
        btn2.setCardBackgroundColor(getResources().getColor(R.color.grey2));
        btn3.setCardBackgroundColor(getResources().getColor(R.color.grey2));
        tv1.setTextColor(getResources().getColor(R.color.grey1));
        tv2.setTextColor(getResources().getColor(R.color.grey1));
        tv3.setTextColor(getResources().getColor(R.color.grey1));
    }

}