package com.example.osca_admin.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.osca_admin.R;
import com.example.osca_admin.util.AuditTrail;
import com.example.osca_admin.util.PromptMessage;
import com.example.osca_admin.util.readwrite.ReadWriteAuditTrail;
import com.example.osca_admin.util.viewholder.AuditTrailViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AuditTrailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AuditTrailFragment extends Fragment {

    DatabaseReference referenceAuditTrail;

    AuditTrail auditTrail;
    PromptMessage promptMessage;
    RecyclerView recyclerView;
    Query query;
    LinearLayoutManager mLayoutManager;

    MaterialButtonToggleGroup btnToggleGroup;
    MaterialButton btnMedicines;
    MaterialButton btnPhysicalActivity;
    MaterialButton btnAppointments;
    MaterialButton btnGames;
    MaterialButton btnAdmin;
    MaterialButton btnCarer;
    MaterialButton btnSenior;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AuditTrailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AuditLogsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AuditTrailFragment newInstance(String param1, String param2) {
        AuditTrailFragment fragment = new AuditTrailFragment();
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

    // layout for recycle view
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerview_auditLogs);
        mLayoutManager = new LinearLayoutManager(requireActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

    }

    void recycleViewAuditTrail(Query query){
        FirebaseRecyclerOptions<ReadWriteAuditTrail> options = new FirebaseRecyclerOptions.Builder<ReadWriteAuditTrail>().setQuery(query, ReadWriteAuditTrail.class).build();
        FirebaseRecyclerAdapter<ReadWriteAuditTrail, AuditTrailViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteAuditTrail, AuditTrailViewHolder>(options) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull AuditTrailViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteAuditTrail model) {

                holder.UserID.setText("Entity ID: " + model.getUserID());
                holder.By.setText("User: " + model.getName());
                holder.ActionMade.setText("Action Made: " + model.getActionMade());
                holder.Details.setText("Details: " + model.getInfo());

                SimpleDateFormat sfd = new SimpleDateFormat("MMMM-dd-yyyy hh:mm:ss", Locale.getDefault());
                holder.DateTime.setText("Timestamp: " + sfd.format(new Date(model.getTimestamp())));

            }
            @NonNull
            @Override
            public AuditTrailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_audit_trail, parent, false);
                return new AuditTrailViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void loadAuditLogs() {
        referenceAuditTrail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds2 : snapshot.getChildren()) {
                            query = ds2.getRef();
                            recycleViewAuditTrail(query);
                            int buttonID = btnToggleGroup.getCheckedButtonId();
                            btnToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
                                @Override
                                public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                                    if(isChecked){
                                        if(checkedId == R.id.btnMedicines){
                                            query = ds2.getRef().orderByChild("Type").equalTo("Medicine");
                                            recycleViewAuditTrail(query);
                                        }else if(checkedId == R.id.btnPhysicalActivity){
                                            query = ds2.getRef().orderByChild("Type").equalTo("Physical Activity");
                                            recycleViewAuditTrail(query);
                                        }else if(checkedId == R.id.btnAppointments){
                                            query = ds2.getRef().orderByChild("Type").equalTo("Appointment");
                                            recycleViewAuditTrail(query);
                                        }else if(checkedId == R.id.btnGames){
                                            query = ds2.getRef().orderByChild("Type").equalTo("Game");
                                            recycleViewAuditTrail(query);
                                        }else if(checkedId == R.id.btnAdmins){
                                            query = ds2.getRef().orderByChild("ActionMadeBy").equalTo("Admin");
                                            recycleViewAuditTrail(query);
                                        }else if(checkedId == R.id.btnCarers){
                                            query = ds2.getRef().orderByChild("ActionMadeBy").equalTo("Carer");
                                            recycleViewAuditTrail(query);
                                        }else if(checkedId == R.id.btnSeniors){
                                            query = ds2.getRef().orderByChild("ActionMadeBy").equalTo("Senior");
                                            recycleViewAuditTrail(query);
                                        }
                                  }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audit_logs, container, false);

        referenceAuditTrail = FirebaseDatabase.getInstance().getReference("Audit Trail");

        auditTrail = new AuditTrail();
        promptMessage = new PromptMessage();

        btnToggleGroup = view.findViewById(R.id.toggleButtonGroup);
        btnMedicines = view.findViewById(R.id.btnMedicines);
        btnPhysicalActivity = view.findViewById(R.id.btnPhysicalActivity);
        btnAppointments = view.findViewById(R.id.btnAppointments);
        btnGames = view.findViewById(R.id.btnGames);
        btnAdmin = view.findViewById(R.id.btnAdmins);
        btnCarer = view.findViewById(R.id.btnCarers);
        btnSenior = view.findViewById(R.id.btnSeniors);

        loadAuditLogs();

        return view;
    }
}