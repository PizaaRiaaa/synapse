package com.example.synapse.screen.carer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.synapse.R;
import com.example.synapse.databinding.ActivityCarerBottomNavigationBinding;
import com.example.synapse.screen.carer.modules.HomeFragment;
import com.example.synapse.screen.util.ReplaceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.Bundle;

public class BottomNavigation extends AppCompatActivity {

    ActivityCarerBottomNavigationBinding binding;
    ReplaceFragment replaceFragment = new ReplaceFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_carer_home);
        binding = ActivityCarerBottomNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment.replaceFragment(new HomeFragment(),BottomNavigation.this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // set bottomNavigationView to transparent
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.miHome:
                    replaceFragment.replaceFragment(new HomeFragment(),BottomNavigation.this);
                    break;
                case R.id.miChat:
                    break;
                case R.id.miProfile:
                    break;
                case R.id.miSettings:
                    break;
            }
            return true;
        });
    }

}