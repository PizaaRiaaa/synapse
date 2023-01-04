package com.example.osca_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.MimeTypeFilter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.osca_admin.util.PromptMessage;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    DatabaseReference referenceAdmin;
    TextInputEditText etUsername;
    TextInputEditText etPassword;
    String username, password;
    MaterialButton btnLogin;
    PromptMessage promptMessage = new PromptMessage();

   void signInAdmin(String username, String password){
      if(username != null && password != null) {
          referenceAdmin.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  for (DataSnapshot adminKey : snapshot.getChildren()) {
                      referenceAdmin.child(adminKey.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot snapshot) {
                              for (DataSnapshot ignored : snapshot.getChildren()) {
                                  String adminUser = snapshot.child("userName").getValue().toString();
                                  String adminPass = snapshot.child("password").getValue().toString();

                                  if(username.equals(adminUser) && password.equals(adminPass)) {
                                     // SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                                     // SharedPreferences.Editor editor = sharedPref.edit();
                                     // editor.putString("userKey", adminKey.getKey());
                                     // editor.apply();

                                      setDefaults("userKey", adminKey.getKey(), getApplicationContext());
                                      startActivity(new Intent(Login.this, LoadingScreen.class));
                                      overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                      finish();
                                  }else {
                                      promptMessage.displayMessage(
                                              "Error",
                                              "Invalid credentials. Kindly, check and re-enter",
                                              R.color.red_decline_request,
                                              Login.this);
                                  }
                              }
                          }
                          @Override
                          public void onCancelled(@NonNull DatabaseError error) {
                              promptMessage.defaultErrorMessage(Login.this);
                          }
                      });
                  }
              }
              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                  promptMessage.defaultErrorMessage(Login.this);
              }
          });
      }
    }

    public void authenticateUser(){
       username = etUsername.getText().toString();
       password = etPassword.getText().toString();

       if(TextUtils.isEmpty(username)){
           promptMessage.displayMessage(
                  "Empty field",
                  "Please enter your username",
                   R.color.red1,
                  Login.this);
           etUsername.requestFocus();
       }else if(TextUtils.isEmpty(password)){
           promptMessage.displayMessage(
                   "Empty field",
                   "Please enter your password",
                   R.color.red1,
                   Login.this);
           etPassword.requestFocus();
       } else{
           signInAdmin(username, password);
       }
    }


    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public void checkSharedPreference(){
       if(getDefaults("userKey", this) != null){
          startActivity(new Intent(Login.this, LoadingScreen.class));
          finish();
       }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // force app to light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        transparentStatusBar();

        checkSharedPreference();

        referenceAdmin = FirebaseDatabase.getInstance()
                .getReference("Users").child("Admins");

        btnLogin.setOnClickListener(v -> authenticateUser());
    }
}