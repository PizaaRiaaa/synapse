package com.example.synapse.screen.senior.modules.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.synapse.R;
import com.example.synapse.screen.carer.modules.view.ViewMedicine;
import com.example.synapse.screen.util.AuditTrail;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
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
import org.checkerframework.checker.units.qual.A;

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
public class MedicationFragment extends Fragment {

    // Global variables
    ReplaceFragment replaceFragment = new ReplaceFragment(); // replacing fragment
    PromptMessage promptMessage = new PromptMessage(); // custom prompt message
    AuditTrail auditTrail = new AuditTrail();

    DatabaseReference referenceProfile, referenceReminders;
    FirebaseUser mUser;
    RequestQueue requestQueue;

    AppCompatButton btnMon;
    AppCompatButton btnTue;
    AppCompatButton btnWed;
    AppCompatButton btnThu;
    AppCompatButton btnFri;
    AppCompatButton btnSat;
    AppCompatButton btnSun;

    RecyclerView recyclerView;
    ImageView profilePic;

    LinearLayoutManager mLayoutManager;
    Query query;
    Bundle args;
    String key;

    MaterialButtonToggleGroup toggleGroup;
    Button btnAll;
    Button btnTaken;
    Button btnNotTaken;

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
        View view = inflater.inflate(R.layout.fragment_senior_medication, container, false);

        referenceProfile = FirebaseDatabase.getInstance().getReference("Users").child("Seniors");
        referenceReminders = FirebaseDatabase.getInstance().getReference().child("Medication Reminders");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        requestQueue = Volley.newRequestQueue(getActivity());

        // view ids for this fragment
        ImageButton ibBack = view.findViewById(R.id.ibBack);
        profilePic = view.findViewById(R.id.ivProfilePic);
        btnMon = view.findViewById(R.id.btnMON);
        btnTue = view.findViewById(R.id.btnTUE);
        btnWed = view.findViewById(R.id.btnWED);
        btnThu = view.findViewById(R.id.btnTHU);
        btnFri = view.findViewById(R.id.btnFRI);
        btnSat = view.findViewById(R.id.btnSAT);
        btnSun = view.findViewById(R.id.btnSUN);

        toggleGroup = view.findViewById(R.id.toggleButtonGroup);
        btnAll = view.findViewById(R.id.btnAll);
        btnTaken = view.findViewById(R.id.btnTaken);
        btnNotTaken = view.findViewById(R.id.btnNotTaken);

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ibBack.setOnClickListener(v -> replaceFragment.replaceFragment(new HomeFragment(), getActivity()));

        showUserProfile();
        LoadScheduleForMedication();
        displayCurrentDay();

        // we need  to check if user clicked the notification
        // then retrieve id first
        // so we can display the right information with the right key
        args = getArguments();
        if(args != null){
            key = args.getString("key");
            updateSeniorMedicineTaken(key);
        }

