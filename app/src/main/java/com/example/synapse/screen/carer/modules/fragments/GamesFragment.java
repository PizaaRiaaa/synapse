package com.example.synapse.screen.carer.modules.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.synapse.R;
import com.example.synapse.screen.carer.CarerMainActivity;
import com.example.synapse.screen.carer.modules.view.ViewGame;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.ReplaceFragment;
import com.example.synapse.screen.util.TimePickerFragment;
import com.example.synapse.screen.util.adapter.ItemGames;
import com.example.synapse.screen.util.notifications.AlertReceiver;
import com.example.synapse.screen.util.notifications.FcmNotificationsSender;
import com.example.synapse.screen.util.readwrite.ReadWriteGames;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.example.synapse.screen.util.viewholder.GamesViewHolder;
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
import org.aviran.cookiebar2.CookieBar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GamesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GamesFragment extends Fragment implements AdapterView.OnItemSelectedListener,  TimePickerDialog.OnTimeSetListener{

    // global variables
    PromptMessage promptMessage = new PromptMessage();
    ReplaceFragment replaceFragment = new ReplaceFragment();
    DatabaseReference referenceCompanion, referenceReminders, referenceRequest, referenceProfile;
    AppCompatButton btnMon, btnTue, btnWed, btnThu, btnFri, btnSat, btnSun, btnAddSchedule ;
    AppCompatEditText etDuration;

    FirebaseUser mUser;
    RequestQueue requestQueue;
    int requestCode;
    FloatingActionButton fabAddGame;
    Dialog dialog;
    final Calendar calendar = Calendar.getInstance();
    RecyclerView recyclerView;

    final String[]  GAMES = {"Tic-tac-toe","Trivia Quiz","Math Game"};
    final int [] GAMES_ICS = {R.drawable.ic_tic_tac_toe, R.drawable.ic_trivia_quiz, R.drawable.ic_math_game};

    private TextView tvTime;
    private String selected_game, time, imageURL, token, seniorID;
    private ImageView profilePic;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GamesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GamesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GamesFragment newInstance(String param1, String param2) {
        GamesFragment fragment = new GamesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carer_games, container, false);

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog_box_add_games);
        dialog.getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(getActivity(), R.drawable.dialog_background2));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM;
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation1;

        btnMon = view.findViewById(R.id.btnMON);
        btnTue = view.findViewById(R.id.btnTUE);
        btnWed = view.findViewById(R.id.btnWED);
        btnThu = view.findViewById(R.id.btnTHU);
        btnFri = view.findViewById(R.id.btnFRI);
        btnSat = view.findViewById(R.id.btnSAT);
        btnSun = view.findViewById(R.id.btnSUN);

        // references for firebase
        referenceCompanion = FirebaseDatabase.getInstance().getReference("Companion");
        referenceReminders = FirebaseDatabase.getInstance().getReference("Games Reminders");
        referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        referenceRequest = FirebaseDatabase.getInstance().getReference("Request");

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        requestQueue = Volley.newRequestQueue(getActivity());

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getActivity().registerReceiver(broadcastReceiver, new IntentFilter("NOTIFY_GAMES"));

        Spinner spinner_games;
        tvTime = dialog.findViewById(R.id.tvTime);
        ImageButton ibClose = dialog.findViewById(R.id.ibClose);
        ImageButton ibBack = view.findViewById(R.id.ibBack);
        btnAddSchedule = dialog.findViewById(R.id.btnAddSchedule);
        AppCompatImageButton ibTimePicker = dialog.findViewById(R.id.ibTimePicker);
        profilePic = view.findViewById(R.id.ivCarerProfilePic);

        showUserProfile();
        displayCurrentDay();
        loadScheduleForGames();
        addButton();

        ibClose.setOnClickListener(v -> dialog.dismiss());
        ibBack.setOnClickListener(v -> startActivity(new Intent(getActivity(), CarerMainActivity.class)));

        spinner_games = dialog.findViewById(R.id.spinner_games);
        ItemGames adapter = new ItemGames(getActivity(), GAMES, GAMES_ICS);
        adapter.notifyDataSetChanged();
        spinner_games.setAdapter(adapter);
        spinner_games.setOnItemSelectedListener(this);

        ibTimePicker.setOnClickListener(v -> {
             DialogFragment timePicker = new TimePickerFragment(this::onTimeSet);
             timePicker.show(getChildFragmentManager(), "time picker");
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // recyclerview layout
        recyclerView = view.findViewById(R.id.recyclerview_games);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        // display dialog after floating action button was clicked
        fabAddGame = (FloatingActionButton) view.findViewById(R.id.btnAddGames);
        fabAddGame.setOnClickListener(v -> dialog.show());
    }

    // prevent error when using back pressed inside fragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                this.setEnabled(false);
                replaceFragment.replaceFragment(new HomeFragment(), getActivity());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
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

    // listen if alarm is currently running so we can send notification to senior
  //  BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
  //      @Override
  //      public void onReceive(Context context, Intent intent) {
  //          if (intent != null) {
  //              referenceCompanion.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
  //                  @Override
  //                  public void onDataChange(@NonNull DataSnapshot snapshot) {
  //                      for (DataSnapshot ds : snapshot.getChildren()) {
  //                          seniorID = ds.getKey();

  //                          referenceProfile.child(seniorID).addValueEventListener(new ValueEventListener() {
  //                              @Override
  //                              public void onDataChange(@NonNull DataSnapshot snapshot) {
  //                                  ReadWriteUserDetails seniorProfile = snapshot.getValue(ReadWriteUserDetails.class);
  //                                  token = seniorProfile.getToken();
  //                                  FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token,
  //                                          "Game Reminder",
  //                                          "Hello! It's time for you to play a game",
  //                                          getActivity());
  //                                  notificationsSender.SendNotifications();
  //                              }
  //                              @Override
  //                              public void onCancelled(@NonNull DatabaseError error) {
  //                                  promptMessage.defaultErrorMessage(getActivity());
  //                              }
  //                          });
  //                      }
  //                  }
  //                  @Override
  //                  public void onCancelled(@NonNull DatabaseError error) {
  //                      promptMessage.defaultErrorMessage(getActivity());
  //                  }
  //              });
  //          }
  //      }
  //  };

  //  @Override
  //  public void onDestroy() {
  //      super.onDestroy();
  //      getActivity().unregisterReceiver(broadcastReceiver);
  //  }

    // display all schedules for games
    private void loadScheduleForGames() {
        referenceReminders.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ignored : snapshot.getChildren()) {
                        for (DataSnapshot ds2 : snapshot.getChildren()) {
                            Query query = ds2.getRef();
                            FirebaseRecyclerOptions<ReadWriteGames> options = new FirebaseRecyclerOptions.Builder<ReadWriteGames>().setQuery(query, ReadWriteGames.class).build();
                            FirebaseRecyclerAdapter<ReadWriteGames, GamesViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteGames, GamesViewHolder>(options) {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @SuppressLint("SetTextI18n")
                                @Override
                                protected void onBindViewHolder(@NonNull GamesViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteGames model) {

                                    String game = model.getGame();
                                    switch (game) {
                                        case "Tic-tac-toe":
                                            holder.ivGame.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_tic_tac_toe));
                                            break;
                                        case "Math Game":
                                            holder.ivGame.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_math_game));
                                            break;
                                        case "Trivia Quiz":
                                            holder.ivGame.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_trivia_quiz));
                                    }
                                    holder.gameName.setText(model.getGame());
                                    holder.gameAlarm.setText(model.getTime());

                                    // open medicine's information and send medicine's Key to another activity
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getActivity(), ViewGame.class);
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
                promptMessage.defaultErrorMessage(getActivity());

            }
        });
    }

    // set the alarm manager and listen for broadcast
    void startAlarm(Calendar c, String key) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlertReceiver.class);
        intent.putExtra("Games", 4);
        intent.putExtra("game_id", key);
        intent.putExtra("request_code", requestCode);

        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent
                    .getBroadcast(getActivity(),
                            requestCode,
                            intent,
                            PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_ONE_SHOT);
        } else {
            pendingIntent = PendingIntent
                    .getBroadcast(getActivity(),
                            requestCode,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        // set alarm for everyday
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
        //        calendar.getTimeInMillis(),
        //        AlarmManager.INTERVAL_DAY,
        //        pendingIntent);
    }

    // store schedule for games
    void addSchedule() {
        requestCode = (int)calendar.getTimeInMillis()/1000;
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
                        String key = referenceReminders.push().getKey();
                        referenceReminders.child(seniorID).child(mUser.getUid()).child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    referenceReminders.child(mUser.getUid()).child(seniorID).child(key).setValue(hashMap).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            dialog.dismiss();
                                            tvTime.setVisibility(View.INVISIBLE);
                                            clearDialogText();
                                            Toast.makeText(getActivity(), "Alarm has been set", Toast.LENGTH_SHORT).show();
                                            // start alarm and retrieve the unique id of newly created medicine
                                            // so we can send it to alert receiver.
                                            startAlarm(calendar, key);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(getActivity());
            }
        });
    }

    void addButton(){
        // perform add schedule
        btnAddSchedule.setOnClickListener(v -> {
            // check if carer has already assigned senior in companion node
            referenceCompanion.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.S)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if(tvTime.getText().equals("Add New Game")){
                            Toast.makeText(getActivity(), "Please pick a schedule for the appointment", Toast.LENGTH_SHORT).show();
                        } else {
                            addSchedule();
                        }
                    } else {
                        dialog.dismiss();
                        promptMessage.displayMessage("Failed to set an appointment", "Wait for your senior to accept your request before sending notifications", R.color.red_decline_request, getActivity());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    promptMessage.defaultErrorMessage(getActivity());
                }
            });
        });

    }

    // change the background the current day to white
    void displayCurrentDay(){
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        Calendar calendar = Calendar.getInstance();
        String day = dayFormat.format(calendar.getTime());
        switch (day){
            case "Sunday":
                btnSun.setBackgroundTintList(AppCompatResources.getColorStateList(getActivity(),R.color.white));
                break;
            case "Saturday":
                btnSat.setBackgroundTintList(AppCompatResources.getColorStateList(getActivity(),R.color.white));
                break;
            case "Monday":
                btnMon.setBackgroundTintList(AppCompatResources.getColorStateList(getActivity(),R.color.white));
                break;
            case "Tuesday":
                btnTue.setBackgroundTintList(AppCompatResources.getColorStateList(getActivity(),R.color.white));
                break;
            case "Wednesday":
                btnWed.setBackgroundTintList(AppCompatResources.getColorStateList(getActivity(),R.color.white));
                break;
            case "Thursday":
                btnThu.setBackgroundTintList(AppCompatResources.getColorStateList(getActivity(),R.color.white));
                break;
            case "Friday":
                btnFri.setBackgroundTintList(AppCompatResources.getColorStateList(getActivity(),R.color.white));
                break;
        }
    }

    // display carer's profile pic
    void showUserProfile(){
        referenceProfile.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails userProfile = snapshot.getValue(ReadWriteUserDetails.class);
                if(userProfile != null){
                    Uri uri = mUser.getPhotoUrl();
                    Picasso.get()
                            .load(uri)
                            .transform(new CropCircleTransformation())
                            .into(profilePic);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(getActivity());
            }
        });
    }

    // clear text in dialog box
    void clearDialogText(){
        tvTime.setText("Add New Game");
        tvTime.setTextColor(getResources().getColor(R.color.black));
    }

}