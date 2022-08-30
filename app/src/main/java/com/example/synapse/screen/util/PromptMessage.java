package com.example.synapse.screen.util;

import android.app.Activity;
import com.example.synapse.R;
import org.aviran.cookiebar2.CookieBar;

public class PromptMessage {

    public void displayMessage(String title, String message, int color,Activity context){
        CookieBar.build(context)
                .setTitle(title)
                .setMessage(message)
                .setBackgroundColor(color)
                .setCookiePosition(CookieBar.TOP)
                .setDuration(5000)
                .show();
    }

    public void defaultErrorMessage(Activity context){
        CookieBar.build(context)
                .setTitle("Error")
                .setMessage("Something went wrong. Please try again")
                .setBackgroundColor(R.color.dark_green)
                .setCookiePosition(CookieBar.TOP)
                .setDuration(5000)
                .show();
    }

}
