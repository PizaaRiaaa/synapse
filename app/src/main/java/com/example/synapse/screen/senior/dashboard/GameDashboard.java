package com.example.synapse.screen.senior.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.synapse.R;
import com.example.synapse.screen.senior.games.BrainTrainer;


public class GameDashboard extends AppCompatActivity {

    private Button game1Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_dashboard);

        // game 1 reference id
        game1Btn = findViewById(R.id.game1Btn);

        // onclick listener to redirect to brain trainer game
        game1Btn.setOnClickListener(view -> {
            // BrainTrainer game
            Intent intent = new Intent(this, BrainTrainer.class);
            startActivity(intent);
        });
    }
}