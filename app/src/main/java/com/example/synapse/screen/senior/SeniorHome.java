package com.example.synapse.screen.senior;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.example.synapse.R;
import com.example.synapse.screen.senior.dashboard.GameDashboard;
import com.example.synapse.screen.senior.dashboard.MedicationDashboard;
import com.example.synapse.screen.util.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class SeniorHome extends AppCompatActivity {

    private static final String TAG = "";
    private DatabaseReference referenceProfile;
    private FirebaseUser mUser;
    private String token;
    private TextView tvSeniorName;
    private AppCompatImageView ivProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_home);

        TextClock currentTime = findViewById(R.id.tcTime);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        FloatingActionButton fabBtnMyLocation = findViewById(R.id.fabLocateSenior);
        MaterialCardView btnGames = findViewById(R.id.btnGames);
        MaterialCardView btnSearchPeople = findViewById(R.id.btnSearchPeople);
        ivProfilePic = findViewById(R.id.ivSeniorProfilePic);
        tvSeniorName = findViewById(R.id.tvSeniorName);

        referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = mUser.getUid();

        FirebaseMessaging.getInstance().subscribeToTopic("hello");

        // Show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fabBtnMyLocation.setOnClickListener(v -> startActivity(new Intent(SeniorHome.this, MyLocation.class)));

        // get current date
        //Date c = Calendar.getInstance().getTime();
        //SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
        //String formattedDate = df.format(c);

        // set bottomNavigationView to transparent
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));


        // direct user to medication dashboard
        //medicationBtn.setOnClickListener(view -> startActivity(new Intent(SeniorHome.this, MedicationDashboard.class)));

        // direct user to games dashboard
        btnGames.setOnClickListener(view -> startActivity(new Intent(SeniorHome.this, GameDashboard.class)));

        // direct user to search people
        btnSearchPeople.setOnClickListener(view -> startActivity(new Intent(SeniorHome.this, SearchPeople.class)));

        // display current time
        currentTime.setFormat12Hour("hh:mm a");

        // display senior profile picture
        showUserProfile(userID);

    }

    // retrieve and update token
    @Override
    public void onStart() {
        super.onStart();
        HashMap hashMap = new HashMap();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // generate token everytime user access the app
                        // Get new FCM registration token
                        token = task.getResult();
                        hashMap.put("token", token);
                        referenceProfile.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                            }
                        });
                        // Log and toast
                        String msg = token;
                        Log.d("Token:", msg);
                    }
                });
    }

    // retrieve senior's profile picture
    public void showUserProfile(String firebaseUser){
        referenceProfile.child(firebaseUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ReadWriteUserDetails userProfile = snapshot.getValue(ReadWriteUserDetails.class);
                    if(userProfile != null){
                        String name = userProfile.fullName;
                        int firstName = name.indexOf(" ");
                        tvSeniorName.setText("Hello,\n" + name.substring(0, firstName ).toString());

                        // display carer profile pic
                        Uri uri = mUser.getPhotoUrl();
                        Picasso.get()
                                .load(uri)
                                .transform(new CropCircleTransformation())
                                .into(ivProfilePic);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SeniorHome.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}