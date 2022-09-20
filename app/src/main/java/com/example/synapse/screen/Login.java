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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.synapse.R;
import com.example.synapse.screen.carer.CarerVerifyEmail;
import com.example.synapse.screen.carer.CarerMainActivity;
import com.example.synapse.screen.carer.SendRequest;
import com.example.synapse.screen.senior.SeniorMainActivity;
import com.example.synapse.screen.util.PromptMessage;
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
import java.util.Objects;


public class Login extends AppCompatActivity {

    PromptMessage promptMessage = new PromptMessage();
    static final String TAG = "loginActivity";
    DatabaseReference referenceUser, referenceRequest, referenceCompanion;
    EditText etEmail, etPassword;
    FirebaseAuth mAuth;
    String userType;

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

        // show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // make status bar transparent
        transparentStatusBar();

        // proceed to PickRole screen
        tvSwitchToPickRole.setOnClickListener(view -> startActivity(new Intent(Login.this, PickRole.class)));

        // proceed to ForgotPassword screen
        tvForgotPass.setOnClickListener(view -> startActivity(new Intent(Login.this, CarerVerifyEmail.class)));

        // change substring color
        changeColor();

        // authenticate user
        btnLogin.setOnClickListener(v -> authenticateUser());

    }

   void authenticateUser(){
        String textEmail = etEmail.getText().toString();
        String textPassword = etPassword.getText().toString();
        if(TextUtils.isEmpty(textEmail)){
            promptMessage.displayMessage("Empty field", "Please enter your email", R.color.red1, Login.this);
            etEmail.requestFocus();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
            promptMessage.displayMessage("Invalid email", "Please enter your email", R.color.dark_green, Login.this);
            etPassword.requestFocus();
        }else if(TextUtils.isEmpty(textPassword)){
            promptMessage.displayMessage("Empty field", "Please enter your password", R.color.dark_green, Login.this);
            etPassword.requestFocus();
        }else{
            loginUser(textEmail, textPassword);
        }
    }

   void loginUser(String email, String password){
       // check user credentials
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                assert firebaseUser != null;

                if(firebaseUser.isEmailVerified()){ // check if user's email is verified
                    String userID = firebaseUser.getUid();
                    referenceUser.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            userType = snapshot.child("userType").getValue().toString(); // check if current user is senior, carer or admin
                            if(userType.equals("Carer")){
                                referenceRequest.child(userID).addListenerForSingleValueEvent(new ValueEventListener() { // check if carer is already send request to senior
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if(snapshot.exists()){
                                            startActivity(new Intent(Login.this, CarerMainActivity.class));
                                            finish();
                                        }else{
                                            startActivity(new Intent(Login.this, SendRequest.class));
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        promptMessage.defaultErrorMessage(Login.this);
                                    }
                                });

                                // else check if carer and senior are already companion
                                referenceCompanion.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            startActivity(new Intent(Login.this, CarerMainActivity.class));
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        promptMessage.defaultErrorMessage(Login.this);
                                    }
                                });

                            }else if(userType.equals("Senior")){
                                startActivity(new Intent(Login.this, SeniorMainActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            promptMessage.defaultErrorMessage(Login.this);
                        }
                    });

                }else{
                    firebaseUser.sendEmailVerification();
                    mAuth.signOut();
                    showAlertDialog();
                }

            }else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthInvalidUserException e) {
                    promptMessage.displayMessage("Error", "User does not exists. Please try again", R.color.red_decline_request, Login.this);
                    etEmail.requestFocus();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    promptMessage.displayMessage("Error", "Invalid credentials. Kindly, check and re-enter", R.color.red_decline_request, Login.this);
                    etPassword.requestFocus();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    promptMessage.displayMessage("Error", e.getMessage(), R.color.red_decline_request, Login.this);
                 }
               }
            }
        });
    }

    // display dialog if user's email is not verified
   void showAlertDialog(){
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

    // transparent status bar
    void transparentStatusBar(){
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

    // change the color of textview "Register"
    void changeColor(){
        // change substring color
        @SuppressLint("CutPasteId") TextView tvRegister = findViewById(R.id.tvRegister);
        String text = "Don't have an account? Register!";
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ForegroundColorSpan light_green = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.light_green));
        ssb.setSpan(light_green, 23, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegister.setText(ssb);
    }

 }