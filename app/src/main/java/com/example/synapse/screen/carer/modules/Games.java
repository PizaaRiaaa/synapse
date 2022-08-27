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
import com.example.synapse.screen.util.ReadWriteGames;
import com.example.synapse.screen.util.ReadWritePhysicalActivity;
import com.example.synapse.screen.util.ReadWriteUserDetails;
import com.example.synapse.screen.util.TimePickerFragment;
import com.example.synapse.screen.util.adapter.ItemAppointmentSpecialist;
import com.example.synapse.screen.util.adapter.ItemGames;
import com.example.synapse.screen.util.notifications.AlertReceiver;
import com.example.synapse.screen.util.notifications.FcmNotificationsSender;
import com.example.synapse.screen.util.viewholder.GamesViewHolder;
import com.example.synapse.screen.util.viewholder.PhysicalActivityViewHolder;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.aviran.cookiebar2.CookieBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class Games extends AppCompatActivity implements AdapterView.OnItemSelectedListener,  TimePickerDialog.OnTimeSetListener {

    private DatabaseReference
            referenceCompanion,
            referenceReminders,
            referenceRequest,
            referenceProfile;

    private AppCompatButton btnWeek;
    private AppCompatButton btnMonth;
    private AppCompatButton btnYear;
    private AppCompatEditText etDuration;

    private FirebaseUser mUser;
    RequestQueue requestQueue;
    private int requestCode;

    private Dialog dialog;
    private final Calendar calendar = Calendar.getInstance();
    private RecyclerView recyclerView;

    private final String[]  GAMES = {"Tic-tac-toe","Trivia Quiz","Math Game"};
    private final int [] GAMES_ICS = {R.drawable.ic_tic_tac_toe,
            R.drawable.ic_trivia_quiz, R.drawable.ic_math_game};

    private TextView tvTime;
    private String selected_game, time, imageURL, token, seniorID;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);


        dialog = new Dialog(Games.this);
        dialog.setContentView(R.layout.custom_dialog_box_add_games);
        dialog.getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(this, R.drawable.dialog_background2));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM;
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation1;

        // recyclerview layout
        recyclerView = findViewById(R.id.recyclerview_games);
        recyclerView.setLayoutManager(new GridLayoutManager(Games.this, 2));

        // references for firebase
        referenceCompanion = FirebaseDatabase.getInstance().getReference("Companion");
        referenceReminders = FirebaseDatabase.getInstance().getReference("Games Reminders");
        referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        referenceRequest = FirebaseDatabase.getInstance().getReference("Request");

        // listen for broadcast
        registerReceiver(broadcastReceiver, new IntentFilter("NOTIFY_GAMES"));

        // get current user
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        // generate unique alarm id
        requestCode = (int)calendar.getTimeInMillis()/1000;

        // generate volley for sending notification to senior
        requestQueue = Volley.newRequestQueue(Games.this);

        Spinner spinner_games;
        tvTime = dialog.findViewById(R.id.tvTime);
        ImageButton ibClose = dialog.findViewById(R.id.ibClose);
        ImageButton ibBack = findViewById(R.id.ibBack);
        AppCompatButton btnAddSchedule = dialog.findViewById(R.id.btnAddSchedule);
        AppCompatImageButton ibTimePicker = dialog.findViewById(R.id.ibTimePicker);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        FloatingActionButton btnAddAppointment = findViewById(R.id.btnAddGames);
        btnWeek = findViewById(R.id.btnThisWeek);
        btnMonth = findViewById(R.id.btnThisMonth);
        btnYear = findViewById(R.id.btnThisYear);
        profilePic = findViewById(R.id.ivCarerProfilePic);

        // set bottomNavigationView to transparent & show status bar
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // show carer profile pic
        showCarerProfilePic(mUser.getUid());

        // load games schedule
        loadScheduleForGames();

        // display dialog
        btnAddAppointment.setOnClickListener(v -> dialog.show());

        // close dialog
        ibClose.setOnClickListener(v -> dialog.dismiss());

        // redirect user to games screen
        ibBack.setOnClickListener(v -> startActivity(new Intent(Games.this, CarerHome.class)));

        // spinner for games
        spinner_games = dialog.findViewById(R.id.spinner_games);
        ItemGames adapter = new ItemGames(Games.this,
                GAMES, GAMES_ICS);
        adapter.notifyDataSetChanged();
        spinner_games.setAdapter(adapter);
        spinner_games.setOnItemSelectedListener(Games.this);

        // display time picker
        ibTimePicker.setOnClickListener(v -> {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
        });

        // perform add schedule
        btnAddSchedule.setOnClickListener(v -> {
            // check if carer has already assigned senior in companion node
            referenceCompanion.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.S)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if(tvTime.getText().equals("Add New Game")){
                            Toast.makeText(Games.this, "Please pick a schedule for the appointment", Toast.LENGTH_SHORT).show();
                        } else {
                            addSchedule();
                        }
                    } else {
                        dialog.dismiss();
                        promptMessage("Failed to set an appointment", "Wait for your senior to accept your request before sending notifications", R.color.red_decline_request);
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
        if(parent.getId() == R.id.spinner_games)
            selected_game = GAMES[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
        intent.putExtra("Games", 4);

        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, 0);
        }
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
                                            "Game Reminder",
                                            "Hello! It's time for you to play a game",
                                            Games.this);
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
        if(broadcastReceiver != null) unregisterReceiver(broadcastReceiver);
    }

    // display all schedules for games
    private void loadScheduleForGames() {
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
                                    recyclerView.setLayoutManager(new GridLayoutManager(Games.this, 2));
                                    FirebaseRecyclerOptions<ReadWriteGames> options = new FirebaseRecyclerOptions.Builder<ReadWriteGames>().setQuery(query, ReadWriteGames.class).build();
                                    FirebaseRecyclerAdapter<ReadWriteGames, GamesViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteGames, GamesViewHolder>(options) {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        protected void onBindViewHolder(@NonNull GamesViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteGames model) {
                                            int year = calendar.get(Calendar.YEAR);
                                            String current_year = String.valueOf(year);
                                            String scheduleYear = model.getTime().split(" ")[2];
                                            if(current_year.equals(scheduleYear)){
                                                String game = model.getGame();
                                                switch (game) {
                                                    case "Tic-tac-toe":
                                                        holder.ivGame.setBackground(AppCompatResources.getDrawable(Games.this, R.drawable.ic_tic_tac_toe));
                                                        break;
                                                    case "Math Game":
                                                        holder.ivGame.setBackground(AppCompatResources.getDrawable(Games.this, R.drawable.ic_math_game));
                                                        break;
                                                    case "Trivia Quiz":
                                                        holder.ivGame.setBackground(AppCompatResources.getDrawable(Games.this, R.drawable.ic_trivia_quiz));
                                                }
                                                holder.gameName.setText(model.getGame());
                                                holder.gameAlarm.setText(model.getTime());
                                            }else{
                                                holder.itemView.setVisibility(View.GONE);
                                                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                            }

                                            // open medicine's information and send medicine's Key to another activity
                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(Games.this, ViewGame.class);
                                                    intent.putExtra("userKey", getRef(position).getKey());
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                        @NonNull
                                        @Override
                                        public GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_carer_games_schedule, parent, false);
                                            return new GamesViewHolder(view);
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
                                    recyclerView.setLayoutManager(new LinearLayoutManager(Games.this));
                                    FirebaseRecyclerOptions<ReadWriteGames> options = new FirebaseRecyclerOptions.Builder<ReadWriteGames>().setQuery(query, ReadWriteGames.class).build();
                                    FirebaseRecyclerAdapter<ReadWriteGames, GamesViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteGames, GamesViewHolder>(options) {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        protected void onBindViewHolder(@NonNull GamesViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteGames model) {
                                            String current_month = LocalDate.now().getMonth().name().toUpperCase(Locale.ROOT);
                                            String scheduleMonth = model.getTime().split(" ")[0].toUpperCase(Locale.ROOT);

                                            if(current_month.equals(scheduleMonth)){
                                                String game = model.getGame();
                                                switch (game) {
                                                    case "Tic-tac-toe":
                                                        holder.ivGame.setBackground(AppCompatResources.getDrawable(Games.this, R.drawable.ic_tic_tac_toe));
                                                        break;
                                                    case "Math Game":
                                                        holder.ivGame.setBackground(AppCompatResources.getDrawable(Games.this, R.drawable.ic_math_game));
                                                        break;
                                                    case "Trivia Quiz":
                                                        holder.ivGame.setBackground(AppCompatResources.getDrawable(Games.this, R.drawable.ic_trivia_quiz));
                                                }
                                                holder.gameName.setText(model.getGame());
                                                holder.gameAlarm.setText(model.getTime());
                                            }else{
                                                holder.itemView.setVisibility(View.GONE);
                                                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                            }

                                            // open medicine's information and send medicine's Key to another activity
                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(Games.this, ViewGame.class);
                                                    intent.putExtra("userKey", getRef(position).getKey());
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                        @NonNull
                                        @Override
                                        public GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_carer_games_schedule, parent, false);
                                            return new GamesViewHolder(view);
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
                                    recyclerView.setLayoutManager(new LinearLayoutManager(Games.this));
                                    FirebaseRecyclerOptions<ReadWriteGames> options = new FirebaseRecyclerOptions.Builder<ReadWriteGames>().setQuery(query, ReadWriteGames.class).build();
                                    FirebaseRecyclerAdapter<ReadWriteGames, GamesViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteGames, GamesViewHolder>(options) {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        protected void onBindViewHolder(@NonNull GamesViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteGames model) {
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
                                                String game = model.getGame();
                                                switch (game) {
                                                    case "Tic-tac-toe":
                                                        holder.ivGame.setBackground(AppCompatResources.getDrawable(Games.this, R.drawable.ic_tic_tac_toe));
                                                        break;
                                                    case "Math Game":
                                                        holder.ivGame.setBackground(AppCompatResources.getDrawable(Games.this, R.drawable.ic_math_game));
                                                        break;
                                                    case "Trivia Quiz":
                                                        holder.ivGame.setBackground(AppCompatResources.getDrawable(Games.this, R.drawable.ic_trivia_quiz));
                                                }
                                                holder.gameName.setText(model.getGame());
                                                holder.gameAlarm.setText(model.getTime());
                                            }else{
                                                holder.itemView.setVisibility(View.GONE);
                                                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                            }

                                            // open physical activity's information and send Key to ViewPhysicalActivity
                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(Games.this, ViewGame.class);
                                                    intent.putExtra("userKey", getRef(position).getKey());
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                        @NonNull
                                        @Override
                                        public GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_carer_games_schedule, parent, false);
                                            return new GamesViewHolder(view);
                                        }
                                    };
                                    adapter.startListening();
                                    recyclerView.setAdapter(adapter);
                                }
                            });


                            FirebaseRecyclerOptions<ReadWriteGames> options = new FirebaseRecyclerOptions.Builder<ReadWriteGames>().setQuery(query, ReadWriteGames.class).build();
                            FirebaseRecyclerAdapter<ReadWriteGames, GamesViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteGames, GamesViewHolder>(options) {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @SuppressLint("SetTextI18n")
                                @Override
                                protected void onBindViewHolder(@NonNull GamesViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteGames model) {

                                    String game = model.getGame();
                                    switch (game) {
                                        case "Tic-tac-toe":
                                            holder.ivGame.setBackground(AppCompatResources.getDrawable(Games.this, R.drawable.ic_tic_tac_toe));
                                            break;
                                        case "Math Game":
                                            holder.ivGame.setBackground(AppCompatResources.getDrawable(Games.this, R.drawable.ic_math_game));
                                            break;
                                        case "Trivia Quiz":
                                            holder.ivGame.setBackground(AppCompatResources.getDrawable(Games.this, R.drawable.ic_trivia_quiz));
                                    }
                                    holder.gameName.setText(model.getGame());
                                    holder.gameAlarm.setText(model.getTime());

                                    // open medicine's information and send medicine's Key to another activity
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Games.this, ViewGame.class);
                                            intent.putExtra("userKey", getRef(position).getKey());
                                            startActivity(intent);
                                        }
                                    });
                                }
                                @NonNull
                                @Override
                                public GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_carer_games_schedule, parent, false);
                                    return new GamesViewHolder(view);
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

    // set background for week, month, and year buttons
    public void setBtnBackground(AppCompatButton btn_1, AppCompatButton btn_2, AppCompatButton btn_3){
        btn_1.setBackgroundDrawable(AppCompatResources.getDrawable(this, R.drawable.rounded_button_clicked));
        btn_1.setTextColor(getColor(R.color.brown1));

        btn_2.setBackgroundDrawable(AppCompatResources.getDrawable(this, R.drawable.rounded_button_games));
        btn_2.setTextColor(getColor(R.color.white));

        btn_3.setBackgroundDrawable(AppCompatResources.getDrawable(this, R.drawable.rounded_button_games));
        btn_3.setTextColor(getResources().getColor(R.color.white));
    }

    // store schedule for games
    private void addSchedule() {

        startAlarm(calendar);

        HashMap<String, Object> hashMap = new HashMap<String, Object>();

        hashMap.put("Game", selected_game);
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

                                CookieBar.build(Games.this)
                                        .setTitle("Set Game")
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
        CookieBar.build(Games.this)
                .setTitle(title)
                .setMessage(msg)
                .setCookiePosition(CookieBar.TOP)
                .setBackgroundColor(background)
                .setDuration(4999)
                .show();

    }
}