package com.example.synapse.screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import com.example.synapse.R;
import com.example.synapse.screen.carer.register.RegisterCarer;
import com.example.synapse.screen.senior.test.CheckCarerEmail;
import com.example.synapse.screen.senior.test.MCIpromptMessage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PickRole extends AppCompatActivity {
    public boolean isPressedCarer = false;
    public boolean isPressedSenior = false;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_role);

        ImageButton ivBack;
        TextView tvBack;
        Button btnContinue;
        ImageView ibCarer;
        ImageView ibSenior;

        // (back arrow) bring user back to login screen
        ivBack = findViewById(R.id.ibBack);
        ivBack.setOnClickListener(view -> startActivity(new Intent(PickRole.this, Login.class)));

        // (textview) bring back user to login screen
        tvBack = findViewById(R.id.tvAlreadyHaveAnAccount);
        tvBack.setOnClickListener(view -> startActivity(new Intent(PickRole.this, Login.class)));

        btnContinue = findViewById(R.id.btnContinue);
        ibCarer = findViewById(R.id.ibPickCarer);
        ibSenior = findViewById(R.id.ibPickSenior);

        // check if carer ImageButton was pressed
        ibCarer.setOnClickListener(v -> {
            btnContinue.setEnabled(true);
            ibCarer.setBackground(AppCompatResources.getDrawable(PickRole.this, R.drawable.rounded_button_pick_role));
            ibSenior.setBackground(AppCompatResources.getDrawable(PickRole.this, R.drawable.custom_button_selector));
            btnContinue.setOnClickListener(view -> startActivity(new Intent(PickRole.this, RegisterCarer.class)));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // check if senior ImageButton was pressed
        ibSenior.setOnClickListener(v -> {
            btnContinue.setEnabled(true);
            ibSenior.setBackground(AppCompatResources.getDrawable(PickRole.this, R.drawable.rounded_button_pick_role));
            ibCarer.setBackground(AppCompatResources.getDrawable(PickRole.this, R.drawable.custom_button_selector));
            btnContinue.setOnClickListener(view -> startActivity(new Intent(PickRole.this, MCIpromptMessage.class)));
            // btnContinue.setOnClickListener(view -> startActivity(new Intent(PickRole.this, CheckCarerEmail.class)));
        });


        // show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // transparent status bar
        if (Build.VERSION.SDK_INT >= 21) {
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
    }
}