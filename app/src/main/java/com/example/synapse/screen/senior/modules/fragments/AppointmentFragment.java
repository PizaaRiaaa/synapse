package com.example.synapse.screen.senior.modules.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
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

import com.example.synapse.R;
import com.example.synapse.screen.carer.CarerMainActivity;
import com.example.synapse.screen.carer.modules.fragments.HomeFragment;
import com.example.synapse.screen.carer.modules.view.ViewAppointment;
import com.example.synapse.screen.senior.SeniorMainActivity;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.ReplaceFragment;
import com.example.synapse.screen.util.TimePickerFragment;
import com.example.synapse.screen.util.adapter.ItemAppointmentSpecialist;
import com.example.synapse.screen.util.notifications.AlertReceiver;
import com.example.synapse.screen.util.readwrite.ReadWriteAppointment;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.example.synapse.screen.util.viewholder.AppointmentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
public class AppointmentFragment extends Fragment {

    // global variables
    PromptMessage promptMessage = new PromptMessage();
    ReplaceFragment replaceFragment = new ReplaceFragment();
    DatabaseReference referenceReminders, referenceCarer;

    final Calendar calendar = Calendar.getInstance();

    AppCompatButton btnMon, btnTue, btnWed, btnThu, btnFri, btnSat, btnSun;
    FirebaseUser mUser;

    RecyclerView recyclerView;
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
        View view = inflater.inflate(R.layout.fragment_senior_appointment, container, false);

        btnMon = view.findViewById(R.id.btnMON);
        btnTue = view.findViewById(R.id.btnTUE);
        btnWed = view.findViewById(R.id.btnWED);
        btnThu = view.findViewById(R.id.btnTHU);
        btnFri = view.findViewById(R.id.btnFRI);
        btnSat = view.findViewById(R.id.btnSAT);
        btnSun = view.findViewById(R.id.btnSUN);

        // references for firebase
        referenceReminders = FirebaseDatabase.getInstance().getReference("Appointment Reminders");
        referenceCarer = FirebaseDatabase.getInstance().getReference("Users").child("Seniors");

        // listen for broadcast
        // getActivity().registerReceiver(broadcastReceiver, new IntentFilter("NOTIFY_APPOINTMENT"));

        // get current user
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        // generate volley for sending notification to senior
        //requestQueue = Volley.newRequestQueue(getActivity());

        // set recyclerview
        recyclerView = view.findViewById(R.id.recyclerview_appointment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ImageButton ibBack = view.findViewById(R.id.ibBack);
        profilePic = view.findViewById(R.id.ivSeniorProfilePic);

        // show status bar
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        showUserProfile();
        loadScheduleForAppointments();
        displayCurrentDay();

        ibBack.setOnClickListener(v -> startActivity(new Intent(getActivity(), SeniorMainActivity.class)));

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

    // get the selected time
    Calendar getCalendar(){
        return calendar;
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

}