package com.example.synapse.screen.senior.modules.view;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import com.example.synapse.screen.senior.games.TicTacToe;
import com.google.android.material.button.MaterialButton;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class TicTacToeHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe_start);

        MaterialButton btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> startActivity(new Intent(this, TicTacToe.class)));

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

    }
}