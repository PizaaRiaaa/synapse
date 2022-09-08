package com.example.synapse.screen.carer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.synapse.R;
import com.example.synapse.databinding.ActivityCarerBottomNavigationBinding;
import com.example.synapse.screen.carer.modules.fragments.HomeFragment;
import com.example.synapse.screen.carer.modules.fragments.SettingsFragment;
import com.example.synapse.screen.util.ReplaceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    ActivityCarerBottomNavigationBinding binding;
    ReplaceFragment replaceFragment = new ReplaceFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCarerBottomNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment.replaceFragment(new HomeFragment(), MainActivity.this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.miHome:
                    replaceFragment.replaceFragment(new HomeFragment(), MainActivity.this);
                    break;
                case R.id.miChat:
                    break;
                case R.id.miProfile:
                    break;
                case R.id.miSettings:
                    replaceFragment.replaceFragment(new SettingsFragment(), MainActivity.this);
                    break;
            }
            return true;
        });
    }

}