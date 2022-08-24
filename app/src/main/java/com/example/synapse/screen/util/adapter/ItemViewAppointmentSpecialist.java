package com.example.synapse.screen.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.synapse.R;

public class ItemViewAppointmentSpecialist extends BaseAdapter {
    Context context;
    String[] appointment_specialist;

    public ItemViewAppointmentSpecialist(Context context, String[] appointment_specialist){
        this.context = context;
        this.appointment_specialist = appointment_specialist;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner_view_appointment_specialist, parent, false);
            TextView tv = convertView.findViewById(R.id.tvAppointmentSpecialist);

            tv.setText(appointment_specialist[position]);
        }

        return convertView;
    }
}
