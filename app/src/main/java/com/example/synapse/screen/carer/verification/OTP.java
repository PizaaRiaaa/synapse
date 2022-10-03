package com.example.synapse.screen.carer.verification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import com.example.synapse.screen.carer.RegisterCarer;
import com.example.synapse.screen.util.PromptMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OTP extends AppCompatActivity {

    private EditText input1,input2,input3,input4,input5,input6;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    PromptMessage promptMessage = new PromptMessage();
    TextView tvResendOTP, tvCountdown;
    CountDownTimer countDownTimer;
    long timeLeftInMilliseconds = 60000; // 60 seconds
    String OTPid, mobile;
    ProgressBar progressBar;
    MaterialButton btnVerify;

    private void setUpOTPInputs(){
        input1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    input2.requestFocus();

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        input2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    input3.requestFocus();

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        input3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    input4.requestFocus();

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        input4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    input5.requestFocus();

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        input5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    input6.requestFocus();

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    boolean checkIfEmpty(EditText et){
        return TextUtils.isEmpty(et.getText().toString().trim());
    }

    void sendOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+63" + mobile,
                60,
                TimeUnit.SECONDS,
                OTP.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        progressBar.setVisibility(View.GONE);
                        btnVerify.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        btnVerify.setVisibility(View.VISIBLE);
                        Toast.makeText(OTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        startTime();
                        progressBar.setVisibility(View.VISIBLE);
                        btnVerify.setVisibility(View.VISIBLE);
                        OTPid = verificationId;
                    }
                }
        );
    }

       void checkVerification(){
            btnVerify.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(checkIfEmpty(input1) || checkIfEmpty(input2) || checkIfEmpty(input3)
                            || checkIfEmpty(input4) || checkIfEmpty(input5) || checkIfEmpty(input6)){
                        promptMessage.displayMessage("Error OTP", "Please enter the valid code", R.color.red1, OTP.this);
                        return;
                    }

                    String code =
                            input1.getText().toString() + input2.getText().toString() +
                                    input3.getText().toString() + input4.getText().toString() +
                                    input5.getText().toString() + input6.getText().toString();

                    if(OTPid != null){

                        progressBar.setVisibility(View.VISIBLE);
                        btnVerify.setVisibility(View.INVISIBLE);
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                                OTPid,
                                code
                        );

                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                btnVerify.setVisibility(View.VISIBLE);
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(getApplicationContext(), Email.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    auth.signOut();
                                }else{
                                    promptMessage.displayMessage("Invalid OTP", "The verification code entered was invalid", R.color.red1, OTP.this);
                                }
                            }
                        });
                    }
                }
            });
    }

    void startTime(){
       countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
           @Override
           public void onTick(long l) {
               timeLeftInMilliseconds = l;
               updateTime();
           }

           @Override
           public void onFinish() {

           }
       }.start();
    }

    void updateTime(){
        int seconds = (int) timeLeftInMilliseconds % 60000 / 1000;

        String timeLeftText;

        timeLeftText = "" + seconds + "s";

         if(seconds == 0){
             timeLeftText = "" + seconds;
             tvResendOTP.setVisibility(View.VISIBLE);
             timeLeftInMilliseconds = 60000;
             startTime();
         }

        tvCountdown.setText(timeLeftText);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        auth.signOut();
    }

    private boolean isAppRunning() {
        ActivityManager m = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =  m.getRunningTasks(10);
        Iterator<ActivityManager.RunningTaskInfo> itr = runningTaskInfoList.iterator();
        int n=0;
        while(itr.hasNext()){
            n++;
            itr.next();
        }
        if(n==1){ // App is killed
            auth.signOut();
            return false;
        }

        return true; // App is in background or foreground
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_otp);

        progressBar = findViewById(R.id.progressBarOTP);
        btnVerify = findViewById(R.id.btnVerifyOTP);
        tvResendOTP = findViewById(R.id.tvResendOTP);
        tvCountdown = findViewById(R.id.tvCountDown);

        input1 = findViewById(R.id.input1);
        input2 = findViewById(R.id.input2);
        input3 = findViewById(R.id.input3);
        input4 = findViewById(R.id.input4);
        input5 = findViewById(R.id.input5);
        input6 = findViewById(R.id.input6);

        // show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle extras = getIntent().getExtras();
        mobile  = extras.getString("Mobile");

        setUpOTPInputs();
        sendOTP();
        checkVerification();
        isAppRunning();

        tvResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvResendOTP.setVisibility(View.GONE);
                sendOTP();
            }
        });

    }
}