        return view;
    }

    private void updateSeniorMedicineTaken(String key) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("isTaken", "Taken");
        referenceReminders.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    String carerID = ds.getKey();
                    referenceReminders
                            .child(mUser.getUid())
                            .child(carerID)
                            .child(key)
                            .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                    if (task.isSuccessful()) {
                                        referenceReminders
                                                .child(carerID)
                                                .child(mUser.getUid())
                                                .child(key)
                                                .updateChildren(hashMap).addOnCompleteListener(task0 -> {
                                                    if (task0.isSuccessful()) {
                                                        promptMessage.displayMessage(
                                                                "Success",
                                                                "You have successfully taken your medicine",
                                                                R.color.dark_green, getActivity());

                                                        referenceReminders
                                                                .child(mUser.getUid())
                                                                .child(carerID)
                                                                .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        for(DataSnapshot ds : snapshot.getChildren()){
                                                                            ReadWriteMedication medicine = snapshot.getValue(ReadWriteMedication.class);
                                                                            String name = medicine.getName();
                                                                            String dosage = medicine.getDosage();

                                                                            auditTrail.auditTrail(
                                                                                    "Took Medicine",
                                                                                    name + " " + dosage,
                                                                                    "Medicine", "Carer", referenceProfile, mUser);

                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });

                                                    }
                                                });
                                    }
                                }
                           });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // layout for recycle view
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerview_medication);
        mLayoutManager = new LinearLayoutManager(requireActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

    }

    void recycleviewMedicine(Query query){
        FirebaseRecyclerOptions<ReadWriteMedication> options = new FirebaseRecyclerOptions.Builder<ReadWriteMedication>().setQuery(query, ReadWriteMedication.class).build();
        FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder>(options) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull MedicationViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteMedication model) {

                String pill_shape = model.getShape();
                String pill_color = model.getColor();
                String dose = model.getDose();
                String isTaken = model.getIsTaken();
                String get_dose = dose.split(" ")[0];

                if (pill_color.equals("White") && pill_shape.equals("Pill1")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill1_white_horizontal));
                } else if (pill_color.equals("Blue") && pill_shape.equals("Pill1")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill1_blue_horizontal));
                } else if (pill_color.equals("Brown") && pill_shape.equals("Pill1")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill1_brown_horizontal));
                } else if (pill_color.equals("Green") && pill_shape.equals("Pill1")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill1_green_horizontal));
                } else if (pill_color.equals("Pink") && pill_shape.equals("Pill1")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill1_pink_horizontal));
                } else if (pill_color.equals("Red") && pill_shape.equals("Pill1")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill1_red_horizontal));
                }

                if (pill_color.equals("White") && pill_shape.equals("Pill2")) {
                    holder.pill_shape.getLayoutParams().height = 160;
                    holder.pill_shape.getLayoutParams().width = 160;
                    holder.pill_shape.requestLayout();
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill2_white));
                } else if (pill_color.equals("Blue") && pill_shape.equals("Pill2")) {
                    holder.pill_shape.getLayoutParams().height = 160;
                    holder.pill_shape.getLayoutParams().width = 160;
                    holder.pill_shape.requestLayout();
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill2_blue));
                } else if (pill_color.equals("Brown") && pill_shape.equals("Pill2")) {
                    holder.pill_shape.getLayoutParams().height = 160;
                    holder.pill_shape.getLayoutParams().width = 160;
                    holder.pill_shape.requestLayout();
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill2_brown));
                } else if (pill_color.equals("Green") && pill_shape.equals("Pill2")) {
                    holder.pill_shape.getLayoutParams().height = 160;
                    holder.pill_shape.getLayoutParams().width = 160;
                    holder.pill_shape.requestLayout();
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill2_green));
                } else if (pill_color.equals("Pink") && pill_shape.equals("Pill2")) {
                    holder.pill_shape.getLayoutParams().height = 160;
                    holder.pill_shape.getLayoutParams().width = 160;
                    holder.pill_shape.requestLayout();
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill2_pink));
                } else if (pill_color.equals("Red") && pill_shape.equals("Pill2")) {
                    holder.pill_shape.getLayoutParams().height = 160;
                    holder.pill_shape.getLayoutParams().width = 160;
                    holder.pill_shape.requestLayout();
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill2_red));
                }

                if (pill_color.equals("White") && pill_shape.equals("Pill3")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill3_white_horizontal));
                } else if (pill_color.equals("Blue") && pill_shape.equals("Pill3")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill3_blue_horizontal));
                } else if (pill_color.equals("Brown") && pill_shape.equals("Pill3")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill3_brown_horizontal));
                } else if (pill_color.equals("Green") && pill_shape.equals("Pill3")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill3_green_horizontal));
                } else if (pill_color.equals("Pink") && pill_shape.equals("Pill3")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill3_pink_horizontal));
                } else if (pill_color.equals("Red") && pill_shape.equals("Pill3")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill3_red_horizontal));
                }

                if (pill_color.equals("White") && pill_shape.equals("Pill4")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill4_white_horizontal));
                } else if (pill_color.equals("Blue") && pill_shape.equals("Pill4")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill4_blue_horizontal));
                } else if (pill_color.equals("Brown") && pill_shape.equals("Pill4")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill4_brown_horizontal));
                } else if (pill_color.equals("Green") && pill_shape.equals("Pill4")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill4_green_horizontal));
                } else if (pill_color.equals("Pink") && pill_shape.equals("Pill4")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill4_pink_horizontal));
                } else if (pill_color.equals("Red") && pill_shape.equals("Pill4")) {
                    holder.pill_shape.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.pill4_red_horizontal));
                }

                holder.time.setText(model.getTime());
                holder.name.setText(model.getName());
                holder.inTake.setText(model.getInTake());

                if(Objects.equals(get_dose, "1")){
                    holder.dose.setText("dose " + get_dose + " pill" + " | " + "dosage " + model.getDosage());
                }else{
                    holder.dose.setText("dose " + get_dose + " pills" + " | " + "dosage " + model.getDosage());
                }

                if(isTaken.equals("Taken")){
                    holder.tvIsTaken.setText("Taken");
                    holder.taken.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_is_taken));
                }else{
                    holder.tvIsTaken.setText("Taken");
                }
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


    // display all schedules for medication
    private void LoadScheduleForMedication() {
        referenceReminders.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ignored : snapshot.getChildren()) {
                        for (DataSnapshot ds2 : snapshot.getChildren()) {
                            query = ds2.getRef();
                            recycleviewMedicine(query);
                            int buttonID = toggleGroup.getCheckedButtonId();
                            toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
                                @Override
                                public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                                    if(isChecked){
                                        if(checkedId == R.id.btnAll){
                                            query = ds2.getRef();
                                            recycleviewMedicine(query);
                                        }else if(checkedId == R.id.btnTaken){
                                            query = ds2.getRef().orderByChild("isTaken").equalTo("Taken");
                                            recycleviewMedicine(query);
                                        }else if(checkedId == R.id.btnNotTaken){
                                            query = ds2.getRef().orderByChild("isTaken").equalTo("Not Taken");
                                            recycleviewMedicine(query);
                                        }
                                    }
                                }
                            });
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


    // display senior's profile pic
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

}