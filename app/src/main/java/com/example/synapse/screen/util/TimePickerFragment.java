package com.example.synapse.screen.util;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.synapse.R;

import java.util.Calendar;
import java.util.Timer;

public class TimePickerFragment extends DialogFragment{
    TimePickerDialog.OnTimeSetListener onTimeSetListener;

    //Fragments need to empty constructor
    TimePickerFragment(){

    }

    public TimePickerFragment(TimePickerDialog.OnTimeSetListener onTimeSetListener){
        this.onTimeSetListener = onTimeSetListener;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialTimePicker,onTimeSetListener, hour, minute, false);
    }
}
