package com.example.synapse.screen.senior.test;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import com.example.synapse.screen.Login;
import com.example.synapse.screen.PickRole;
import com.example.synapse.screen.senior.RegisterSenior;
import com.google.android.material.button.MaterialButton;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MCIpromptMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcipromp_message);

        ImageButton ibBack = findViewById(R.id.ibBack);
        MaterialButton btnProceed = findViewById(R.id.btnProceed);

        ibBack.setOnClickListener(v -> startActivity(new Intent(this, PickRole.class)));
        btnProceed.setOnClickListener(v -> startActivity(new Intent(this, MCItest.class)));

    }


}