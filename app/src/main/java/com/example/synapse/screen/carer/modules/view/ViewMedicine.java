package com.example.synapse.screen.carer.modules.view;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.synapse.R;
import com.example.synapse.screen.carer.modules.fragments.MedicationFragment;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.ReplaceFragment;
import com.example.synapse.screen.util.TimePickerFragment;
import com.example.synapse.screen.util.adapter.ItemPillColorAdapter;
import com.example.synapse.screen.util.adapter.ItemPillShapeAdapter;
import com.example.synapse.screen.util.notifications.MedicineNotificationHelper;
import com.example.synapse.screen.util.readwrite.ReadWriteMedication;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.example.synapse.screen.util.notifications.AlertReceiver;
import com.example.synapse.screen.util.notifications.FcmNotificationsSender;
import com.google.android.datatransport.runtime.scheduling.jobscheduling.AlarmManagerSchedulerBroadcastReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.aviran.cookiebar2.CookieBar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ViewMedicine extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener {

    private FirebaseUser mUser;
    private DatabaseReference referenceReminders;
    private final String[] PILL_SHAPE_NAMES = {"Pill1", "Pill2","Pill3","Pill4"};
    private final String[] PILL_COLOR_NAMES = {"Green","Red","Brown","Pink","Blue","White"};
    private final int [] PILL_SHAPES_ICS = {R.drawable.ic_pill1, R.drawable.ic_pill2, R.drawable.ic_pill3, R.drawable.ic_pill4};
    private final int [] PILL_IC_COLORS ={R.drawable.pill_green,R.drawable.pill_red,R.drawable.pill_brown,
            R.drawable.pill_pink,R.drawable.pill_blue,R.drawable.pill_white};
    private String pill_color_selected;
    private String pill_shape_selected;

    private AppCompatEditText etPillName, etPillDosage, etPillDescription;
    private int count = 0;
    private Intent intent;
    private final Calendar calendar = Calendar.getInstance();
    private Long request_code;
    int code, requestCode;

    String medicineID, name,
           dosage, color, shape, dose,
           time, description, quantity;

    RequestQueue requestQueue;
    MaterialCardView btnChangeTime;
    AppCompatButton btnUpdate;
    AppCompatEditText etDose;
    ImageView ibBin;
    TextView tvAlarm, tvPillName;
    ImageView ivPill;
    MaterialButton ibMinus, ibAdd;
    Spinner pillShapeSpinner, pillColorSpinner;
    ItemPillShapeAdapter adapter;
    ItemPillColorAdapter adapter2;

    PromptMessage promptMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medication);

        ImageButton btnBack = findViewById(R.id.ibBack);
        pillColorSpinner = findViewById(R.id.spinner_pillColor);
        pillShapeSpinner = findViewById(R.id.spinner_pillShape);
        ivPill = findViewById(R.id.ivPill);
        etPillName = findViewById(R.id.tvPillNameSub);
        tvPillName = findViewById(R.id.tvPillNameHeader);
        etPillDosage = findViewById(R.id.etDosageSub);
        etDose = findViewById(R.id.etDose);
        etPillDescription = findViewById(R.id.tvDescriptionSub);
        tvAlarm = findViewById(R.id.tvAlarmSub);
        btnUpdate = findViewById(R.id.btnUpdate);
        ibBin = findViewById(R.id.ibBin);
        btnChangeTime = findViewById(R.id.btnChangeSchedule);
        ibMinus = findViewById(R.id.ibMinus);
        ibAdd = findViewById(R.id.ibAdd);

        pillShapeSpinner.setOnItemSelectedListener(ViewMedicine.this);
        adapter = new ItemPillShapeAdapter(ViewMedicine.this, PILL_SHAPES_ICS, PILL_SHAPE_NAMES);
        adapter.notifyDataSetChanged();
        pillShapeSpinner.setAdapter(adapter);

        pillColorSpinner.setOnItemSelectedListener(ViewMedicine.this);
        adapter2 = new ItemPillColorAdapter(ViewMedicine.this, PILL_COLOR_NAMES, PILL_IC_COLORS);
        adapter2.notifyDataSetChanged();
        pillColorSpinner.setAdapter(adapter2);

        promptMessage = new PromptMessage();

        referenceReminders = FirebaseDatabase.getInstance().getReference("Medication Reminders");
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        requestQueue = Volley.newRequestQueue(ViewMedicine.this);

        // Show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // we need  to check if user clicked the notification
        // then retrieve id first
        // so we can display the right information with the right key
        medicineID = getIntent().getStringExtra( "userKey");
        String key = getIntent().getStringExtra("key");
        if(key != null) showMedicineInfo(key);
        else showMedicineInfo(medicineID);

        btnUpdate.setOnClickListener(v -> updatePill(medicineID));


        deleteMedicine();

        // back button
        btnBack.setOnClickListener(v -> finish());

        ibMinus.setOnClickListener(this::decrement);
        ibAdd.setOnClickListener(this::increment);

        // change time
        btnChangeTime.setOnClickListener(v -> {
                DialogFragment timePicker = new TimePickerFragment(this::onTimeSet);
                timePicker.show(getSupportFragmentManager(), "time picker");
        });
    }

    // decrement and increment for dose input
    public void increment(View v) {
        count++;
        etDose.setText("");
        etDose.setText("" + count + " pills");
    }

    public void decrement(View v) {
        if (count <= 0) count = 0;
        else count--;
        etDose.setText("");
        etDose.setText("" + count + " pills");
    }

    // update textview based on user's timepicker input
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        updateTimeText(calendar);
    }

    private void updateTimeText(Calendar calendar) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        tvAlarm.setText(simpleDateFormat.format(calendar.getTime()));
        time = simpleDateFormat.format(calendar.getTime());
    }

    // set the alarm manager and listen for broadcast
    private void startAlarm(Calendar c) {
        requestCode = (int)calendar.getTimeInMillis()/1000;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("Medication",1);

        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, 0);
        }
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        // alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        // set alarm for everyday
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }

    public void cancelAlarm(int requestCode){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(this,requestCode, intent, 0);
        }
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinner_pillShape){
            pill_shape_selected = PILL_SHAPE_NAMES[position];
        }else if(parent.getId() == R.id.spinner_pillColor){
            pill_color_selected = PILL_COLOR_NAMES[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showMedicineInfo(String medicineID){
        // userID >
        referenceReminders.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot ignored: snapshot.getChildren()){

                  // userID > seniorID
                   referenceReminders.child(mUser.getUid()).child(getDefaults("seniorKey",ViewMedicine.this)).addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                          for(DataSnapshot ignored : snapshot.getChildren()) {

                              // userID > seniorID > medicineID
                              referenceReminders.child(mUser.getUid()).child(getDefaults("seniorKey",ViewMedicine.this)).child(medicineID).addListenerForSingleValueEvent(new ValueEventListener() {
                                  @SuppressLint("UseCompatLoadingForDrawables")
                                  @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                                      if(snapshot.exists()){
                                          ReadWriteMedication readWriteMedication = snapshot.getValue(ReadWriteMedication.class);
                                          adapter.notifyDataSetChanged();
                                          adapter2.notifyDataSetChanged();

                                          name = readWriteMedication.getName();
                                          dosage = readWriteMedication.getDosage();
                                          shape = readWriteMedication.getShape();
                                          color = readWriteMedication.getColor();
                                          dose = readWriteMedication.getDose();
                                          time = readWriteMedication.getTime();
                                          description = readWriteMedication.getDescription();
                                          quantity = readWriteMedication.getQuantity();

                                          etPillName.setText(name);
                                          tvPillName.setText(name);
                                          etDose.setText(dose);
                                          tvAlarm.setText(time);
                                          etPillDosage.setText(dosage);
                                          etPillDescription.setText(description);

                                          switch(shape){
                                              case "Pill1":
                                                  displayPillColor(R.drawable.pill1_green_horizontal,R.drawable.pill1_red_horizontal,R.drawable.pill1_brown_horizontal,
                                                          R.drawable.pill1_pink_horizontal,R.drawable.pill1_blue_horizontal,R.drawable.pill1_white_horizontal);
                                                  pillShapeSpinner.setSelection(0);
                                                  break;
                                              case "Pill2":
                                                  displayPillColor(R.drawable.pill2_green,R.drawable.pill2_red,R.drawable.pill2_brown,
                                                          R.drawable.pill2_pink,R.drawable.pill2_blue,R.drawable.pill2_white);
                                                  pillShapeSpinner.setSelection(1);
                                                  break;
                                              case "Pill3":
                                                  displayPillColor(R.drawable.pill3_green_horizontal,R.drawable.pill3_red_horizontal,R.drawable.pill3_brown_horizontal,
                                                          R.drawable.pill3_pink_horizontal,R.drawable.pill3_blue_horizontal,R.drawable.pill3_white_horizontal);
                                                  pillShapeSpinner.setSelection(2);
                                                  break;
                                              case "Pill4":
                                                  displayPillColor(R.drawable.pill4_green_horizontal,R.drawable.pill4_red_horizontal,R.drawable.pill4_brown_horizontal,
                                                          R.drawable.pill4_pink_horizontal,R.drawable.pill4_blue_horizontal,R.drawable.pill4_white_horizontal);
                                                  pillShapeSpinner.setSelection(3);
                                                  break;
                                          }
                                      }
                                  }
                                  @Override
                                  public void onCancelled(@NonNull DatabaseError error) {
                                      promptMessage.defaultErrorMessage(ViewMedicine.this);
                                  }
                              });
                          }
                       }
                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {
                           promptMessage.defaultErrorMessage(ViewMedicine.this);
                       }
                   });
               }
           }
           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               promptMessage.defaultErrorMessage(ViewMedicine.this);
            }
       });
    }

    public void displayPillColor(int color1, int color2, int color3, int color4, int color5, int color6) {
        switch (color) {
            case "Green":
                ivPill.setBackground(AppCompatResources.getDrawable(this, color1));
                pillColorSpinner.setSelection(0);
            break;
            case "Red":
                ivPill.setBackground(AppCompatResources.getDrawable(this, color2));
                pillColorSpinner.setSelection(1);
                break;
            case "Brown":
                ivPill.setBackground(AppCompatResources.getDrawable(this, color3));
                pillColorSpinner.setSelection(2);
                break;
            case "Pink":
                ivPill.setBackground(AppCompatResources.getDrawable(this, color4));
                pillColorSpinner.setSelection(3);
                break;
            case "Blue":
                ivPill.setBackground(AppCompatResources.getDrawable(this, color5));
                pillColorSpinner.setSelection(4);
                break;
            case "White":
                ivPill.setBackground(AppCompatResources.getDrawable(this, color6));
                pillColorSpinner.setSelection(5);
                break;
        }
    }

    // update pill for both carer and senior nodes
    public void updatePill(String medicineID){
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Name", Objects.requireNonNull(etPillName.getText()).toString());
        hashMap.put("Dose", Objects.requireNonNull(etDose.getText()).toString());
        hashMap.put("Dosage", Objects.requireNonNull(etPillDosage.getText()).toString());
        hashMap.put("Shape", pill_shape_selected);
        hashMap.put("Color", pill_color_selected);
        hashMap.put("Time", tvAlarm.getText().toString());
        hashMap.put("Description", Objects.requireNonNull(etPillDescription.getText()).toString());

        referenceReminders.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ignored : snapshot.getChildren()){

                    referenceReminders.child(mUser.getUid()).child(getDefaults("seniorKey",ViewMedicine.this)).child(medicineID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ignored : snapshot.getChildren()){
                                ReadWriteMedication rw1 = snapshot.getValue(ReadWriteMedication.class);
                                assert rw1 != null;
                                Long carer_request_code = rw1.getRequestCode();
                                int i = carer_request_code.intValue();

                                String time1 = rw1.getTime();
                                String time2 = tvAlarm.getText().toString();

                                if(!time1.equals(time2)){

                                    // if the alarm was updated by the user, then we need to cancel the old alarm
                                    cancelAlarm(i);

                                    // store the new requestCode
                                    hashMap.put("RequestCode", requestCode);

                                    // start the new alarm
                                    startAlarm(calendar);
                                }

                                referenceReminders.child(getDefaults("seniorKey",ViewMedicine.this)).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds3 : snapshot.getChildren()){
                                            String senior_medicineID  = ds3.getKey();

                                            referenceReminders.child(getDefaults("seniorKey",ViewMedicine.this)).child(mUser.getUid()).child(senior_medicineID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    ReadWriteMedication rw2 = snapshot.getValue(ReadWriteMedication.class);
                                                    Long senior_request_code = rw2.getRequestCode();

                                                    // update both nodes
                                                    if(senior_request_code.equals(carer_request_code)){
                                                        referenceReminders.child(mUser.getUid()).child(getDefaults("seniorKey",ViewMedicine.this)).child(medicineID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                referenceReminders.child(getDefaults("seniorKey",ViewMedicine.this)).child(mUser.getUid()).child(senior_medicineID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    promptMessage.defaultErrorMessage(ViewMedicine.this);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        promptMessage.defaultErrorMessage(ViewMedicine.this);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            promptMessage.defaultErrorMessage(ViewMedicine.this);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(ViewMedicine.this);
            }
        });
        promptMessage.defaultErrorMessage(ViewMedicine.this);
    }

    // delete medicine for both carer and senior nodes
    public void deleteMedicine(){
               ibBin.setOnClickListener(v -> {
                   // userID
                   referenceReminders.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           for(DataSnapshot ignored: snapshot.getChildren()){
                               // userID > seniorID > medicineID (retrieve)
                               referenceReminders.child(mUser.getUid()).child(getDefaults("seniorKey",ViewMedicine.this)).child(medicineID).addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot snapshot) {

                                       // retrieve request code
                                       ReadWriteMedication readWriteMedication = snapshot.getValue(ReadWriteMedication.class);
                                       request_code = readWriteMedication.getRequestCode();
                                       code = request_code.intValue();

                                       // cancel the alarm
                                       cancelAlarm(code);

                                       // userID > seniorID > medicineID (remove)
                                       referenceReminders.child(mUser.getUid()).child(getDefaults("seniorKey",ViewMedicine.this)).child(medicineID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {

                                               if(task.isSuccessful()){
                                                   // seniorID > carerID
                                                   referenceReminders.child(getDefaults("seniorKey",ViewMedicine.this)).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                       @Override
                                                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                           for(DataSnapshot ds2: snapshot.getChildren()){
                                                               String medicine_key = ds2.getKey();

                                                               // seniorID > carerID > medicineID (retrieve)
                                                               referenceReminders.child(getDefaults("seniorKey",ViewMedicine.this)).child(mUser.getUid()).child(medicine_key).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                   @Override
                                                                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                       for(DataSnapshot ignored : snapshot.getChildren()){

                                                                           //retrieve request code
                                                                           ReadWriteMedication rw = snapshot.getValue(ReadWriteMedication.class);
                                                                           Long request_code2 = rw.getRequestCode();
                                                                           if (request_code2.equals(request_code)) {
                                                                               // seniorID > carerID > medicineID (remove)
                                                                               referenceReminders.child(getDefaults("seniorKey",ViewMedicine.this)).child(mUser.getUid()).child(medicine_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                   @Override
                                                                                   public void onComplete(@NonNull Task<Void> task) {
                                                                                       finish();
                                                                                   }
                                                                               });
                                                                           }
                                                                       }
                                                                   }

                                                                   @Override
                                                                   public void onCancelled(@NonNull DatabaseError error) {
                                                                       promptMessage.defaultErrorMessage(ViewMedicine.this);
                                                                   }
                                                               });
                                                           }
                                                       }

                                                       @Override
                                                       public void onCancelled(@NonNull DatabaseError error) {
                                                           promptMessage.defaultErrorMessage(ViewMedicine.this);
                                                       }
                                                   });
                                               }
                                           }
                                       });
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError error) {
                                       promptMessage.defaultErrorMessage(ViewMedicine.this);
                                   }
                               });
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {
                           promptMessage.defaultErrorMessage(ViewMedicine.this);
                       }
                   });
               });
           }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

}