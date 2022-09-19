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
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.synapse.R;
import com.example.synapse.screen.carer.modules.view.ViewMedicine;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.ReplaceFragment;
import com.example.synapse.screen.util.TimePickerFragment;
import com.example.synapse.screen.util.notifications.AlertReceiver;
import com.example.synapse.screen.util.notifications.FcmNotificationsSender;
import com.example.synapse.screen.util.notifications.FirebaseMessagingService;
import com.example.synapse.screen.util.readwrite.ReadWriteMedication;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.example.synapse.screen.util.viewholder.MedicationViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
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
import java.util.concurrent.atomic.AtomicInteger;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MedicationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicationFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {

    // global variables
    ReplaceFragment replaceFragment = new ReplaceFragment(); // replacing fragment
    PromptMessage promptMessage = new PromptMessage(); // custom prompt message
    DatabaseReference referenceProfile, referenceCompanion, referenceRequest, referenceReminders;
    FirebaseUser mUser;

    String seniorID, token, pushMedicineID;

    Intent intent;
    RequestQueue requestQueue;
    int requestCode;
    Calendar calendar = Calendar.getInstance();
    AppCompatButton btnMon, btnTue, btnWed,
            btnThu, btnFri, btnSat, btnSun;
    RecyclerView recyclerView;
    TextView tv1, tv2, tv3, tv4, tv5, tv6,
            etDose, etName;
    ShapeableImageView color1, color2, color3, color4, color5, color6;
    ImageView pill1, pill2, pill3, pill4;
    String pillShape = "", color = "", time = "";
    Dialog dialog;
    int count = 0;
    ImageView profilePic;
    boolean isClicked = false;
    FloatingActionButton fabAddMedicine;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MedicationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MedicationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MedicationFragment newInstance(String param1, String param2) {
        MedicationFragment fragment = new MedicationFragment();
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
        View view = inflater.inflate(R.layout.fragment_medication, container, false);

        referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        referenceCompanion = FirebaseDatabase.getInstance().getReference().child("Companion");
        referenceReminders = FirebaseDatabase.getInstance().getReference().child("Medication Reminders");
        referenceRequest = FirebaseDatabase.getInstance().getReference().child("Request");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        requestQueue = Volley.newRequestQueue(getActivity());

        ImageButton ibBack, btnClose;
        MaterialButton ibMinus, ibAdd;
        AppCompatImageButton buttonTimePicker;
        AppCompatButton btnAddSchedule;

        // initialized dialog
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog_box_add_medication);
        dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.dialog_background2));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM;
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation1;

        // ids for dialog
        ibMinus = dialog.findViewById(R.id.ibMinus);
        ibAdd = dialog.findViewById(R.id.ibAdd);
        etDose = dialog.findViewById(R.id.etDose);
        btnClose = dialog.findViewById(R.id.btnClose);
        etName = dialog.findViewById(R.id.etName);
        buttonTimePicker = dialog.findViewById(R.id.ibTimePicker);
        btnAddSchedule = dialog.findViewById(R.id.btnAddSchedule);
        pill1 = dialog.findViewById(R.id.ivPill1);
        pill2 = dialog.findViewById(R.id.ivPill2);
        pill3 = dialog.findViewById(R.id.ivPill3);
        pill4 = dialog.findViewById(R.id.ivPill4);
        color1 = dialog.findViewById(R.id.color1);
        color2 = dialog.findViewById(R.id.color2);
        color3 = dialog.findViewById(R.id.color3);
        color4 = dialog.findViewById(R.id.color4);
        color5 = dialog.findViewById(R.id.color5);
        color6 = dialog.findViewById(R.id.color6);
        tv1 = dialog.findViewById(R.id.tvGreen);
        tv2 = dialog.findViewById(R.id.tvRed);
        tv3 = dialog.findViewById(R.id.tvBrown);
        tv4 = dialog.findViewById(R.id.tvPink);
        tv5 = dialog.findViewById(R.id.tvBlue);
        tv6 = dialog.findViewById(R.id.tvWhite);

        // view ids for this fragment
        ibBack = view.findViewById(R.id.ibBack);
        profilePic = view.findViewById(R.id.ivProfilePic);
        btnMon = view.findViewById(R.id.btnMON);
        btnTue = view.findViewById(R.id.btnTUE);
        btnWed = view.findViewById(R.id.btnWED);
        btnThu = view.findViewById(R.id.btnTHU);
        btnFri = view.findViewById(R.id.btnFRI);
        btnSat = view.findViewById(R.id.btnSAT);
        btnSun = view.findViewById(R.id.btnSUN);

        // uniqueID for scheduling medicine
        requestCode = (int)calendar.getTimeInMillis()/1000;

        // listen for broadcast
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("NOTIFY_MEDICINE"));

        // show status bar
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //show senior profile picture
        showUserProfile();

        // load recyclerview
        LoadScheduleForMedication();

        // check what color shape was clicked
        clickedColorAndShape();

        // change the background of the current day of the week
        displayCurrentDay();

        // close the dialog box
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // prevent keyboard pop up
        etDose.setShowSoftInputOnFocus(false);

        // redirect user to home
        ibBack.setOnClickListener(v -> replaceFragment.replaceFragment(new HomeFragment(), getActivity()));

        // increment and decrement for number picker
        ibMinus.setOnClickListener(this::decrement);
        ibAdd.setOnClickListener(this::increment);

        // display time picker
        buttonTimePicker.setOnClickListener(v -> {
            DialogFragment timePicker = new TimePickerFragment(this::onTimeSet);
            timePicker.show(getChildFragmentManager(), "time picker");
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
                        String pillName = etName.getText().toString();
                        String pillDose = etDose.getText().toString();

                        if (TextUtils.isEmpty(pillName)) {
                            Toast.makeText(getActivity(), "Please enter the name of the medicine", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(pillDose)) {
                            Toast.makeText(getActivity(), "Please enter the dose of the medicine", Toast.LENGTH_SHORT).show();
                        } else if (Objects.equals(pillShape, "")) {
                            Toast.makeText(getActivity(), "Please pick the shape the medicine", Toast.LENGTH_SHORT).show();
                        } else if (Objects.equals(color, "")) {
                            Toast.makeText(getActivity(), "Please pick the color the medicine", Toast.LENGTH_SHORT).show();
                        } else if (!isClicked) {
                            Toast.makeText(getActivity(), "Please pick a schedule for the medicine", Toast.LENGTH_SHORT).show();
                        } else {
                            addSchedule();
                            dialog.dismiss();
                        }
                    } else {
                        dialog.dismiss();
                        promptMessage.displayMessage("Failed to set a medicine", "Wait for your senior to accept your request before sending notifications", R.color.red_decline_request, getActivity());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    promptMessage.defaultErrorMessage(getActivity());
                }
            });
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // layout for recycle view
        recyclerView = view.findViewById(R.id.recyclerview_medication);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        // display dialog after floating action button was clicked
        fabAddMedicine = (FloatingActionButton) view.findViewById(R.id.btnAddMedicine);
        fabAddMedicine.setOnClickListener(v -> dialog.show());
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

    // increment for dose input
    public void increment(View v) {
        count++;
        etDose.setText("");
        etDose.setText("" + count + " pills");
    }

    // decrement for dose input
    public void decrement(View v) {
        if (count <= 0) count = 0;
        else count--;
        etDose.setText("");
        etDose.setText("" + count + " pills");
    }

    // set hour and minute for time picker
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        updateTimeText(calendar);
    }

    // update time textview after time was selected
    private void updateTimeText(Calendar c) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        // tvTime.setText("Alarm set for " + simpleDateFormat.format(calendar.getTime()));
        time = simpleDateFormat.format(calendar.getTime());
    }

    // get the selected time
    public Calendar getCalendar(){
        return calendar;
    }

    // listen if alarm is currently running so we can send notification to senior
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {

                //TODO: sent intent to FirebaseMessagingService so we can change speech text based on the notif type
                Intent fcm_intent = new Intent(getActivity(), FirebaseMessagingService.class);
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
                                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token,
                                            "Medicine Reminder",
                                            "It's time to take your medicine",
                                            getActivity());

                                    notificationsSender.SendNotifications();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    promptMessage.defaultErrorMessage(getActivity());
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        promptMessage.defaultErrorMessage(getActivity());
                    }
                });
            }
        }
    };

    // we need to destroy broadcast if we register one
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    // display all schedules for medication
    private void LoadScheduleForMedication() {
        referenceReminders.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ignored : snapshot.getChildren()) {
                        for (DataSnapshot ds2 : snapshot.getChildren()) {
                            Query query = ds2.getRef();

                            FirebaseRecyclerOptions<ReadWriteMedication> options = new FirebaseRecyclerOptions.Builder<ReadWriteMedication>().setQuery(query, ReadWriteMedication.class).build();
                            FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder>(options) {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @SuppressLint("SetTextI18n")
                                @Override
                                protected void onBindViewHolder(@NonNull MedicationViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteMedication model) {

                                    String pill_shape = model.getShape();
                                    String dose = model.getDose();
                                    String get_dose = dose.split(" ")[0];

                                    switch (pill_shape) {
                                        case "Pill1":
                                            holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill1_white_recycleview));
                                            break;
                                        case "Pill2":
                                            holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill2_white_recycleview));
                                            break;
                                        case "Pill3":
                                            holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill3_white_recycleview));
                                            break;
                                        case "Pill4":
                                            holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill4_white_recycleview));
                                            break;
                                    }

                                    holder.time.setText(model.getTime());
                                    holder.name.setText(model.getName());
                                    if(Objects.equals(get_dose, "1")){
                                        holder.dose.setText(get_dose + " time today");
                                    }else{
                                        holder.dose.setText(get_dose + " times today");
                                    }

                                    // open medicine's information and send medicine's Key to another activity

                                  //  holder.itemView.setOnClickListener(v -> {
                                  //      Bundle bundle = new Bundle();
                                  //      bundle.putString("userKey", getRef(position).getKey());
                                  //      ViewMedicineFragment fragment = new ViewMedicineFragment();
                                  //      fragment.setArguments(bundle);
                                  //      replaceFragment.replaceFragment(new ViewMedicineFragment(), getActivity());
                                  //  });

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getActivity(), ViewMedicine.class);
                                            intent.putExtra("userKey", getRef(position).getKey());
                                            startActivity(intent);
                                        }
                                    });
                                }
                                @NonNull
                                @Override
                                public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_carer_medication_schedule, parent, false);
                                    return new MedicationViewHolder(view);
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

    // store schedule for medicine
    private void addSchedule() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Name", etName.getText().toString());
        hashMap.put("Dose", etDose.getText().toString());
        hashMap.put("Time", time);
        hashMap.put("Shape", pillShape);
        hashMap.put("Color", color);
        hashMap.put("RequestCode", requestCode);
        // check the referenceCompanion node so we can retrieve senior's id
        referenceCompanion.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        seniorID = ds.getKey();
                        assert seniorID != null;
                        // create unique key
                        String key = referenceReminders.push().getKey();
                        referenceReminders.child(seniorID).child(mUser.getUid()).child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    referenceReminders.child(mUser.getUid()).child(seniorID).child(key).setValue(hashMap).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {

                                            //dialog.dismiss();
                                            CookieBar.build(getActivity())
                                                    .setTitle("Set Medicine")
                                                    .setMessage("Alarm has been set")
                                                    .setIcon(R.drawable.ic_cookie_check)
                                                    .setBackgroundColor(R.color.dark_green)
                                                    .setCookiePosition(CookieBar.TOP)
                                                    .setDuration(5000)
                                                    .show();

                                            // start alarm and retrieve the unique id of newly created medicine
                                            // so we can send it to alert receiver. And when user clicked the notification
                                            // user will be redirected to viewMedicine screen while displaying the right information
                                            // for the medicine
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

    // set the alarm manager
    public void startAlarm(Calendar c, String key) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(getActivity(), AlertReceiver.class);
        intent.putExtra("Medication",1);
        intent.putExtra("med_id", key);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(getActivity(), requestCode, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_ONE_SHOT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(getActivity(), requestCode, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
        }

        // check whether the time is earlier than current time. If so, set it to tomorrow.
        // Otherwise, all alarms for earlier time will fire
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        // set alarm for everyday
        // alarmManager.setExact(AlarmManager.RTC_WAKEUP,
        //         calendar.getTimeInMillis() ,
        //         pendingIntent);
    }

    // display carer's profile pic
    private void showUserProfile(){
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

    // change the background the current day to white
    public void displayCurrentDay(){
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

    // check what color and shape of medicine was clicked
    public void clickedColorAndShape(){
        // check what shape was clicked
        pill1.setOnClickListener(v -> {
            pill1.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.rounded_button_pick_role));
            pill2.setBackground(null); pill3.setBackground(null); pill4.setBackground(null);
            pillShape = "Pill1";
        });
        pill2.setOnClickListener(v -> {
            pill2.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.rounded_button_pick_role));
            pill1.setBackground(null); pill3.setBackground(null); pill4.setBackground(null);
            pillShape = "Pill2";
        });
        pill3.setOnClickListener(v -> {
            pill3.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.rounded_button_pick_role));
            pill1.setBackground(null); pill2.setBackground(null); pill4.setBackground(null);
            pillShape = "Pill3";
        });
        pill4.setOnClickListener(v -> {
            pill4.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.rounded_button_pick_role));
            pill1.setBackground(null); pill2.setBackground(null); pill3.setBackground(null);
            pillShape = "Pill4";
        });

        // check what color was clicked
        color1.setOnClickListener(v -> {
            tv1.setTextColor(getActivity().getColor(R.color.grey5)); tv2.setTextColor(getActivity().getColor(R.color.et_stroke));
            tv3.setTextColor(getActivity().getColor(R.color.et_stroke)); tv4.setTextColor(getActivity().getColor(R.color.et_stroke));
            tv5.setTextColor(getActivity().getColor(R.color.et_stroke)); tv6.setTextColor(getActivity().getColor(R.color.et_stroke));
            color = "Green";
        });
        color2.setOnClickListener(v -> {
            tv2.setTextColor(getActivity().getColor(R.color.grey5)); tv1.setTextColor(getActivity().getColor(R.color.et_stroke));
            tv3.setTextColor(getActivity().getColor(R.color.et_stroke)); tv4.setTextColor(getActivity().getColor(R.color.et_stroke));
            tv5.setTextColor(getActivity().getColor(R.color.et_stroke)); tv6.setTextColor(getActivity().getColor(R.color.et_stroke));
            color = "Red";
        });
        color3.setOnClickListener(v -> {
            tv3.setTextColor(getActivity().getColor(R.color.grey5)); tv1.setTextColor(getActivity().getColor(R.color.et_stroke));
            tv2.setTextColor(getActivity().getColor(R.color.et_stroke)); tv4.setTextColor(getActivity().getColor(R.color.et_stroke));
            tv5.setTextColor(getActivity().getColor(R.color.et_stroke)); tv6.setTextColor(getActivity().getColor(R.color.et_stroke));
            color = "Brown";
        });
        color4.setOnClickListener(v -> {
            tv4.setTextColor(getActivity().getColor(R.color.grey5)); tv1.setTextColor(getActivity().getColor(R.color.et_stroke));
            tv2.setTextColor(getActivity().getColor(R.color.et_stroke)); tv3.setTextColor(getActivity().getColor(R.color.et_stroke));
            tv5.setTextColor(getActivity().getColor(R.color.et_stroke)); tv6.setTextColor(getActivity().getColor(R.color.et_stroke));
            color = "Pink";
        });
        color5.setOnClickListener(v -> {
            tv5.setTextColor(getActivity().getColor(R.color.grey5)); tv1.setTextColor(getActivity().getColor(R.color.et_stroke));
            tv2.setTextColor(getActivity().getColor(R.color.et_stroke)); tv3.setTextColor(getActivity().getColor(R.color.et_stroke));
            tv4.setTextColor(getActivity().getColor(R.color.et_stroke)); tv6.setTextColor(getActivity().getColor(R.color.et_stroke));
            color = "Blue";
        });
        color6.setOnClickListener(v -> {
            tv6.setTextColor(getActivity().getColor(R.color.grey5)); tv1.setTextColor(getActivity().getColor(R.color.et_stroke));
            tv2.setTextColor(getActivity().getColor(R.color.et_stroke)); tv3.setTextColor(getActivity().getColor(R.color.et_stroke));
            tv4.setTextColor(getActivity().getColor(R.color.et_stroke)); tv5.setTextColor(getActivity().getColor(R.color.et_stroke));
            color = "White";
        });
    }

}