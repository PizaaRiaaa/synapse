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

import com.example.synapse.screen.Landing;
import com.example.synapse.screen.Login;
import com.example.synapse.screen.Onboarding;
import com.example.synapse.screen.admin.AdminMainActivity;
import com.example.synapse.screen.admin.LoadingScreen;
import com.example.synapse.screen.carer.CarerMainActivity;
import com.example.synapse.screen.carer.SelectSenior;
import com.example.synapse.screen.carer.SendRequest;
import com.example.synapse.screen.carer.verification.OTP;
import com.example.synapse.screen.senior.SeniorMainActivity;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class Splashscreen extends AppCompatActivity {

    // Global variables
    PromptMessage promptMessage = new PromptMessage();
    private FirebaseAuth mAuth;
    private DatabaseReference referenceCarer, referenceSenior, referenceAdmin;
    private String userType, checkStatus;

    // check if user is already logged in, then direct to their respective home screen
    @Override
    protected void onStart(){
        super.onStart();
        if (mAuth.getCurrentUser() != null) {

            referenceCarer.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if(snapshot.exists()){
                       startActivity(new Intent(Splashscreen.this, SelectSenior.class));
                       overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                       finish();
                   }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

           referenceSenior.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {

                   if(snapshot.exists()){
                       startActivity(new Intent(Splashscreen.this, SeniorMainActivity.class));
                       overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                       finish();
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });

            referenceAdmin.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                        ReadWriteUserDetails rd = snapshot.getValue(ReadWriteUserDetails.class);
                        String userType = rd.getUserType();

                        if(userType.equals("Admin")){
                            startActivity(new Intent(Splashscreen.this, LoadingScreen.class));
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // display on fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        // extracting user reference from firebase nodes
        mAuth = FirebaseAuth.getInstance();
        referenceAdmin = FirebaseDatabase.getInstance().getReference("Users").child("Carers");
        referenceCarer = FirebaseDatabase.getInstance().getReference("Users").child("Carers");
        referenceSenior = FirebaseDatabase.getInstance().getReference("Users").child("Seniors");

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
                startActivity(new Intent(Splashscreen.this, Landing.class));
                finish();

            }else if(mAuth.getCurrentUser() == null) {  // prevent display on-boarding screen
                startActivity(new Intent(Splashscreen.this, Login.class));
                finish();
            }
        }, 2000); // splash screen duration
    }

 }

