package com.example.synapse.screen.carer.modules;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.synapse.R;
import com.example.synapse.screen.carer.CarerHome;
import com.example.synapse.screen.util.adapter.ItemPhysicalActivityAdapter;
import com.example.synapse.screen.util.viewholder.PhysicalActivityViewHolder;
import com.example.synapse.screen.util.ReadWritePhysicalActivity;
import com.example.synapse.screen.util.ReadWriteUserDetails;
import com.example.synapse.screen.util.TimePickerFragment;
import com.example.synapse.screen.util.notifications.AlertReceiver;
import com.example.synapse.screen.util.notifications.FcmNotificationsSender;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import org.aviran.cookiebar2.CookieBar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class PhysicalActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener{

    private AppCompatButton btnWeek;
    private AppCompatButton btnMonth;
    private AppCompatButton btnYear;
    private AppCompatEditText etDuration;

    private DatabaseReference
            referenceCompanion,
            referenceReminders,
            referenceRequest,
            referenceProfile;

    private FirebaseUser mUser;
    RequestQueue requestQueue;
    private String token;
    private int requestCode;

    private RecyclerView recyclerView;
    private int count = 0;
    private Dialog dialog;
    private TextView tvTime;
    private String time, type_of_activity, seniorID, imageURL;
    private boolean isClicked = false;
    private ImageView profilePic;
    private final Calendar calendar = Calendar.getInstance();
    private final String[] physical_activity = {"Stretching", "Walking","Yoga","Aerobics"};
    private final int [] physical_activity_ics = {R.drawable.ic_stretching, R.drawable.ic_walking,
            R.drawable.ic_yoga, R.drawable.ic_aerobics};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical);

        dialog = new Dialog(PhysicalActivity.this);
        dialog.setContentView(R.layout.custom_dialog_box_add_physical_activity);
        dialog.getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(this, R.drawable.dialog_background2));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM;
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation1;

        referenceCompanion = FirebaseDatabase.getInstance().getReference("Companion");
        referenceReminders = FirebaseDatabase.getInstance().getReference("Physical Activity Reminders");
        referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        referenceRequest = FirebaseDatabase.getInstance().getReference("Request");
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        requestCode = (int)calendar.getTimeInMillis()/1000;
        requestQueue = Volley.newRequestQueue(PhysicalActivity.this);

        // listen for broadcast
        registerReceiver(broadcastReceiver, new IntentFilter("NOTIFY_PHYSICAL_ACTIVITY"));

        // recyclerview layout
        recyclerView = findViewById(R.id.recyclerview_physical_activity);
        recyclerView.setLayoutManager(new GridLayoutManager(PhysicalActivity.this, 2));

        ImageButton ibBack = findViewById(R.id.ibBack);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        FloatingActionButton fabAddPhysicalActivity = findViewById(R.id.btnAddPhysicalActivity);
        Button btnAdd = dialog.findViewById(R.id.btnAdd);
        Button btnMinus = dialog.findViewById(R.id.btnMinus);
        btnWeek = findViewById(R.id.btnThisWeek);
        btnMonth = findViewById(R.id.btnThisMonth);
        btnYear = findViewById(R.id.btnThisYear);
        profilePic = findViewById(R.id.ivCarerProfilePic);
        AppCompatButton btnAddSchedule = dialog.findViewById(R.id.btnAddSchedule);
        tvTime = dialog.findViewById(R.id.tvTime);
        etDuration = dialog.findViewById(R.id.etDuration);
        AppCompatImageButton ibTimePicker = dialog.findViewById(R.id.ibTimePicker);

        // set bottomNavigationView to transparent & show status bar
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ibBack.setOnClickListener(v -> startActivity(new Intent(PhysicalActivity.this, CarerHome.class)));

        Spinner spinner_physical_activity = dialog.findViewById(R.id.spinner_physical_activity);
        ItemPhysicalActivityAdapter adapter = new ItemPhysicalActivityAdapter(PhysicalActivity.this, physical_activity, physical_activity_ics);
        adapter.notifyDataSetChanged();
        spinner_physical_activity.setAdapter(adapter);
        spinner_physical_activity.setOnItemSelectedListener(PhysicalActivity.this);

        // display carer's profile pic
        showCarerProfilePic(mUser.getUid());

        // display schedules for physical activity
        loadScheduleForPhysicalActivity();

        // display dialog
        fabAddPhysicalActivity.setOnClickListener(v -> dialog.show());

        // increment and decrement for number picker
        btnMinus.setOnClickListener(this::decrement);
        btnAdd.setOnClickListener(this::increment);

        // prevent keyboard pop up
        etDuration.setShowSoftInputOnFocus(false);

        // display time picker
        ibTimePicker.setOnClickListener(v -> {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
                isClicked = true;
        });

        // perform add schedule
        btnAddSchedule.setOnClickListener(v -> {
            // check if carer has already assigned senior in companion node
            referenceCompanion.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.S)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String duration = etDuration.getText().toString();
                       if (TextUtils.isEmpty(duration)) {
                           promptMessage("Empty field", "Please enter the duration of the physical activity", R.color.red_decline_request);
                       }else if(!isClicked){
                           promptMessage("Empty field","Please pick a schedule for the physical activity", R.color.red_decline_request);
                       }
                       else {
                           addSchedule();
                       }
                    } else {
                        dialog.dismiss();
                        promptMessage("Failed to set a medicine", "Wait for tour senior to accept your request before sending notifications", R.color.red_decline_request);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    promptMessage("Error","Something went wrong! Please try again.", R.color.red_decline_request);
                }
            });
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinner_physical_activity){
            type_of_activity = physical_activity[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

    private void updateTimeText(Calendar calendar) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("MMMM dd yyyy hh:mm a", Locale.ENGLISH);
        tvTime.setText("Alarm set for " + simpleDateFormat.format(calendar.getTime()));
        time = simpleDateFormat.format(calendar.getTime());
    }

    // set the alarm manager and listen for broadcast
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("PhysicalActivity", 2);

        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, 0);
        }


        //check whether the time is earlier than current time. If so, set it to tomorrow. Otherwise, all alarms for earlier time will fire
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        // alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        // set alarm for everyday
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);

    }

    // listen if alarm is currently running so we can send notification to senior
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
                                            PhysicalActivity.this);
                                    notificationsSender.SendNotifications();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    promptMessage("Error", "Something went wrong! Please try again.",R.color.red_decline_request);
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        promptMessage("Error", "Something went wrong! Please try again.", R.color.red_decline_request);
                    }
                });
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


    // set background for week, month, and year buttons
    public void setBtnBackground(AppCompatButton btn_1, AppCompatButton btn_2, AppCompatButton btn_3){
        btn_1.setBackgroundDrawable(AppCompatResources.getDrawable(this, R.drawable.rounded_button_clicked));
        btn_1.setTextColor(getColor(R.color.physical_activity_add_button));

        btn_2.setBackgroundDrawable(AppCompatResources.getDrawable(this, R.drawable.rounded_button_physical_activity));
        btn_2.setTextColor(getColor(R.color.white));

        btn_3.setBackgroundDrawable(AppCompatResources.getDrawable(this, R.drawable.rounded_button_physical_activity));
        btn_3.setTextColor(getColor(R.color.white));
    }

    // display all schedules for medication
    private void loadScheduleForPhysicalActivity() {
        referenceReminders.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ignored : snapshot.getChildren()) {
                        for (DataSnapshot ds2 : snapshot.getChildren()) {
                            Query query = ds2.getRef();

                            // display all schedule for this current year
                            btnYear.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setBtnBackground(btnYear,btnMonth,btnWeek);
                                    recyclerView.setLayoutManager(new GridLayoutManager(PhysicalActivity.this, 2));
                                    FirebaseRecyclerOptions<ReadWritePhysicalActivity> options = new FirebaseRecyclerOptions.Builder<ReadWritePhysicalActivity>().setQuery(query, ReadWritePhysicalActivity.class).build();
                                    FirebaseRecyclerAdapter<ReadWritePhysicalActivity, PhysicalActivityViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWritePhysicalActivity, PhysicalActivityViewHolder>(options) {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        protected void onBindViewHolder(@NonNull PhysicalActivityViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWritePhysicalActivity model) {
                                            int year = calendar.get(Calendar.YEAR);
                                            String current_year = String.valueOf(year);
                                            String scheduleYear = model.getTime().split(" ")[2];
                                            if(current_year.equals(scheduleYear)){
                                                String activity = model.getActivity();
                                                switch (activity) {
                                                    case "Stretching":
                                                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this, R.drawable.ic_stretching));
                                                        break;
                                                    case "Walking":
                                                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this, R.drawable.ic_walking));
                                                        break;
                                                    case "Yoga":
                                                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this, R.drawable.ic_yoga));
                                                        break;
                                                    case "Aerobics":
                                                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this, R.drawable.ic_aerobics));
                                                        break;
                                                }
                                                holder.name.setText(model.getActivity());
                                                holder.duration.setText("Duration: " + model.getDuration());
                                            }else{
                                                holder.itemView.setVisibility(View.GONE);
                                                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                            }

                                            // open medicine's information and send medicine's Key to another activity
                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(PhysicalActivity.this, ViewPhysicalActivity.class);
                                                    intent.putExtra("userKey", getRef(position).getKey());
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                        @NonNull
                                        @Override
                                        public PhysicalActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_carer_physical_activity_schedule, parent, false);
                                            return new PhysicalActivityViewHolder(view);
                                        }
                                    };
                                    adapter.startListening();
                                    recyclerView.setAdapter(adapter);
                                }
                            });

                            // display all schedule for this current month
                            btnMonth.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setBtnBackground(btnMonth,btnWeek,btnYear);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(PhysicalActivity.this));
                                    FirebaseRecyclerOptions<ReadWritePhysicalActivity> options = new FirebaseRecyclerOptions.Builder<ReadWritePhysicalActivity>().setQuery(query, ReadWritePhysicalActivity.class).build();
                                    FirebaseRecyclerAdapter<ReadWritePhysicalActivity, PhysicalActivityViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWritePhysicalActivity, PhysicalActivityViewHolder>(options) {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        protected void onBindViewHolder(@NonNull PhysicalActivityViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWritePhysicalActivity model) {
                                            String current_month = LocalDate.now().getMonth().name().toUpperCase(Locale.ROOT);
                                            String scheduleMonth = model.getTime().split(" ")[0].toUpperCase(Locale.ROOT);

                                            if(current_month.equals(scheduleMonth)){
                                                String activity = model.getActivity();
                                                switch (activity) {
                                                    case "Stretching":
                                                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this, R.drawable.ic_stretching));
                                                        break;
                                                    case "Walking":
                                                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this, R.drawable.ic_walking));
                                                        break;
                                                    case "Yoga":
                                                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this, R.drawable.ic_yoga));
                                                        break;
                                                    case "Aerobics":
                                                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this, R.drawable.ic_aerobics));
                                                        break;
                                                }
                                                holder.name.setText(model.getActivity());
                                                holder.duration.setText("Duration: " + model.getDuration());
                                            }else{
                                                holder.itemView.setVisibility(View.GONE);
                                                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                            }

                                            // open medicine's information and send medicine's Key to another activity
                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(PhysicalActivity.this, ViewPhysicalActivity.class);
                                                    intent.putExtra("userKey", getRef(position).getKey());
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                        @NonNull
                                        @Override
                                        public PhysicalActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_carer_physical_activity_schedule, parent, false);
                                            return new PhysicalActivityViewHolder(view);
                                        }
                                    };
                                    adapter.startListening();
                                    recyclerView.setAdapter(adapter);
                                }
                            });

                            // display all schedule for this current week
                            btnWeek.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setBtnBackground(btnWeek,btnMonth,btnYear);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(PhysicalActivity.this));
                                    FirebaseRecyclerOptions<ReadWritePhysicalActivity> options = new FirebaseRecyclerOptions.Builder<ReadWritePhysicalActivity>().setQuery(query, ReadWritePhysicalActivity.class).build();
                                    FirebaseRecyclerAdapter<ReadWritePhysicalActivity, PhysicalActivityViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWritePhysicalActivity, PhysicalActivityViewHolder>(options) {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        protected void onBindViewHolder(@NonNull PhysicalActivityViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWritePhysicalActivity model) {
                                            Calendar current_calendar = Calendar.getInstance();
                                            int current_week = current_calendar.get(Calendar.WEEK_OF_YEAR);
                                            int year = current_calendar.get(Calendar.YEAR);
                                            String scheduleYear = model.getTime();
                                            SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
                                            try {
                                                current_calendar.setTime(Objects.requireNonNull(format.parse(scheduleYear)));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            int targetWeek = current_calendar.get(Calendar.WEEK_OF_YEAR);
                                            int targetYear = current_calendar.get(Calendar.YEAR);
                                            if(current_week == targetWeek && year == targetYear) {
                                                String activity = model.getActivity();
                                                switch (activity) {
                                                    case "Stretching":
                                                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this, R.drawable.ic_stretching));
                                                        break;
                                                    case "Pill2":
                                                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this, R.drawable.ic_walking));
                                                        break;
                                                    case "Pill3":
                                                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this, R.drawable.ic_yoga));
                                                        break;
                                                    case "Pill4":
                                                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this, R.drawable.ic_aerobics));
                                                        break;
                                                }
                                                holder.name.setText(model.getActivity());
                                                holder.duration.setText("Duration: " + model.getDuration());

                                            }else{
                                                holder.itemView.setVisibility(View.GONE);
                                                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                            }

                                            // open physical activity's information and send Key to ViewPhysicalActivity
                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(PhysicalActivity.this, ViewPhysicalActivity.class);
                                                    intent.putExtra("userKey", getRef(position).getKey());
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                        @NonNull
                                        @Override
                                        public PhysicalActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_carer_physical_activity_schedule, parent, false);
                                            return new PhysicalActivityViewHolder(view);
                                        }
                                    };
                                    adapter.startListening();
                                    recyclerView.setAdapter(adapter);
                                }
                            });


                            FirebaseRecyclerOptions<ReadWritePhysicalActivity> options = new FirebaseRecyclerOptions.Builder<ReadWritePhysicalActivity>().setQuery(query, ReadWritePhysicalActivity.class).build();
                            FirebaseRecyclerAdapter<ReadWritePhysicalActivity, PhysicalActivityViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWritePhysicalActivity, PhysicalActivityViewHolder>(options) {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @SuppressLint("SetTextI18n")
                                @Override
                                protected void onBindViewHolder(@NonNull PhysicalActivityViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWritePhysicalActivity model) {

                                    String activity = model.getActivity();
                                    switch (activity) {
                                        case "Stretching":
                                            holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this,R.drawable.ic_stretching));
                                            break;
                                        case "Walking":
                                            holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this, R.drawable.ic_walking));
                                            break;
                                        case "Yoga":
                                            holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this, R.drawable.ic_yoga));
                                            break;
                                        case "Aerobics":
                                            holder.ic_activity.setBackground(AppCompatResources.getDrawable(PhysicalActivity.this, R.drawable.ic_aerobics));
                                            break;
                                    }
                                    holder.name.setText(model.getActivity());
                                    holder.duration.setText("Duration: " + model.getDuration());

                                    // open medicine's information and send medicine's Key to another activity
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(PhysicalActivity.this, ViewPhysicalActivity.class);
                                            intent.putExtra("userKey", getRef(position).getKey());
                                            startActivity(intent);
                                        }
                                    });
                                }
                                @NonNull
                                @Override
                                public PhysicalActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_carer_physical_activity_schedule, parent, false);
                                    return new PhysicalActivityViewHolder(view);
                                }
                            };
                            adapter.startListening();
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage("Error", "Something went wrong. Please try again!", R.color.red_decline_request);
            }
        });
    }

    // store schedule for medicine
    private void addSchedule() {

        startAlarm(calendar);

        HashMap<String, Object> hashMap = new HashMap<String, Object>();

        hashMap.put("Activity", type_of_activity);
        hashMap.put("Duration", Objects.requireNonNull(etDuration.getText()).toString());
        hashMap.put("Time", time);
        hashMap.put("RequestCode", requestCode);

        referenceCompanion.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        seniorID = ds.getKey();
                        assert seniorID != null;
                        referenceReminders.child(seniorID).child(mUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    referenceReminders.child(mUser.getUid()).child(seniorID).push().updateChildren(hashMap).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            dialog.dismiss();
                                        }
                                    });
                                }

                                CookieBar.build(PhysicalActivity.this)
                                        .setTitle("Set Medicine")
                                        .setMessage("Alarm has been set")
                                        .setIcon(R.drawable.ic_cookie_check)
                                        .setBackgroundColor(R.color.dark_green)
                                        .setCookiePosition(CookieBar.TOP)
                                        .setDuration(5000)
                                        .show();

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

    // display carer's profile pic
    private void showCarerProfilePic(String firebaseUser){
        // check if carer already send request
        referenceRequest.child(firebaseUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        referenceProfile.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    imageURL = Objects.requireNonNull(snapshot.child("imageURL").getValue()).toString();
                                    Picasso.get()
                                            .load(imageURL)
                                            .fit()
                                            .transform(new CropCircleTransformation())
                                            .into(profilePic);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                promptMessage("Error", "Something went wrong. Please try again!", R.color.red_decline_request);
                            }
                        });
                    }
                }else{
                    // check if senior accepted the carer request
                    referenceCompanion.child(firebaseUser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot ds : snapshot.getChildren()){
                                    referenceProfile.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                imageURL = Objects.requireNonNull(snapshot.child("imageURL").getValue()).toString();
                                                Picasso.get()
                                                        .load(imageURL)
                                                        .fit()
                                                        .transform(new CropCircleTransformation())
                                                        .into(profilePic);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            promptMessage("Error", "Something went wrong. Please try again!", R.color.red_decline_request);
                                        }
                                    });
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

    public void promptMessage(String title, String msg, int background){
        CookieBar.build(PhysicalActivity.this)
                .setTitle(title)
                .setMessage(msg)
                .setCookiePosition(CookieBar.TOP)
                .setBackgroundColor(background)
                .setDuration(4999)
                .show();

    }

}