package com.example.synapse.screen.carer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.synapse.R;
import com.example.synapse.screen.carer.modules.Appointment;
import com.example.synapse.screen.carer.modules.Games;
import com.example.synapse.screen.carer.modules.Medication;
import com.example.synapse.screen.carer.modules.PhysicalActivity;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.aviran.cookiebar2.CookieBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class CarerHome extends AppCompatActivity {

    private DatabaseReference referenceProfile, referenceRequest, referenceCompanion;
    private ImageView ivProfilePic, ivSeniorProfilePic;
    private TextView tvSeniorFullName;
    private TextView tvBarangay;
    private TextView tvSeniorAge;
    private String seniorID, imageURL;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_home);

        ShapeableImageView btnMedication, btnPhysicalActivity, btnAppointment, btnGames;

        ivProfilePic = findViewById(R.id.ivCarerProfilePic);
        ivSeniorProfilePic = findViewById(R.id.ivSeniorProfilePic);
        tvSeniorFullName = findViewById(R.id.tvSeniorFullName);
        tvBarangay = findViewById(R.id.tvSeniorBarangay);
        tvSeniorAge = findViewById(R.id.tvSeniorAge);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // get instance of the current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        // references to firebase
        referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        referenceCompanion = FirebaseDatabase.getInstance().getReference("Companion");
        referenceRequest = FirebaseDatabase.getInstance().getReference("Request");
        String userID = user.getUid();

        // show top status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // set bottomNavigationView to transparent
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        // display carer's profile picture
        showUserProfile(userID);

        // display assigned senior's information
        showSeniorProfile(userID);

        // redirect user to medication screen
        btnMedication = findViewById(R.id.btnMedication);
        btnMedication.setOnClickListener(v -> startActivity(new Intent(CarerHome.this, Medication.class)));

        // redirect user to physical activity screen
        btnPhysicalActivity = findViewById(R.id.btnPhysicalActivity);
        btnPhysicalActivity.setOnClickListener(v -> startActivity(new Intent(CarerHome.this, PhysicalActivity.class)));

        // redirect user to appointment screen
        btnAppointment = findViewById(R.id.btnAppointment);
        btnAppointment.setOnClickListener(v -> startActivity(new Intent(CarerHome.this, Appointment.class)));

        // redirect user to games screen
        btnGames = findViewById(R.id.btnGames);
        btnGames.setOnClickListener(v -> startActivity(new Intent(CarerHome.this, Games.class)));
    }

   // display carer profile pic
    private void showUserProfile(String firebaseUser){
        referenceProfile.child(firebaseUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails userProfile = snapshot.getValue(ReadWriteUserDetails.class);
                if(userProfile != null){
                        // display carer profile pic
                        Uri uri = user.getPhotoUrl();
                        Picasso.get()
                               .load(uri)
                               .transform(new CropCircleTransformation())
                               .into(ivProfilePic);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                prompMessage("Error","Something went wrong please try again",R.color.dark_green);
            }
        });
   }

   // calculate senior's age
    public int calculateAge(long date){
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if(today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)){
            age--;
        }
        return age;
    }

   // display assigned senior info
    private void showSeniorProfile(String firebaseUser){
        // check if carer already send request
        referenceRequest.child(firebaseUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

               if(snapshot.exists()){
                     for(DataSnapshot ds : snapshot.getChildren()){
                         seniorID = ds.getKey();
                         assert seniorID != null;

                         referenceProfile.child(seniorID).addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    ReadWriteUserDetails seniorProfile = snapshot.getValue(ReadWriteUserDetails.class);

                                   assert seniorProfile != null;
                                    // get user's age from date of birth
                                    String user_dob = seniorProfile.getDOB();
                                    Calendar cal = Calendar.getInstance();
                                    SimpleDateFormat format = new SimpleDateFormat("MM dd yyyy", Locale.ENGLISH);
                                    try {
                                        cal.setTime(Objects.requireNonNull(format.parse(user_dob)));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    String fullName = seniorProfile.fullName;
                                    String barangay = seniorProfile.address;

                                    tvSeniorFullName.setText(fullName);
                                    tvBarangay.setText("Brgy." + barangay + ",");
                                    tvSeniorAge.setText(Integer.toString(calculateAge(cal.getTimeInMillis()))+ " yrs");

                                    imageURL = Objects.requireNonNull(snapshot.child("imageURL").getValue()).toString();
                                    Picasso.get()
                                            .load(imageURL)
                                            .fit()
                                            .transform(new CropCircleTransformation())
                                            .into(ivSeniorProfilePic);
                                }
                             }
                             @Override
                             public void onCancelled(@NonNull DatabaseError error) {
                                 prompMessage("Error","Something went wrong please try again",R.color.dark_green);
                             }
                         });
                   }
               }else{
                  // check if senior accepted the carer request
                  referenceCompanion.child(firebaseUser).addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot snapshot) {

                         if(snapshot.exists()){
                             for(DataSnapshot ds : snapshot.getChildren()){
                                seniorID = ds.getKey();
                                assert seniorID != null;

                                referenceProfile.child(seniorID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            ReadWriteUserDetails seniorProfile = snapshot.getValue(ReadWriteUserDetails.class);
                                            String fullName = seniorProfile.fullName;
                                            String barangay = seniorProfile.address;
                                            assert seniorProfile != null;

                                            // get user's age from date of birth
                                            String user_dob = seniorProfile.getDOB();
                                            Calendar cal = Calendar.getInstance();
                                            SimpleDateFormat format = new SimpleDateFormat("MM dd yyyy", Locale.ENGLISH);
                                            try {
                                                cal.setTime(Objects.requireNonNull(format.parse(user_dob)));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            tvSeniorFullName.setText(fullName);
                                            tvBarangay.setText("Brgy." + barangay + ",");
                                            tvSeniorAge.setText(Integer.toString(calculateAge(cal.getTimeInMillis()))+ " yrs");

                                            imageURL = Objects.requireNonNull(snapshot.child("imageURL").getValue()).toString();
                                            Picasso.get()
                                                    .load(imageURL)
                                                    .fit()
                                                    .transform(new CropCircleTransformation())
                                                    .into(ivSeniorProfilePic);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        prompMessage("Error","Something went wrong please try again",R.color.dark_green);
                                    }
                                });
                             }
                         }
                      }
                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {
                          prompMessage("Error","Something went wrong please try again",R.color.dark_green);
                      }
                  });
               }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                prompMessage("Error","Something went wrong please try again",R.color.dark_green);
            }
    });
  }

    // custom prompt message
    public void prompMessage(String title, String message, int background){
        CookieBar.build(this)
                .setTitle(title)
                .setMessage(message)
                .setBackgroundColor(background)
                .setCookiePosition(CookieBar.TOP)
                .setDuration(5000)
                .show();
    }
}