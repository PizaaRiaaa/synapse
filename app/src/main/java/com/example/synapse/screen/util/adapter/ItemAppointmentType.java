package com.example.synapse.screen.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.synapse.R;

public class ItemAppointmentType extends BaseAdapter {
    Context context;
    String[] appointment_type;
    int[] appointment_type_ics;

    public ItemAppointmentType(Context context, String[] appointment_type, int[] appointment_type_ics){
        this.context = context;
        this.appointment_type = appointment_type;
        this.appointment_type_ics = appointment_type_ics;
    }

    @Override
    public int getCount() {
        return appointment_type.length;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner_appointment_type, parent, false);
            ImageView imageView = convertView.findViewById(R.id.ivAppointmentType);
            TextView tv = convertView.findViewById(R.id.tvAppointmentType);

            imageView.setImageResource(appointment_type_ics[position]);
            tv.setText(appointment_type[position]);
        }

        return convertView;
    }
}
