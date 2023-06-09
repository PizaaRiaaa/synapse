package com.example.synapse.screen.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.synapse.R;

public class ItemPillShapeAdapter extends BaseAdapter {

    Context context;
    String[] pillShapeNames;
    int[] pillShapes;

    public ItemPillShapeAdapter(Context context, int[] pillShapes, String[] pillShapeNames) {
        this.context = context;
        this.pillShapes = pillShapes;
        this.pillShapeNames = pillShapeNames;
    }

    @Override
    public int getCount() {
        return pillShapeNames.length;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner_pillshape, parent, false);
            ImageView imageView = convertView.findViewById(R.id.ivPillShape);
            TextView tv = convertView.findViewById(R.id.tvPillShapeHint);

            imageView.setImageResource(pillShapes[position]);
            tv.setText(pillShapeNames[position]);
        }

        return convertView;
    }
}
