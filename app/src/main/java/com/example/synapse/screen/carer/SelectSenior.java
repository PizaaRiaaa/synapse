package com.example.synapse.screen.carer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.synapse.R;
import com.example.synapse.screen.PickRole;
import com.example.synapse.screen.carer.modules.view.ViewMedicine;
import com.example.synapse.screen.util.readwrite.ReadWriteMedication;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.example.synapse.screen.util.readwrite.ReadWriteUserSenior;
import com.example.synapse.screen.util.viewholder.MedicationViewHolder;
import com.example.synapse.screen.util.viewholder.SeniorViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class SelectSenior extends AppCompatActivity {

    RecyclerView recyclerView;

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    // calculate senior's age
    int calculateAge(long date){
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if(today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)){
            age--;
        }
        return age;
    }

    // display all assigned seniors
    void loadAssignedSeniors() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference referenceAssignedSeniors = FirebaseDatabase.getInstance().getReference("AssignedSeniors");

        referenceAssignedSeniors.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        for (DataSnapshot ds1 : snapshot.getChildren()) {
                            Query query = ds1.getRef();

                            FirebaseRecyclerOptions<ReadWriteUserSenior> options = new FirebaseRecyclerOptions.Builder<ReadWriteUserSenior>().setQuery(query, ReadWriteUserSenior.class).build();
                            FirebaseRecyclerAdapter<ReadWriteUserSenior, SeniorViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteUserSenior, SeniorViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull SeniorViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteUserSenior model) {
                                    String name = model.getFirstName();
                                    holder.fullName.setText(name);

                                }

                                @NonNull
                                @Override
                                public SeniorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_select_senior, parent, false);
                                    return new SeniorViewHolder(view);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_senior);
        recyclerView = findViewById(R.id.recyclerview_seniors);
        recyclerView.setLayoutManager(new LinearLayoutManager(SelectSenior.this));

        loadAssignedSeniors();

    }
}