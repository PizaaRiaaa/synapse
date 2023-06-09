package com.example.synapse.screen.senior.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synapse.R;
import com.example.synapse.screen.util.readwrite.ReadWriteMedication;
import com.example.synapse.screen.util.notifications.AlertReceiver;
import com.example.synapse.screen.util.viewholder.MedicationViewHolder;
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
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TimePicker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MedicationDashboard extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private DatabaseReference referenceReminders;
    private FirebaseUser mUser;
    private RecyclerView recyclerView;
    private final Calendar calendar = Calendar.getInstance();
    //private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_dashboard);

        referenceReminders = FirebaseDatabase.getInstance().getReference("Reminders");
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        ImageButton ibBack = findViewById(R.id.ibBack);

        // direct user to SeniorHome screen
        //ibBack.setOnClickListener(v -> startActivity(new Intent(MedicationDashboard.this, SeniorHome.class)));

        // set layout for recyclerview
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(MedicationDashboard.this,2));

        // load recyclerview
        LoadScheduleForMedication();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
    }

    // display all schedules for medication
    private void LoadScheduleForMedication(){
        referenceReminders.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ignored : snapshot.getChildren()){
                        for(DataSnapshot ds2 : snapshot.getChildren()){
                            Query query = ds2.getRef();
                            FirebaseRecyclerOptions<ReadWriteMedication> options = new FirebaseRecyclerOptions.Builder<ReadWriteMedication>().setQuery(query, ReadWriteMedication.class).build();
                            FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder>(options) {
                                @SuppressLint("SetTextI18n")
                                @Override
                                protected void onBindViewHolder(@NonNull MedicationViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteMedication model) {

                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd yyyy hh:mm a", Locale.ENGLISH);
                                    try {
                                        c.setTime(sdf.parse(holder.time_of_medication = model.getTime()));

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    //startAlarm(c);


                                    // display medication reminders
                                    holder.name.setText(model.getName());
                                    holder.dose.setText(model.getDose() + " times today");


                                    // // open user's profile and send user's userKey to another activity
                                    // holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    //     @Override
                                    //     public void onClick(View v) {
                                    //         Intent intent = new Intent(SearchPeople.this, ViewPeople.class);
                                    //         intent.putExtra("userKey", getRef(position).getKey());
                                    //         startActivity(intent);
                                    //     }
                                    // });
                                }


                                @NonNull
                                @Override
                                public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_senior_medication_schedule, parent, false);
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

            }
        });
    }

    // alarm for medications on recyclerview
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
}