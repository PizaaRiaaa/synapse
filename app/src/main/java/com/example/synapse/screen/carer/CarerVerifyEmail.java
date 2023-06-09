package com.example.synapse.screen.carer;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import com.example.synapse.screen.Login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class CarerVerifyEmail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_verify2_email);

        // direct user to login screen
        Button btnLogin = findViewById(R.id.btnLoginNow);
        btnLogin.setOnClickListener(view -> startActivity(new Intent(CarerVerifyEmail.this, Login.class)));


    }
}