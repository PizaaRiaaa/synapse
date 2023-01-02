package com.example.synapse.screen.senior.modules.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.synapse.R;
import com.example.synapse.screen.carer.CarerMainActivity;
import com.example.synapse.screen.carer.modules.fragments.HomeFragment;
import com.example.synapse.screen.senior.SeniorMainActivity;
import com.example.synapse.screen.util.AuditTrail;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.ReplaceFragment;
import com.example.synapse.screen.util.readwrite.ReadWriteMedication;
import com.example.synapse.screen.util.readwrite.ReadWritePhysicalActivity;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.example.synapse.screen.util.viewholder.PhysicalActivityViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhysicalActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhysicalActivityFragment extends Fragment {

    PromptMessage promptMessage = new PromptMessage();
    ReplaceFragment replaceFragment = new ReplaceFragment();
    AuditTrail auditTrail = new AuditTrail();

    AppCompatButton btnMon, btnTue, btnWed, btnThu, btnFri, btnSat, btnSun, btnAddSchedule;
    DatabaseReference referenceReminders, referenceSenior;
    FirebaseUser mUser;

    LinearLayoutManager mLayoutManager;
    RequestQueue requestQueue;
    RecyclerView recyclerView;
    Query query;
    ImageView profilePic;
    Bundle args;
    String key;

    MaterialButtonToggleGroup toggleGroup;
    Button btnAll;
    Button btnDone;
    Button btnNotDone;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PhysicalActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhysicalActivityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhysicalActivityFragment newInstance(String param1, String param2) {
        PhysicalActivityFragment fragment = new PhysicalActivityFragment();
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
        View view = inflater.inflate(R.layout.fragment_senior_physical_activity, container, false);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        referenceReminders = FirebaseDatabase.getInstance().getReference("Physical Activity Reminders");
        referenceSenior = FirebaseDatabase.getInstance().getReference("Users").child("Seniors");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        requestQueue = Volley.newRequestQueue(getActivity());

        ImageButton ibBack = view.findViewById(R.id.ibBack);
        profilePic = view.findViewById(R.id.ivSeniorProfilePic);
        btnMon = view.findViewById(R.id.btnMON);
        btnTue = view.findViewById(R.id.btnTUE);
        btnWed = view.findViewById(R.id.btnWED);
        btnThu = view.findViewById(R.id.btnTHU);
        btnFri = view.findViewById(R.id.btnFRI);
        btnSat = view.findViewById(R.id.btnSAT);
        btnSun = view.findViewById(R.id.btnSUN);

        ibBack.setOnClickListener(v -> startActivity(new Intent(getActivity(), SeniorMainActivity.class)));

        showUserProfile();
        loadScheduleForPhysicalActivity();
        displayCurrentDay();

        args = getArguments();
        if(args != null){
            key = args.getString("key");
            updateSeniorPhysicalDone(key);
        }

        toggleGroup = view.findViewById(R.id.toggleButtonGroup);
        btnAll = view.findViewById(R.id.btnAll);
        btnDone = view.findViewById(R.id.btnDone);
        btnNotDone = view.findViewById(R.id.btnNotDone);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // layout for recycle view
        recyclerView = view.findViewById(R.id.recyclerview_physical_activity);
        mLayoutManager = new LinearLayoutManager(requireActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

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

    void recycleviewPhysicalActivity(Query query){
        FirebaseRecyclerOptions<ReadWritePhysicalActivity> options = new FirebaseRecyclerOptions.Builder<ReadWritePhysicalActivity>().setQuery(query, ReadWritePhysicalActivity.class).build();
        FirebaseRecyclerAdapter<ReadWritePhysicalActivity, PhysicalActivityViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWritePhysicalActivity, PhysicalActivityViewHolder>(options) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull PhysicalActivityViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWritePhysicalActivity model) {

                String activity = model.getActivity();
                String isDone = model.getIsDone();

                switch (activity) {
                    case "Stretching":
                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(getActivity(),R.drawable.ic_bg_stretching));
                        break;
                    case "Walking":
                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_bg_walking));
                        break;
                    case "Yoga":
                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_bg_yoga));
                        break;
                    case "Aerobics":
                        holder.ic_activity.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_bg_aerobics));
                        break;
                }

                holder.name.setText(model.getActivity());
                holder.duration.setText("Duration: " + model.getDuration());
                holder.time.setText(model.getTime());

                if(isDone.equals("Done")){
                    holder.ivIsDone.setBackground(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_is_taken));
                }

            }
            @NonNull
            @Override
            public PhysicalActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_senior_physical_activity_schedule, parent, false);
                return new PhysicalActivityViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void loadScheduleForPhysicalActivity() {
        referenceReminders.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ignored : snapshot.getChildren()) {
                        for (DataSnapshot ds2 : snapshot.getChildren()) {
                            query = ds2.getRef();
                            recycleviewPhysicalActivity(query);
                            int buttonID = toggleGroup.getCheckedButtonId();
                            toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
                                @Override
                                public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                                    if(isChecked){
                                        if(checkedId == R.id.btnAll){
                                            query = ds2.getRef();
                                            recycleviewPhysicalActivity(query);
                                        }else if(checkedId == R.id.btnDone){
                                            query = ds2.getRef().orderByChild("IsDone").equalTo("Done");
                                            recycleviewPhysicalActivity(query);
                                        }else if(checkedId == R.id.btnNotDone){
                                            query = ds2.getRef().orderByChild("IsDone").equalTo("Not Done");
                                            recycleviewPhysicalActivity(query);
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


    private void updateSeniorPhysicalDone(String key) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("IsDone", "Done");
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
                                                                "You have successfully finished your physical activity",
                                                                R.color.dark_green, getActivity());

                                                         referenceReminders
                                                                .child(mUser.getUid())
                                                                .child(carerID)
                                                                .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        for(DataSnapshot ds : snapshot.getChildren()){
                                                                            ReadWritePhysicalActivity physical = snapshot.getValue(ReadWritePhysicalActivity.class);
                                                                            String activity = physical.getActivity();

                                                                            auditTrail.auditTrail(
                                                                                    "Finished Physical Activity",
                                                                                    activity,
                                                                                    "Physical Activity", "Carer", referenceSenior, mUser);

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

    // display carer's profile pic
    private void showUserProfile(){
        // display carer profile pic
        referenceSenior.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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