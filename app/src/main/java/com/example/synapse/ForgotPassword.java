package com.example.synapse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import com.example.synapse.databinding.ActivityForgotPasswordBinding;
import com.example.synapse.screen.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ProgressBar;
import android.widget.Toast;


public class ForgotPassword extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    TextInputEditText etEmail;
    TextInputLayout tilEmail;
    ProgressDialog dialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        etEmail = findViewById(R.id.etEmail);
        tilEmail = findViewById(R.id.tilEmail);
        auth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(ForgotPassword.this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading....");

        binding.btnSubmit.setOnClickListener(v -> {
            forgotPassword();
        });

    }

    private Boolean validateEmail(){
        if(TextUtils.isEmpty(etEmail.getText())){
            binding.tilEmail.setError("This field can't be empty");
            binding.tilEmail.requestFocus();
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()){
            binding.tilEmail.setError("Invalid email. Please re-enter your email");
            binding.tilEmail.requestFocus();
            return false;
        }else{
           binding.tilEmail.setError(null);
           return true;
        }
    }

    private void forgotPassword(){
       if(!validateEmail()){
           return;
       }

       dialog.show();

       auth.sendPasswordResetEmail(binding.etEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               dialog.dismiss();
               if(task.isSuccessful()){
                   startActivity(new Intent(ForgotPassword.this, Login.class));
                   finish();
                   Toast.makeText(ForgotPassword.this, "Please Check your Email Address!", Toast.LENGTH_SHORT).show();
               }else{
                   Toast.makeText(ForgotPassword.this, "Enter your correct email address", Toast.LENGTH_SHORT).show();
               }

           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(ForgotPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
           }
       });
    }
}