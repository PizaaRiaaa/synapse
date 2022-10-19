package com.example.synapse.screen.carer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    ImageView carerImage;
    private FirebaseUser user;

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

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, SelectSenior.class);
        startActivity(intent);
    }


    // display all assigned seniors
    void loadAssignedSeniors() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference referenceAssignedSeniors = FirebaseDatabase.getInstance().getReference().child("AssignedSeniors");
        referenceAssignedSeniors.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DatabaseReference dsf = snapshot.getRef();
                            Query query = dsf.getRef();
                            FirebaseRecyclerOptions<ReadWriteUserSenior> options = new FirebaseRecyclerOptions.Builder<ReadWriteUserSenior>().setQuery(query, ReadWriteUserSenior.class).build();
                            FirebaseRecyclerAdapter<ReadWriteUserSenior, SeniorViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteUserSenior, SeniorViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull SeniorViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteUserSenior model) {
                                    String name = model.getFirstName() + " " + model.getLastName();
                                    String barangay = model.getBarangay();
                                    String city = model.getCity();
                                    String dob = model.getDob();

                                    Calendar cal = Calendar.getInstance();
                                    SimpleDateFormat format = new SimpleDateFormat("MM dd yyyy", Locale.ENGLISH);

                                    try {
                                        cal.setTime(Objects.requireNonNull(format.parse(dob)));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    holder.fullName.setText(name);
                                    holder.barangay.setText(barangay);
                                    holder.city.setText(city);
                                    holder.dob.setText(Integer.toString(calculateAge(cal.getTimeInMillis()))+ " yrs");

                                    Picasso.get()
                                            .load(model.getImage())
                                            .fit()
                                            .transform(new CropCircleTransformation())
                                            .into(holder.image);


                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(SelectSenior.this, CarerMainActivity.class);
                                            intent.putExtra("userKey", getRef(position).getKey());
                                            startActivity(intent);

                                            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("userKey", getRef(position).getKey());
                                            editor.apply();
                                            setDefaults("seniorKey", model.getSeniorID(),getApplicationContext());
                                            startActivity(new Intent(SelectSenior.this, CarerMainActivity.class));
                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        }
                                    });
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
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    void loadCarerImage(){
       DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users") .child("Carers");
       reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               ReadWriteUserDetails carer = snapshot.getValue(ReadWriteUserDetails.class);
               String imageURL = carer.getImageURL();
               Picasso.get()
                       .load(imageURL)
                       .transform(new CropCircleTransformation())
                       .into(carerImage);
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

        SharedPreferences myPrefs = getApplicationContext().getSharedPreferences("seniorKey", Context.MODE_PRIVATE);
        myPrefs.edit().remove("seniorKey").apply();

        recyclerView = findViewById(R.id.recyclerview_seniors);
        recyclerView.setLayoutManager(new LinearLayoutManager(SelectSenior.this));
        carerImage = findViewById(R.id.ivProfilePic);

        loadAssignedSeniors();
        loadCarerImage();

    }
}