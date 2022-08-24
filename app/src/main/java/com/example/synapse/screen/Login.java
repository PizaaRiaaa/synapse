package com.example.synapse.screen;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.synapse.R;
import com.example.synapse.screen.carer.CarerVerifyEmail;
import com.example.synapse.screen.carer.CarerHome;
import com.example.synapse.screen.carer.SendRequest;
import com.example.synapse.screen.senior.SeniorHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Login extends AppCompatActivity {

    private static final String TAG = "loginActivity";
    private DatabaseReference referenceUser, referenceRequest, referenceCompanion;
    private EditText etEmail, etPassword;
    private FirebaseAuth mAuth;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvSwitchToPickRole = findViewById(R.id.tvRegister);
        TextView tvForgotPass = findViewById(R.id.tvForgotPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        mAuth = FirebaseAuth.getInstance();

        referenceUser = FirebaseDatabase.getInstance().getReference("Users");
        referenceRequest = FirebaseDatabase.getInstance().getReference("Request");
        referenceCompanion = FirebaseDatabase.getInstance().getReference("Companion");

        // authenticate user
        btnLogin.setOnClickListener(view -> {
            String textEmail = etEmail.getText().toString();
            String textPassword = etPassword.getText().toString();

            if(TextUtils.isEmpty(textEmail)){
                Toast.makeText(Login.this, "Please enter your email", Toast.LENGTH_LONG).show();
                etEmail.setError("Email is required");
                etEmail.requestFocus();
            }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                Toast.makeText(Login.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                etEmail.setError("Valid email is required");
                etPassword.requestFocus();
            }else if(TextUtils.isEmpty(textPassword)){
                Toast.makeText(Login.this, "Please enter your password", Toast.LENGTH_LONG).show();
                etPassword.setError("Password is required");
                etPassword.requestFocus();
            }else{
                loginUser(textEmail, textPassword);
            }
     });

        // proceed to PickRole screen
        tvSwitchToPickRole.setOnClickListener(view -> startActivity(new Intent(Login.this, PickRole.class)));

        // proceed to ForgotPassword screen
        tvForgotPass.setOnClickListener(view -> startActivity(new Intent(Login.this, CarerVerifyEmail.class)));

        // change substring color
        @SuppressLint("CutPasteId") TextView tvRegister = findViewById(R.id.tvRegister);
        String text = "Don't have an account? Register!";
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ForegroundColorSpan light_green = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.light_green));
        ssb.setSpan(light_green, 23, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegister.setText(ssb);

        // show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // transparent status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.grey4));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.grey4));
    }

    // check user credentials
    private void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
                // get instance of the current user
                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                // check if email is verified
                if(firebaseUser.isEmailVerified()){

                    String userID = firebaseUser.getUid();

                    referenceUser.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            // check if current user is senior, carer or admin
                            userType = snapshot.child("userType").getValue().toString();

                            if(userType.equals("Carer")){

                                // check if carer already send request to senior
                                referenceRequest.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if(snapshot.exists()){
                                            Toast.makeText(Login.this, "You are logged in now", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(Login.this, CarerHome.class));
                                            finish();
                                        }else{
                                            startActivity(new Intent(Login.this, SendRequest.class));
                                            finish();
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(Login.this, "Semething went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                // else check if carer and senior are already companion
                                referenceCompanion.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if(snapshot.exists()){
                                            Toast.makeText(Login.this, "You are logged in now", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(Login.this, CarerHome.class));
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(Login.this, "Semething went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else if(userType.equals("Senior")){
                                Toast.makeText(Login.this, "You are logged in now", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Login.this, SeniorHome.class));
                                finish();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Login.this, "Something went wrong! Please try again.", Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    firebaseUser.sendEmailVerification();
                    mAuth.signOut();
                    showAlertDialog();
                }
            }else {
                try {
                    throw task.getException();
                } catch (FirebaseAuthInvalidUserException e) {
                    etEmail.setError("User does not exists. Please register again.");
                    etEmail.requestFocus();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    etPassword.setError("Invalid credentials. Kindly, check and re-enter.");
                    etPassword.requestFocus();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
              }
            }
        });
    }

    private void showAlertDialog(){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You can not login without email verification.");

        // open email app if user clicks/taps continue
        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // open email app in new window and not within our app
            startActivity(intent);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
 }