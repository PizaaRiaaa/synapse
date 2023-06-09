package com.example.synapse.screen;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.android.material.button.MaterialButton;

public class Landing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // show top status bar
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        MaterialButton btnProceed = findViewById(R.id.btnProceed);
        btnProceed.setOnClickListener(v -> {
            startActivity(new Intent(this, Onboarding.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}