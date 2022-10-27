package com.example.synapse.screen.carer.modules.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import android.preference.PreferenceManager;
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
import com.example.synapse.screen.util.readwrite.ReadWriteMedication;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.example.synapse.screen.util.viewholder.MedicationViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MedicationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicationFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {

    // Global variables
    ReplaceFragment replaceFragment = new ReplaceFragment(); // replacing fragment
    PromptMessage promptMessage = new PromptMessage(); // custom prompt message
    Calendar calendar;

    DatabaseReference referenceCarer, referenceReminders;
    FirebaseUser mUser;
    String seniorID;

    AppCompatButton btnMon, btnTue, btnWed, btnThu, btnFri, btnSat, btnSun;
    TextView tv1, tv2, tv3, tv4, tv5, tv6, etDose, etName, tvTime;
    ShapeableImageView color1, color2, color3, color4, color5, color6;

    Intent intent;
    RequestQueue requestQueue;
    AppCompatButton btnAddSchedule;
    RecyclerView recyclerView;
    ImageView pill1, pill2, pill3, pill4;
    String pillShape = "", color = "", time = "", key;
    Dialog dialog;
    ImageView profilePic;
    FloatingActionButton fabAddMedicine;

    boolean isClicked = false;
    int count = 0;
    int requestCode;

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
        View view = inflater.inflate(R.layout.fragment_carer_medication, container, false);

        //getActivity().registerReceiver(broadcastReceiver, new IntentFilter("NOTIFY_MEDICINE"));
        //requestQueue = Volley.newRequestQueue(getActivity());

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        referenceCarer = FirebaseDatabase.getInstance().getReference("Users").child("Carers");
        referenceReminders = FirebaseDatabase.getInstance().getReference().child("Medication Reminders");
        requestQueue = Volley.newRequestQueue(getActivity());
        calendar = Calendar.getInstance();
        ImageButton ibBack, btnClose;
        MaterialButton ibMinus, ibAdd;
        AppCompatImageButton buttonTimePicker;

        // initialized dialog
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog_box_add_medication);
        dialog.getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(getActivity(),R.drawable.dialog_background2));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM;
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation1;

        // ids for dialog
        tvTime = dialog.findViewById(R.id.tvTime);
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

        showUserProfile();
        displayCurrentDay();
        LoadScheduleForMedication();
        clickedColorAndShape();
        addButton();

        ibBack.setOnClickListener(v -> replaceFragment.replaceFragment(new HomeFragment(), getActivity()));
        btnClose.setOnClickListener(v -> dialog.dismiss());
        etDose.setShowSoftInputOnFocus(false);
        ibMinus.setOnClickListener(this::decrement);
        ibAdd.setOnClickListener(this::increment);

        buttonTimePicker.setOnClickListener(v -> {
            DialogFragment timePicker = new TimePickerFragment(this);
            timePicker.show(getChildFragmentManager(), "time picker");
            isClicked = true;
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
    void increment(View v) {
        count++;
        etDose.setText("");
        etDose.setText("" + count + " pills");
    }

    // decrement for dose input
    void decrement(View v) {
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
    void updateTimeText(Calendar c) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        tvTime.setVisibility(View.VISIBLE);
        tvTime.setText(simpleDateFormat.format(calendar.getTime()));
        time = simpleDateFormat.format(calendar.getTime());
    }

    // get the selected time
   Calendar getCalendar(){
        return calendar;
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    // display all schedules for medication
    void LoadScheduleForMedication() {
        referenceReminders.child(getDefaults("seniorKey",getActivity())).addValueEventListener(new ValueEventListener() {
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

    // set the alarm manager
    void startAlarm(Calendar c, String key) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(getActivity(), AlertReceiver.class);
        intent.putExtra("Medication",1);
        intent.putExtra("med_id", key);
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

    // store schedule for medicine
    void addSchedule() {
        requestCode = (int) getCalendar().getTimeInMillis()/1000;
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Name", etName.getText().toString());
        hashMap.put("Dose", etDose.getText().toString());
        hashMap.put("Time", time);
        hashMap.put("Shape", pillShape);
        hashMap.put("Color", color);
        hashMap.put("RequestCode", requestCode);

        String key = referenceReminders.push().getKey();
        referenceReminders
                .child(getDefaults("seniorKey",getActivity()))
                .child(mUser.getUid())
                .child(key)
                .setValue(hashMap).addOnCompleteListener(new OnCompleteListener() {

            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    referenceReminders
                            .child(mUser.getUid())
                            .child(getDefaults("seniorKey",getActivity()))
                            .child(key)
                            .setValue(hashMap).addOnCompleteListener(task0 -> {

                        if (task0.isSuccessful()) {
                            dialog.dismiss();
                            tvTime.setVisibility(View.INVISIBLE);
                            clearDialogText();
                            promptMessage.displayMessage(
                                    "Success",
                                    "Alarm has been set successfully",
                                    R.color.dark_green, getActivity());
                            // start alarm and retrieve the unique id of newly created medicine
                            // so we can send it to alert receiver.
                            startAlarm(getCalendar(), key);
                        }
                    });
                }
            }
        });
    }

    // store schedule when add button was clicked
    void addButton(){
        btnAddSchedule.setOnClickListener(v -> {
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
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(MedicationFragment.this).attach(MedicationFragment.this).commit();
            }
        });
    }

    // display carer's profile pic
    void showUserProfile(){
        referenceCarer.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

    // check what color and shape of medicine was clicked
    void clickedColorAndShape(){
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

    // clear text in dialog box
    void clearDialogText(){
        tv1.setTextColor(getActivity().getColor(R.color.et_stroke));
        tv2.setTextColor(getActivity().getColor(R.color.et_stroke));
        tv3.setTextColor(getActivity().getColor(R.color.et_stroke));
        tv4.setTextColor(getActivity().getColor(R.color.et_stroke));
        tv5.setTextColor(getActivity().getColor(R.color.et_stroke));
        tv6.setTextColor(getActivity().getColor(R.color.et_stroke));
        pill1.setBackground(null);
        pill2.setBackground(null);
        pill3.setBackground(null);
        pill4.setBackground(null);
        etDose.setText("");
        etName.setText("");
    }

}