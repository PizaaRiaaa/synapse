package com.example.synapse.screen;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class MCIpromptMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcipromp_message);

        ImageButton ibBack = findViewById(R.id.ibBack);
        ibBack.setOnClickListener(v -> startActivity(new Intent(this, PickRole.class)));

    }
}