package com.example.synapsewear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageButton;

public class LowBP extends AppCompatActivity {

    MediaPlayer beepSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_bp);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        beepSound = MediaPlayer.create(this, R.raw.beep);
        beepSound.setLooping(true);
        beepSound.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                //  Log.d(DEBUG_TAG,"Action was DOWN");
                //  return true;
            case (MotionEvent.ACTION_MOVE):
                // Log.d(DEBUG_TAG,"Action was MOVE");
                // return true;
            case (MotionEvent.ACTION_UP):
                // Log.d(DEBUG_TAG,"Action was UP");
                //return true;
            case (MotionEvent.ACTION_CANCEL):
                beepSound.stop();
                finish();
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                // Log.d(DEBUG_TAG,"Movement occurred outside bounds " +
                //          "of current screen element");
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

}