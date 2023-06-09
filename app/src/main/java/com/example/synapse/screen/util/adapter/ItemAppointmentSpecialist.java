package com.example.synapse.screen.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.synapse.R;

public class ItemAppointmentSpecialist extends BaseAdapter {
    Context context;
    String[] appointment_specialist;
    int[] appointment_specialist_ics;

    public ItemAppointmentSpecialist(Context context, String[] appointment_specialist, int[] appointment_specialist_ics){
        this.context = context;
        this.appointment_specialist = appointment_specialist;
        this.appointment_specialist_ics = appointment_specialist_ics;
    }

    @Override
    public int getCount() {
        return appointment_specialist.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner_appointment_specialist, parent, false);
            ImageView imageView = convertView.findViewById(R.id.ivAppointmentSpecialist);
            TextView tv = convertView.findViewById(R.id.tvAppointmentSpecialist);

            imageView.setImageResource(appointment_specialist_ics[position]);
            tv.setText(appointment_specialist[position]);
        }

        return convertView;
    }
}
