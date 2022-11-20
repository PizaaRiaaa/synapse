package com.example.synapse.screen.senior;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.synapse.R;
import com.example.synapse.databinding.ActivityCarerBottomNavigationBinding;
import com.example.synapse.screen.carer.SelectSenior;
import com.example.synapse.screen.senior.modules.fragments.HomeFragment;
import com.example.synapse.screen.senior.modules.fragments.MedicationFragment;
import com.example.synapse.screen.senior.modules.fragments.PhysicalActivityFragment;
import com.example.synapse.screen.senior.modules.fragments.SettingsFragment;
import com.example.synapse.screen.util.ReplaceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;

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

        // medicine
        String key = getIntent().getStringExtra("key");
        MedicationFragment medicationFragment = new MedicationFragment();
        Bundle args = new Bundle();
        args.putString("key",key);
        medicationFragment.setArguments(args);
        if(key != null){
            FragmentManager fragmentManager = ((FragmentActivity) this).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, medicationFragment);
            fragmentTransaction.commit();
        }

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