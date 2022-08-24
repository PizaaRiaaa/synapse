package com.example.synapse.screen.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.synapse.R;

public class ItemPillColorAdapter extends BaseAdapter {
    Context context;
    String[] pillColorsName;
    int[] pillShapeColors;

    public ItemPillColorAdapter(Context context, String[] pillColors, int[] pillShapeColors) {
        this.context = context;
        this.pillColorsName = pillColors;
        this.pillShapeColors = pillShapeColors;
    }

    @Override
    public int getCount() {
        return pillColorsName.length;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner_pillcolor, parent, false);
            ImageView imageView = convertView.findViewById(R.id.ivPillColor);
            TextView tv = convertView.findViewById(R.id.tvPillColorHint);

            imageView.setImageResource(pillShapeColors[position]);
            tv.setText(pillColorsName[position]);
        }

        return convertView;
    }
}
