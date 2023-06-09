package com.example.synapse.screen.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.synapse.R;
import org.aviran.cookiebar2.CookieBar;

public class PromptMessage {

    // display custom message
    public void displayMessage(String title, String message, int color,Activity context){
        CookieBar.build(context)
                .setTitle(title)
                .setMessage(message)
                .setBackgroundColor(color)
                .setCookiePosition(CookieBar.TOP)
                .setDuration(5000)
                .show();
    }

    // display default error message
    public void defaultErrorMessage(Activity context){
        CookieBar.build(context)
                .setTitle("Error")
                .setMessage("Something went wrong. Please try again")
                .setBackgroundColor(R.color.dark_green)
                .setCookiePosition(CookieBar.TOP)
                .setDuration(5000)
                .show();
    }

    // display default error message for non activity class
    public void defaultErrorMessageContext(Context context) {
        Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
    }
}
