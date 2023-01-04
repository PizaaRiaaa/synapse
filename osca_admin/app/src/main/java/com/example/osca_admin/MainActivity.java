package com.example.osca_admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.service.controls.actions.FloatAction;

import com.example.osca_admin.databinding.ActivityMainBinding;
import com.example.osca_admin.fragments.AuditTrailFragment;
import com.example.osca_admin.fragments.HomeFragment;
import com.example.osca_admin.fragments.SettingsFragment;
import com.example.osca_admin.util.ReplaceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FloatingActionButton btnAudit;
    ReplaceFragment replaceFragment;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment = new ReplaceFragment();
        replaceFragment.replaceFragment(new HomeFragment(), MainActivity.this);
        bottomNavigationView = findViewById(R.id.bottomNavigationViewAdmin);

        btnAudit = findViewById(R.id.fabAudit);
        btnAudit.setOnClickListener(v -> replaceFragment.replaceFragment(new AuditTrailFragment(), MainActivity.this));

        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        binding.bottomNavigationViewAdmin.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.miHome:
                    replaceFragment.replaceFragment(new HomeFragment(), MainActivity.this);
                    break;
                case R.id.miSettings:
                    replaceFragment.replaceFragment(new SettingsFragment(), MainActivity.this);
            }
            return true;
        });
    }
}