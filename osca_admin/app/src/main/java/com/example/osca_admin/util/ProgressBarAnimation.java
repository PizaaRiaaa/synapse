package com.example.osca_admin.util;

import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.osca_admin.LoadingScreen;
import com.example.osca_admin.MainActivity;

public class ProgressBarAnimation extends Animation {
    Context context;
    ProgressBar progressBar;
    TextView textView;
    float from;
    float to;

    public ProgressBarAnimation(Context context, ProgressBar progressBar, TextView textView, float from, float to){
        this.context = context;
        this.progressBar = progressBar;
        this.textView = textView;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t){
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
        textView.setText((int) value + " %");

        if(value == to){
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            ((LoadingScreen)(context)).finish();
        }
    }
}
