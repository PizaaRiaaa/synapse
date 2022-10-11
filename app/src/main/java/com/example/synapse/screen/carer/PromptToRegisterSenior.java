package com.example.synapse.screen.carer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.synapse.R;
import com.example.synapse.screen.Login;
import com.example.synapse.screen.senior.test.MCIpromptMessage;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PromptToRegisterSenior extends AppCompatActivity {

    AppCompatButton btnLogout;
    MaterialButton btnStart;
    FirebaseAuth mAuth;

    void logout(){
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(PromptToRegisterSenior.this, Login.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }

    void redirectCarerToSeniorRegistration() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PromptToRegisterSenior.this)
                        .setTitle("Reminder")
                        .setMessage("We need to sign-out your account before you can register you senior")
                        .setPositiveButton("OK", (dialogInterface, i) -> registerPage())
                        .setCancelable(true)
                        .show();
            }
        });
    }

        void registerPage(){
            mAuth.signOut();
            startActivity(new Intent(PromptToRegisterSenior.this, MCIpromptMessage.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_to_register_senior);
        mAuth = FirebaseAuth.getInstance();
        btnLogout = findViewById(R.id.btnLogout);
        btnStart = findViewById(R.id.btnStart);

        redirectCarerToSeniorRegistration();
        logout();
    }
}