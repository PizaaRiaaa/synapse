package com.example.synapse.screen.carer.modules;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.synapse.R;
import com.example.synapse.screen.util.ReadWriteGames;
import com.example.synapse.screen.util.ReadWriteMedication;
import com.example.synapse.screen.util.ReadWritePhysicalActivity;
import com.example.synapse.screen.util.ReadWriteUserDetails;
import com.example.synapse.screen.util.TimePickerFragment;
import com.example.synapse.screen.util.adapter.ItemGames;
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
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class ViewGame extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener {

    private FirebaseUser mUser;
    private DatabaseReference
            referenceReminders,
            referenceProfile,
            referenceCompanion;

    private final String[]  GAMES = {"Tic-tac-toe","Trivia Quiz","Math Game"};
    private final int [] GAMES_ICS = {R.drawable.ic_tic_tac_toe,
            R.drawable.ic_trivia_quiz, R.drawable.ic_math_game};

    RequestQueue requestQueue;
    ItemGames adapter;
    Spinner spinner_games;

    private Intent intent;
    private final Calendar calendar = Calendar.getInstance();
    private Long request_code;
    int code, requestCode;

    private TextView tvAlarm, tvDelete;
    private ImageView ivGameIC;
    private Dialog dialog;
    private String gameID, selected_game, time,
            seniorID, token, game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_game);

        MaterialButton btnChangeTime = findViewById(R.id.btnChangeSchedule);
        AppCompatButton btnUpdate = findViewById(R.id.btnUpdate);
        ImageButton ibBack = findViewById(R.id.ibBack);
        spinner_games = findViewById(R.id.spinner_games);
        ivGameIC = findViewById(R.id.ivGameIC);
        tvAlarm = findViewById(R.id.tvAlarmSub);
        tvDelete = findViewById(R.id.tvDelete);

        referenceCompanion = FirebaseDatabase.getInstance().getReference("Companion");
        referenceReminders = FirebaseDatabase.getInstance().getReference("Games Reminders");
        referenceProfile = FirebaseDatabase.getInstance().getReference("Users");

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        requestQueue = Volley.newRequestQueue(ViewGame.this);

        // retrieve appointment's ID
        gameID = getIntent().getStringExtra( "userKey");

        // show game information
        showGameInfo(gameID);

        // spinner for games
       adapter = new ItemGames(ViewGame.this,
                GAMES, GAMES_ICS);
        adapter.notifyDataSetChanged();
        spinner_games.setAdapter(adapter);
        spinner_games.setOnItemSelectedListener(ViewGame.this);

        // perform update game
        btnUpdate.setOnClickListener(v -> updateGame(gameID));

        // redirect user to games screen
        ibBack.setOnClickListener(v -> startActivity(new Intent(ViewGame.this, Games.class)));

        // change time
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
            new DatePickerDialog(ViewGame.this, dateSetListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
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

    // set the alarm manager and listen for broadcast
    private void startAlarm(Calendar c) {
        requestCode = (int)calendar.getTimeInMillis()/1000;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("Games",4);

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


    private void updateTimeText(Calendar c) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("MMMM dd yyyy hh:mm a", Locale.ENGLISH);
        tvAlarm.setText(simpleDateFormat.format(calendar.getTime()));
        time = simpleDateFormat.format(calendar.getTime());
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


    //  listen if alarm is currently running so we can send notification to senior
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
                                            ViewGame.this);
                                    notificationsSender.SendNotifications();
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
    };


    public void showGameInfo(String gameID){
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
                                referenceReminders.child(mUser.getUid()).child(key1).child(gameID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @SuppressLint("UseCompatLoadingForDrawables")
                                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            ReadWriteGames readWriteGames = snapshot.getValue(ReadWriteGames.class);
                                            adapter.notifyDataSetChanged();

                                            game = readWriteGames.getGame();
                                            time = readWriteGames.getTime();
                                            tvAlarm.setText(time);

                                            switch(game){
                                                case "Tic-tac-toe":
                                                    displayGameIC(R.drawable.ic_tic_tac_toe,R.drawable.ic_math_game,R.drawable.ic_tic_tac_toe);
                                                    spinner_games.setSelection(0);
                                                    break;
                                                case "Trivia Quiz":
                                                    displayGameIC(R.drawable.ic_tic_tac_toe,R.drawable.ic_math_game,R.drawable.ic_trivia_quiz);
                                                    spinner_games.setSelection(1);
                                                    break;
                                                case "Math Game":
                                                    displayGameIC(R.drawable.ic_tic_tac_toe,R.drawable.ic_math_game,R.drawable.ic_math_game);
                                                    spinner_games.setSelection(2);
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

    public void displayGameIC(int image1, int image2, int image3) {
        switch (game) {
            case "Tic-tac-toe":
                ivGameIC.setBackground(AppCompatResources.getDrawable(this , image1));
                break;
            case "Math Game":
                ivGameIC.setBackground(AppCompatResources.getDrawable(this, image2));
                break;
            case "Trivia Quiz":
                ivGameIC.setBackground(AppCompatResources.getDrawable(this, image3));
                break;
        }
    }

    // update game for both carer and senior nodes
    public void updateGame(String medicineID){

        HashMap<String,Object> hashMap = new HashMap<String, Object>();

        hashMap.put("Game", Objects.requireNonNull(selected_game));
        hashMap.put("Time", tvAlarm.getText().toString());

        referenceReminders.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds1 : snapshot.getChildren()){
                    String seniorID = ds1.getKey();

                    referenceReminders.child(mUser.getUid()).child(seniorID).child(medicineID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ignored : snapshot.getChildren()){
                                ReadWriteGames rw1 = snapshot.getValue(ReadWriteGames.class);
                                assert rw1 != null;
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
                                                        referenceReminders.child(mUser.getUid()).child(seniorID).child(medicineID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        promptMessage("Game Info","Successfully updated the game information", R.color.dark_green);
    }

    // custom prompt message
    public void promptMessage(String title, String msg, int background){
        CookieBar.build(ViewGame.this)
                .setTitle(title)
                .setMessage(msg)
                .setCookiePosition(CookieBar.TOP)
                .setBackgroundColor(background)
                .setDuration(5000)
                .show();
    }
}