package com.example.synapse.screen.admin;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingScreen extends AppCompatActivity {

    ProgressBar progressBar;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.tvProgressBar);

        progressBar.setMax(100);
        progressBar.setScaleY(3f);

        progressBarAnimation();
    }

    void progressBarAnimation(){
        ProgressBarAnimation animation = new ProgressBarAnimation(this, progressBar, textView, 0f, 100f);
        animation.setDuration(8000);
        progressBar.setAnimation(animation);

    }
}