package com.example.synapse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.synapse.screen.Login;
import com.example.synapse.screen.Onboarding;
import com.example.synapse.screen.carer.CarerHome;
import com.example.synapse.screen.carer.SendRequest;
import com.example.synapse.screen.senior.SeniorHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class Splashscreen extends AppCompatActivity {

    // firebase reference
    private FirebaseAuth mAuth;
    private DatabaseReference referenceUser, referenceRequest, referenceCompanion;
    private String userType, checkStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // display on fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        // extracting user reference from firebase nodes
        mAuth = FirebaseAuth.getInstance();
        referenceUser = FirebaseDatabase.getInstance().getReference("Users");
        referenceRequest = FirebaseDatabase.getInstance().getReference("Request");
        referenceCompanion = FirebaseDatabase.getInstance().getReference("Companion");

        // initialize animation variables
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        ImageView image = findViewById(R.id.imageView);
        image.setAnimation(topAnim);

        // display splashscreen
        new Handler().postDelayed(() -> {

            SharedPreferences settings = getSharedPreferences("prefs", 0);
            boolean firstRun = settings.getBoolean("firstRun", false);

            if (!firstRun) // if installed for the first time, then display on-boarding screen
            {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("firstRun", true);
                editor.apply();
                startActivity(new Intent(Splashscreen.this, Onboarding.class));
                finish();

            }else if(mAuth.getCurrentUser() == null) {  // prevent display on-boarding screen
                startActivity(new Intent(Splashscreen.this, Login.class));
                finish();
            }
        }, 2000); // splash screen duration
    }

    // check if user is already logged in, then direct to their respective home screen
    @Override
    protected void onStart(){
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            referenceUser.child(Objects.requireNonNull(mAuth.getUid())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // retrieve current user's userType
                    userType = Objects.requireNonNull(snapshot.child("userType").getValue()).toString();

                    // check if current user is senior, carer or admin
                    if(userType.equals("Senior")){
                        startActivity(new Intent(Splashscreen.this, SeniorHome.class));
                        finish();

                    }else if(userType.equals("Carer")) {
                        // check if carer send request to senior
                        referenceRequest.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                               if(snapshot.exists()){
                                   for(DataSnapshot ds : snapshot.getChildren()){

                                       // retrieve current request status
                                       checkStatus = ds.child("status").getValue().toString();

                                       if(checkStatus.equals("pending")){
                                           startActivity(new Intent(Splashscreen.this, CarerHome.class));
                                           finish();
                                       }else{ // it means senior decline the carer request
                                           Toast.makeText(Splashscreen.this, "Sorry but senior decline your request." +
                                                   " Please send a request again.", Toast.LENGTH_LONG).show();
                                           startActivity(new Intent(Splashscreen.this, SendRequest.class));
                                           finish();
                                       }
                                   }
                               }else{ // if carer doesn't send request to senior, then redirect carer to SendRequest screen
                                   startActivity(new Intent(Splashscreen.this, SendRequest.class));
                               }
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(Splashscreen.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        // check if carer and senior already companion
                        referenceCompanion.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    startActivity(new Intent(Splashscreen.this, CarerHome.class));
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(Splashscreen.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Splashscreen.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
 }

