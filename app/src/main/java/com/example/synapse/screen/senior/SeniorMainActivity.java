package com.example.synapse.screen.senior;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.health.connect.client.HealthConnectClient;
import androidx.health.connect.client.permission.HealthPermission;
import androidx.health.connect.client.records.HeartRateRecord;

import com.example.synapse.R;
import com.example.synapse.databinding.ActivityCarerBottomNavigationBinding;
import com.example.synapse.screen.carer.SelectSenior;
import com.example.synapse.screen.senior.games.MathGame;
import com.example.synapse.screen.senior.games.TriviaQuiz;
import com.example.synapse.screen.senior.modules.fragments.GamesFragment;
import com.example.synapse.screen.senior.modules.fragments.HomeFragment;
import com.example.synapse.screen.senior.modules.fragments.MedicationFragment;
import com.example.synapse.screen.senior.modules.fragments.PhysicalActivityFragment;
import com.example.synapse.screen.senior.modules.fragments.SettingsFragment;
import com.example.synapse.screen.senior.modules.view.TicTacToeHome;
import com.example.synapse.screen.util.ReplaceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.Permission;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SeniorMainActivity extends AppCompatActivity {

    ActivityCarerBottomNavigationBinding binding;
    ReplaceFragment replaceFragment = new ReplaceFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCarerBottomNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView bottomNavigationView;
        FloatingActionButton floatingActionButton;

        replaceFragment.replaceFragment(new HomeFragment(), SeniorMainActivity.this);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        floatingActionButton = findViewById(R.id.fabLocateSenior);


        // ==================================================
        // key for notification button content intent
        String med_key = getIntent().getStringExtra("med_key");
        String phy_key = getIntent().getStringExtra("phy_key");
        String game_tag = getIntent().getStringExtra("game_tag");

        MedicationFragment medicationFragment = new MedicationFragment();
        Bundle args1 = new Bundle();
        args1.putString("key", med_key);
        medicationFragment.setArguments(args1);

        PhysicalActivityFragment physicalActivityFragment = new PhysicalActivityFragment();
        Bundle args2 = new Bundle();
        args2.putString("key", phy_key);
        physicalActivityFragment.setArguments(args2);


        if (med_key != null) {
            FragmentManager fragmentManager = ((FragmentActivity) this).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, medicationFragment);
            fragmentTransaction.commit();
        }

        if (phy_key != null) {
            FragmentManager fragmentManager = ((FragmentActivity) this).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, physicalActivityFragment);
            fragmentTransaction.commit();

        }

        if (game_tag != null) {
            if(game_tag.equals("Tic-tac-toe"))
            startActivity(new Intent(SeniorMainActivity.this, TicTacToeHome.class));
         else if (game_tag.equals("TriviaQuiz"))
            startActivity(new Intent(SeniorMainActivity.this, TriviaQuiz.class));
         else if(game_tag.equals("MathGame"))
            startActivity(new Intent(SeniorMainActivity.this, MathGame.class));
        }

        // build a set of permissions for required data types

        if(HealthConnectClient.isAvailable(getApplicationContext())){
            Toast.makeText(this, "health connect is available", Toast.LENGTH_SHORT).show();
            HealthConnectClient healthConnectClient = HealthConnectClient.getOrCreate(getApplicationContext());
        }

        // ==================================================

        FirebaseMessaging.getInstance().subscribeToTopic("hello");

        floatingActionButton.setOnClickListener(v -> startActivity(new Intent(this, MyLocation.class)));

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.miHome:
                    replaceFragment.replaceFragment(new HomeFragment(), SeniorMainActivity.this);
                    break;
                case R.id.miChat:
                    break;
                case R.id.miProfile:
                    break;
                case R.id.miSettings:
                    replaceFragment.replaceFragment(new SettingsFragment(), SeniorMainActivity.this);
                    break;
            }
            return true;
        });
    }

}