package com.example.synapse.screen.carer.register;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import com.google.android.material.button.MaterialButton;
import android.os.Bundle;

public class TermsAndDataPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_data_policy);

        MaterialButton btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v ->
                        finish());
    }
}