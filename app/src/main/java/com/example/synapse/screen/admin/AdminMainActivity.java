package com.example.synapse.screen.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.synapse.R;
import com.example.synapse.databinding.ActivityAdminBottomNavigationBinding;
import com.example.synapse.screen.admin.fragments.HomeFragment;
import com.example.synapse.screen.admin.fragments.SettingsFragment;
import com.example.synapse.screen.util.ReplaceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Bundle;

public class AdminMainActivity extends AppCompatActivity {

    ActivityAdminBottomNavigationBinding binding;
    ReplaceFragment replaceFragment = new ReplaceFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBottomNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment.replaceFragment(new HomeFragment(), AdminMainActivity.this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewAdmin);

        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        binding.bottomNavigationViewAdmin.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.miHome:
                    replaceFragment.replaceFragment(new HomeFragment(), AdminMainActivity.this);
                    break;
                case R.id.miSettings:
                    replaceFragment.replaceFragment(new SettingsFragment(), AdminMainActivity.this);
            }
            return true;
        });

    }
}