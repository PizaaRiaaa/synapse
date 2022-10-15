package com.example.synapse.screen.senior.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import com.example.synapse.R;
import com.example.synapse.screen.senior.RegisterSenior;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckCarerEmail extends AppCompatActivity {
    PromptMessage promptMessage = new PromptMessage();
    TextInputEditText text_email;

    void registerButton(){
        MaterialButton btn = findViewById(R.id.btnRegisterSenior);
        btn.setOnClickListener(v -> {
            validateEmail();
        });
    }

    void validateEmail(){
        text_email = findViewById(R.id.etEmail);
        String email = text_email.getText().toString();
        if(TextUtils.isEmpty(text_email.getText())){
            promptMessage.displayMessage(
                    "Empty field",
                    "Please enter your email.", R.color.red1,
                    CheckCarerEmail.this);
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            promptMessage.displayMessage(
                    "Invalid Email Format",
                    "Please enter your email again", R.color.red1,
                    CheckCarerEmail.this);
        }else{
            checkCarerEmail(email);
        }
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    void checkCarerEmail(String email){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Carers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    ReadWriteUserDetails rw = snapshot1.getValue(ReadWriteUserDetails.class);
                    if(encodeUserEmail(rw.getEmail()).equals(encodeUserEmail(email))){
                        String carerID = snapshot1.getKey();
                        Toast.makeText(CheckCarerEmail.this, "You can now register your senior's account", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CheckCarerEmail.this, RegisterSenior.class);
                        Bundle data = new Bundle();
                        data.putString("carerID", carerID);
                        intent.putExtras(data);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }else {
                       // promptMessage.displayMessage(
                       //         "Oops!",
                       //         "Your email does not exist. Please try again.", R.color.red1,
                       //         CheckCarerEmail.this);
                       // startActivity(new Intent(CheckCarerEmail.this, CheckCarerEmail.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_carer_email);

        registerButton();
    }
}