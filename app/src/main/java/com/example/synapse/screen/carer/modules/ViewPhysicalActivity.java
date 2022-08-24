package com.example.synapse.screen.carer.modules;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.synapse.R;
import com.example.synapse.screen.util.ReadWriteMedication;
import com.example.synapse.screen.util.ReadWritePhysicalActivity;
import com.example.synapse.screen.util.ReadWriteUserDetails;
import com.example.synapse.screen.util.TimePickerFragment;
import com.example.synapse.screen.util.adapter.ItemPhysicalActivityAdapter;
import com.example.synapse.screen.util.notifications.AlertReceiver;
import com.example.synapse.screen.util.notifications.FcmNotificationsSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.aviran.cookiebar2.CookieBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ViewPhysicalActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener {

    private FirebaseUser mUser;
    private DatabaseReference
            referenceReminders,
            referenceProfile,
            referenceCompanion;

    private final String[] PHYSICAL_ACTIVITY_NAME = {"Stretching", "Walking","Yoga","Aerobics"};
    private final int  [] PHYSICAL_ACTIVITY_ICS = {R.drawable.ic_stretching, R.drawable.ic_walking,
            R.drawable.ic_yoga, R.drawable.ic_aerobics};

    private AppCompatEditText etDuration, etDescription;
    private ImageView ivPhysicalActivity;

    private Spinner spinner_physical_activity;
    private TextView tvAlarm, tvDelete;
    private int count = 0;

    private Intent intent;
    private final Calendar calendar = Calendar.getInstance();
    private Long request_code;
    int code, requestCode;

    private  String physical_activity_selected, time, token,
            seniorID, physicalActivityID, activity,
            duration, description;
    RequestQueue requestQueue;
    ItemPhysicalActivityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_physical);

        ImageButton ibBack = findViewById(R.id.ibBack);
        ImageButton btnHelp = findViewById(R.id.ibHelp);
        MaterialButton btnChangeTime = findViewById(R.id.btnChangeSchedule);
        AppCompatButton btnUpdate = findViewById(R.id.btnUpdate);
        tvDelete = findViewById(R.id.tvDelete);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnMinus = findViewById(R.id.btnMinus);
        ivPhysicalActivity = findViewById(R.id.ivPhysicalActivityIC);
        tvAlarm = findViewById(R.id.tvAlarmSub);
        tvDelete = findViewById(R.id.tvDelete);
        etDuration = findViewById(R.id.etDuration);
        etDescription = findViewById(R.id.etDescription);
        spinner_physical_activity = findViewById(R.id.spinner_physical_activity);

        referenceCompanion = FirebaseDatabase.getInstance().getReference("Companion");
        referenceReminders = FirebaseDatabase.getInstance().getReference("Physical Activity Reminders");
        referenceProfile = FirebaseDatabase.getInstance().getReference("Users");

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        requestQueue = Volley.newRequestQueue(ViewPhysicalActivity.this);

        // spinner
        spinner_physical_activity.setOnItemSelectedListener(ViewPhysicalActivity.this);
        adapter = new ItemPhysicalActivityAdapter(ViewPhysicalActivity.this, PHYSICAL_ACTIVITY_NAME, PHYSICAL_ACTIVITY_ICS);
        adapter.notifyDataSetChanged();
        spinner_physical_activity.setAdapter(adapter);

        // retrieve medicine's ID
        physicalActivityID = getIntent().getStringExtra( "userKey");

        // show physical activity information
        showPhysicalActivityInfo(physicalActivityID);

        // update physical activity
        btnUpdate.setOnClickListener(v -> updatePhysicalActivity(physicalActivityID));

        // delete physical activity
        deletePhysicalActivity();

        // back button
        ibBack.setOnClickListener(v -> startActivity(new Intent(ViewPhysicalActivity.this, PhysicalActivity.class)));

        // prevent keyboard pop up
        etDuration.setShowSoftInputOnFocus(false);

        // increment and decrement for number picker
        btnMinus.setOnClickListener(this::decrement);
        btnAdd.setOnClickListener(this::increment);

        // help button
        btnHelp.setOnClickListener(v ->
                promptMessage("To Update","Please Select the text below subtitles to update the context", R.color.dark_green));

        btnChangeTime.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    DialogFragment timePicker = new TimePickerFragment();
                    timePicker.show(getSupportFragmentManager(), "time picker");
                }
            };
            new DatePickerDialog(ViewPhysicalActivity.this, dateSetListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinner_physical_activity)
            physical_activity_selected = PHYSICAL_ACTIVITY_NAME[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //  broadcast to listen if alarm is currently running so we can send notification to senior
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
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
                                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token,
                                            "Physical Activity Reminder",
                                            "It's time to do your physical activity",
                                            ViewPhysicalActivity.this);
                                    notificationsSender.SendNotifications();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    promptMessage("Error", "Something went wrong. Please try again!", R.color.red_decline_request);
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        promptMessage("Error", "Something went wrong. Please try again!", R.color.red_decline_request);
                    }
                });
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null) unregisterReceiver(broadcastReceiver);
    }

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


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        updateTimeText(calendar);
    }

    private void updateTimeText(Calendar c) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("MMMM dd yyyy hh:mm a", Locale.ENGLISH);
        tvAlarm.setText(simpleDateFormat.format(calendar.getTime()));
        time = simpleDateFormat.format(calendar.getTime());
    }

    // set the alarm manager and listen for broadcast
    private void startAlarm(Calendar c) {
        requestCode = (int)calendar.getTimeInMillis()/1000;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("PhysicalActivity", 2);
        PendingIntent pendingIntent;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, 0);
        }
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    public void cancelAlarm(int requestCode){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(this,requestCode, intent, 0);
        }
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
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
                            for(DataSnapshot ds2: snapshot.getChildren()) {

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
                                            description = readWritePhysicalActivity.getDescription();

                                            etDuration.setText(duration);
                                            tvAlarm.setText(time);
                                            if(description != null) etDescription.setText(description);

                                            switch(activity){
                                                case "Stretching":
                                                    displayPhysicalActivityIC(R.drawable.ic_stretching,R.drawable.ic_walking,R.drawable.ic_yoga,
                                                            R.drawable.ic_aerobics);
                                                    spinner_physical_activity.setSelection(0);
                                                    break;
                                                case "Walking":
                                                    displayPhysicalActivityIC(R.drawable.ic_stretching,R.drawable.ic_walking,R.drawable.ic_yoga,
                                                            R.drawable.ic_aerobics);
                                                    spinner_physical_activity.setSelection(1);
                                                    break;
                                                case "Yoga":
                                                    displayPhysicalActivityIC(R.drawable.ic_stretching,R.drawable.ic_walking,R.drawable.ic_yoga,
                                                            R.drawable.ic_aerobics);
                                                    spinner_physical_activity.setSelection(2);
                                                    break;
                                                case "Aerobics":
                                                    displayPhysicalActivityIC(R.drawable.ic_stretching,R.drawable.ic_walking,R.drawable.ic_yoga,
                                                            R.drawable.ic_aerobics);
                                                    spinner_physical_activity.setSelection(3);
                                                    break;
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        promptMessage("Error", "Something went wrong. Please try again!", R.color.red_decline_request);
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            promptMessage("Error", "Something went wrong. Please try again!", R.color.red_decline_request);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage("Error", "Something went wrong. Please try again!", R.color.red_decline_request);
            }
        });
    }

    public void displayPhysicalActivityIC(int image1, int image2, int image3, int image4) {
        switch (activity) {
            case "Stretching":
                ivPhysicalActivity.setBackground(AppCompatResources.getDrawable(this , image1));
                break;
            case "Walking":
                ivPhysicalActivity.setBackground(AppCompatResources.getDrawable(this, image2));
                break;
            case "Yoga":
                ivPhysicalActivity.setBackground(AppCompatResources.getDrawable(this, image3));
                break;
            case "Aerobics":
                ivPhysicalActivity.setBackground(AppCompatResources.getDrawable(this, image4));
                break;
        }
    }

    public void updatePhysicalActivity(String physicalActivityID){

        HashMap<String, Object> hashMap = new HashMap<String, Object>();

        hashMap.put("Activity", physical_activity_selected);
        hashMap.put("Duration", Objects.requireNonNull(etDuration.getText()).toString());
        hashMap.put("RequestCode", requestCode);
        hashMap.put("Time", tvAlarm.getText().toString());
        hashMap.put("Description", Objects.requireNonNull(etDescription.getText()).toString());

        referenceReminders.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds1 : snapshot.getChildren()){
                    String seniorID = ds1.getKey();

                    referenceReminders.child(mUser.getUid()).child(seniorID).child(physicalActivityID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ignored : snapshot.getChildren()){
                                ReadWritePhysicalActivity rw1 = snapshot.getValue(ReadWritePhysicalActivity.class);
                                Long carer_request_code = rw1.getRequestCode();
                                int i = carer_request_code.intValue();

                                String time1 = rw1.getTime();
                                String time2 = tvAlarm.getText().toString();

                                if(!time1.equals(time2)){

                                    // if the alarm was updated by the user, then we need to cancel the old alarm
                                    cancelAlarm(i);

                                    // store the new requestCode
                                    hashMap.put("RequestCode", requestCode);

                                    // start the new alarm
                                    startAlarm(calendar);
                                }

                                referenceReminders.child(seniorID).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds3 : snapshot.getChildren()){
                                            String senior_medicineID  = ds3.getKey();

                                            referenceReminders.child(seniorID).child(mUser.getUid()).child(senior_medicineID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    ReadWriteMedication rw2 = snapshot.getValue(ReadWriteMedication.class);
                                                    Long senior_request_code = rw2.getRequestCode();

                                                    // update both nodes
                                                    if(senior_request_code.equals(carer_request_code)){
                                                        referenceReminders.child(mUser.getUid()).child(seniorID).child(physicalActivityID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                referenceReminders.child(seniorID).child(mUser.getUid()).child(senior_medicineID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }

                                                    promptMessage("Physical Activity Info","Successfully updated the physical activity information", R.color.dark_green);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    promptMessage("Error","Something went wrong! Please try again.", R.color.red_decline_request);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        promptMessage("Error","Something went wrong! Please try again.", R.color.red_decline_request);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            promptMessage("Error","Something went wrong! Please try again.", R.color.red_decline_request);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage("Error","Something went wrong! Please try again.", R.color.red_decline_request);
            }
        });
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

                                // cancel the alarm
                                cancelAlarm(code);

                                referenceReminders.child(mUser.getUid()).child(key).child(physicalActivityID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            referenceReminders.child(key).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for(DataSnapshot ds2: snapshot.getChildren()){
                                                        String medicine_key = ds2.getKey();

                                                        referenceReminders.child(key).child(mUser.getUid()).child(medicine_key).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for(DataSnapshot ignored : snapshot.getChildren()){
                                                                    ReadWriteMedication rw = snapshot.getValue(ReadWriteMedication.class);
                                                                    Long request_code2 = rw.getRequestCode();

                                                                    if (request_code2.equals(request_code)) {
                                                                        referenceReminders.child(key).child(mUser.getUid()).child(medicine_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                startActivity(new Intent(ViewPhysicalActivity.this, PhysicalActivity.class));
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                promptMessage("Error","Something went wrong! Please try again.", R.color.red_decline_request);
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    promptMessage("Error","Something went wrong! Please try again.", R.color.red_decline_request);
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                promptMessage("Error","Something went wrong! Please try again.", R.color.red_decline_request);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    promptMessage("Error","Something went wrong! Please try again.", R.color.red_decline_request);
                }
            });
        });
    }

    // custom prompt message
    public void promptMessage(String title, String msg, int background){
        CookieBar.build(ViewPhysicalActivity.this)
                .setTitle(title)
                .setMessage(msg)
                .setCookiePosition(CookieBar.TOP)
                .setBackgroundColor(background)
                .setDuration(5000)
                .show();
    }
}