package com.example.synapse.screen.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.synapse.R;

public class ItemPhysicalActivityAdapter extends BaseAdapter {
    Context context;
    String[] physical_activity_name;
    int[] physical_activity_ics;

    public ItemPhysicalActivityAdapter(Context context, String[] physical_activity_name, int[] physical_activity_ics){
        this.context = context;
        this.physical_activity_name = physical_activity_name;
        this.physical_activity_ics = physical_activity_ics;
    }

    @Override
    public int getCount() {
        return physical_activity_name.length;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner_physical_activity, parent, false);
            ImageView imageView = convertView.findViewById(R.id.ivPhysicalActivity);
            TextView tv = convertView.findViewById(R.id.tvPhysicalActivityName);

            imageView.setImageResource(physical_activity_ics[position]);
            tv.setText(physical_activity_name[position]);
        }

        return convertView;
    }
}
