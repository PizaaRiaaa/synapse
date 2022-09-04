package com.example.synapse.screen.carer.modules.view;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import android.os.Bundle;
import android.widget.Toast;

public class Sample extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        //String getKey = getIntent().getStringExtra("MedicationID");
        //Toast.makeText(this,"id: " + getKey,Toast.LENGTH_SHORT).show();
    }
}