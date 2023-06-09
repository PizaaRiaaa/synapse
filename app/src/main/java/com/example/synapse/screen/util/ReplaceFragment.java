package com.example.synapse.screen.util;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.synapse.R;

// replace fragment
public class ReplaceFragment {
    public void replaceFragment(Fragment fragment, Activity activity){
        FragmentManager fragmentManager = ((FragmentActivity)activity).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
