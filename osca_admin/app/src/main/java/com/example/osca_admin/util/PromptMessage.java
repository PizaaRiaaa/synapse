package com.example.osca_admin.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.osca_admin.R;

import org.aviran.cookiebar2.CookieBar;

public class PromptMessage {
    // display custom message
    public void displayMessage(String title, String message, int color, Activity context){
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
                .setCookiePosition(CookieBar.TOP)
                .setDuration(5000)
                .show();
    }

    // display default error message for non activity class
    public void defaultErrorMessageContext(Context context) {
        Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
    }
}
