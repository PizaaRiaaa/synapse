package com.example.synapse.screen.carer;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import com.example.synapse.screen.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CarerVerifyEmail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_verify_email);

        // direct user to login screen
        Button btnLogin = findViewById(R.id.btnLoginNow);
        btnLogin.setOnClickListener(view -> startActivity(new Intent(CarerVerifyEmail.this, Login.class)));


    }
}