package com.example.synapse.screen.senior.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import com.example.synapse.R;
import com.example.synapse.screen.senior.SeniorHome;
import com.example.synapse.screen.senior.games.MathGame;
import com.example.synapse.screen.senior.games.TicTacToe;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class GameDashboard extends AppCompatActivity {

    MaterialCardView btnMath,btnTicTacToe;
    ImageButton ibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_dashboard);

        ibBack = findViewById(R.id.ibBack);
        btnMath = findViewById(R.id.btnMath);
        btnTicTacToe = findViewById(R.id.btnTicTacToe);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // set bottomNavigationView to transparent
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        // redirect user to math screen
        btnMath.setOnClickListener(view -> startActivity(new Intent(this, MathGame.class)));

        // redirect user to tic tac toe screen
        btnTicTacToe.setOnClickListener(v -> startActivity(new Intent(this, TicTacToe.class)));

        // redirect user to senior home scree
        ibBack.setOnClickListener(v -> startActivity(new Intent(this, SeniorHome.class)));

    }
}