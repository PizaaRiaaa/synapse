package com.example.synapse.screen.carer.modules.fragments;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.example.synapse.screen.carer.modules.view.ViewAppointment;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.ReplaceFragment;
import com.example.synapse.screen.util.TimePickerFragment;
import com.example.synapse.screen.util.adapter.ItemAppointmentSpecialist;
import com.example.synapse.screen.util.adapter.ItemAppointmentType;
import com.example.synapse.screen.util.notifications.AlertReceiver;
import com.example.synapse.screen.util.notifications.FcmNotificationsSender;
import com.example.synapse.screen.util.readwrite.ReadWriteAppointment;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.example.synapse.screen.util.viewholder.AppointmentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
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
 * Use the {@link AppointmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppointmentFragment extends Fragment  implements AdapterView.OnItemSelectedListener,  TimePickerDialog.OnTimeSetListener{

    // global variables
    PromptMessage promptMessage = new PromptMessage();
    ReplaceFragment replaceFragment = new ReplaceFragment();
    DatabaseReference referenceCompanion, referenceReminders, referenceRequest, referenceProfile;

    final String[] APPOINTMENT_SPECIALIST =
            {"Geriatrician","General Doctor","Cardiologist","Rheumatologist","Urologist",
            "Ophthalmologist","Dentist","Psychologist","Audiologist"};

    final int [] APPOINTMENT_SPECIALIST_ICS =
            {R.drawable.ic_geriatrician, R.drawable.ic_doctor, R.drawable.ic_cardiologist,
            R.drawable.ic_rheumatologist,R.drawable.ic_urologist, R.drawable.ic_ophthalmologist,
            R.drawable.ic_dentist,R.drawable.ic_psychologist,R.drawable.ic_audiologist};

    final String[]  APPOINTMENT_TYPE = {"In Person","Online"};
    final int [] APPOINTMENT_TYPE_ICS = {R.drawable.ic_in_person, R.drawable.ic_online};
    final Calendar calendar = Calendar.getInstance();

    AppCompatButton btnMon, btnTue, btnWed, btnThu, btnFri, btnSat, btnSun, btnAddSchedule;
    String time, selected_specialist, token, selected_appointment_type, seniorID, date;
    FirebaseUser mUser;
    RequestQueue requestQueue;
    int requestCode;

    RecyclerView recyclerView;
    Dialog dialog;
    TextView tvTime;
    TextInputEditText etDrName, etConcern;
    ImageView profilePic;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AppointmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AppointmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppointmentFragment newInstance(String param1, String param2) {
        AppointmentFragment fragment = new AppointmentFragment();
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
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog_box_add_appointment);
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
        referenceReminders = FirebaseDatabase.getInstance().getReference("Appointment Reminders");
        referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        referenceRequest = FirebaseDatabase.getInstance().getReference("Request");

        // listen for broadcast
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("NOTIFY_APPOINTMENT"));

        // get current user
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        // generate volley for sending notification to senior
        requestQueue = Volley.newRequestQueue(getActivity());

        // set recyclerview
        recyclerView = view.findViewById(R.id.recyclerview_appointment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ImageButton ibBack = view.findViewById(R.id.ibBack);
        ImageButton ibClose = dialog.findViewById(R.id.ibClose);
        AppCompatImageButton ibTimePicker = dialog.findViewById(R.id.ibTimePicker);
        btnAddSchedule = dialog.findViewById(R.id.btnAddSchedule);
        FloatingActionButton btnAddAppointment = view.findViewById(R.id.btnAddAppointment);
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
        Spinner spinner_appointment_specialist, spinner_appointment_type;
        etDrName = dialog.findViewById(R.id.etDrName);
        etConcern = dialog.findViewById(R.id.etConcern);
        tvTime = dialog.findViewById(R.id.tvTime);
        profilePic = view.findViewById(R.id.ivCarerProfilePic);

        // show status bar
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        showUserProfile();
        loadScheduleForAppointments();
        displayCurrentDay();
        addButton();

        ibBack.setOnClickListener(v -> startActivity(new Intent(getActivity(), CarerMainActivity.class)));
        btnAddAppointment.setOnClickListener(v -> dialog.show());
        ibClose.setOnClickListener(v -> dialog.dismiss());

        spinner_appointment_specialist = dialog.findViewById(R.id.spinner_appointment_specialist);
        ItemAppointmentSpecialist adapter1 = new ItemAppointmentSpecialist(getActivity(),
                APPOINTMENT_SPECIALIST, APPOINTMENT_SPECIALIST_ICS);
        adapter1.notifyDataSetChanged();
        spinner_appointment_specialist.setAdapter(adapter1);
        spinner_appointment_specialist.setOnItemSelectedListener(this);

        spinner_appointment_type = dialog.findViewById(R.id.spinner_appointment_type);
        ItemAppointmentType adapter2 = new ItemAppointmentType(getActivity(),
                APPOINTMENT_TYPE, APPOINTMENT_TYPE_ICS);
        adapter2.notifyDataSetChanged();
        spinner_appointment_type.setAdapter(adapter2);
        spinner_appointment_type.setOnItemSelectedListener(this);

        ibTimePicker.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                DialogFragment timePicker = new TimePickerFragment(this);
                timePicker.show(getChildFragmentManager(), "time picker");
            };
            new DatePickerDialog(getActivity(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        return view;
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

    // get the current selected item in spinners
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinner_appointment_specialist){
            selected_specialist = APPOINTMENT_SPECIALIST[position];
        }else if(parent.getId() == R.id.spinner_appointment_type){
            selected_appointment_type = APPOINTMENT_TYPE[position];
        }
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
        tvTime.setText(simpleDateFormat.format(calendar.getTime()));
        tvTime.setTextColor(getResources().getColor(R.color.black));
        time = simpleDateFormat.format(calendar.getTime());
    }

    // get the selected time
    Calendar getCalendar(){
        return calendar;
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
                                            "Appointment Reminder",
                                            "This is a reminder that you have an appointment scheduled for tomorrow ",
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    // set the alarm manager and listen for broadcast
    private void startAlarm(Calendar c, String key) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlertReceiver.class);
        intent.putExtra("Appointment", 3);
        intent.putExtra("appointment_id", key);
        intent.putExtra("request_code", requestCode);

        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent =
                    PendingIntent.getBroadcast(getActivity(),
                            requestCode,
                            intent,
                            PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_ONE_SHOT);
        } else {
            pendingIntent =
                    PendingIntent.getBroadcast(getActivity(),
                            requestCode,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
        }

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        // notify a day before the appointment
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() - (86400000) , pendingIntent);
    }

    // store schedule for appointment
    private void addSchedule() {
        requestCode = (int) getCalendar().getTimeInMillis()/1000;
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Specialist", selected_specialist);
        hashMap.put("AppointmentType", selected_appointment_type);
        hashMap.put("Time", time);
        hashMap.put("DrName", Objects.requireNonNull(etDrName.getText()).toString());
        hashMap.put("Concern", Objects.requireNonNull(etConcern.getText()).toString());
        hashMap.put("RequestCode", requestCode);

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
                                            dialog.dismiss();
                                            clearDialogText();
                                            Toast.makeText(getActivity(), "Alarm has been set", Toast.LENGTH_SHORT).show();
                                            // start alarm and retrieve the unique id of newly created medicine
                                            // so we can send it to alert receiver.
                                            startAlarm(getCalendar(), key);
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

    // store schedule when add button was clicked
    void addButton(){
        // perform add schedule
        btnAddSchedule.setOnClickListener(v -> {
            // check if carer has already assigned senior in companion node
            referenceCompanion.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.S)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if(tvTime.getText().equals("Add New Appointment")){
                            Toast.makeText(getActivity(), "Please pick a schedule for the appointment", Toast.LENGTH_SHORT).show();
                        } else {
                            addSchedule();
                        }
                    } else {
                        addSchedule();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        if (Build.VERSION.SDK_INT >= 26) {ft.setReorderingAllowed(false);}
                        ft.detach(AppointmentFragment.this).attach(AppointmentFragment.this).commit();
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

    // display all schedules for appointments
    private void loadScheduleForAppointments() {
        referenceReminders.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ignored : snapshot.getChildren()) {
                        for (DataSnapshot ds2 : snapshot.getChildren()) {
                            Query query = ds2.getRef();

                            FirebaseRecyclerOptions<ReadWriteAppointment> options = new FirebaseRecyclerOptions.Builder<ReadWriteAppointment>().setQuery(query, ReadWriteAppointment.class).build();
                            FirebaseRecyclerAdapter<ReadWriteAppointment, AppointmentViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteAppointment, AppointmentViewHolder>(options) {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @SuppressLint("SetTextI18n")
                                @Override
                                protected void onBindViewHolder(@NonNull AppointmentViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteAppointment model) {

                                    String specialist = model.getSpecialist();
                                    switch (specialist) {
                                        case "General Doctor":
                                            holder.ivSpecialist.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_doctor));
                                            break;
                                        case "Geriatrician":
                                            holder.ivSpecialist.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_geriatrician));
                                            break;
                                        case "Cardiologist":
                                            holder.ivSpecialist.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_cardiologist));
                                            break;
                                        case "Rheumatologist":
                                            holder.ivSpecialist.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_rheumatologist));
                                            break;
                                        case "Urologist":
                                            holder.ivSpecialist.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_urologist));
                                            break;
                                        case "Ophthalmologist":
                                            holder.ivSpecialist.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_ophthalmologist));
                                            break;
                                        case "Dentist":
                                            holder.ivSpecialist.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_dentist));
                                            break;
                                        case "Psychologist":
                                            holder.ivSpecialist.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_psychologist));
                                            break;
                                        case "Audiologist":
                                            holder.ivSpecialist.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_audiologist));
                                            break;
                                    }

                                    holder.dateAndTime.setText(model.getTime());

                                    if(!model.getDrName().split(" ")[0].equals("Dr.")){
                                        holder.doctorName.setText("Dr. " + model.getDrName());
                                    }else{
                                        holder.doctorName.setText(model.getDrName());
                                    }

                                    holder.doctorSpecialist.setText(model.getSpecialist());
                                    // open medicine's information and send medicine's Key to another activity
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getActivity(), ViewAppointment.class);
                                            intent.putExtra("userKey", getRef(position).getKey());
                                            startActivity(intent);
                                        }
                                    });
                                }
                                @NonNull
                                @Override
                                public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_carer_appointment, parent, false);
                                    return new AppointmentViewHolder(view);
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
            }
        });
    }

    // display carer's profile pic
    private void showUserProfile(){
        // display carer profile pic
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
        etDrName.setText("");
        etConcern.setText("");
        tvTime.setText("Add New Appointment");
        tvTime.setTextColor(getResources().getColor(R.color.grey5));
    }

}