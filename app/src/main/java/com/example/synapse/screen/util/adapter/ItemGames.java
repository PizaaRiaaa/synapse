package com.example.synapse.screen.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.synapse.R;

public class ItemGames extends BaseAdapter {
    Context context;
    String[] games;
    int[] games_ics;

    public ItemGames(Context context, String[] games, int[] games_ics){
        this.context = context;
        this.games = games;
        this.games_ics = games_ics;
    }

    @Override
    public int getCount() {
        return games.length;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner_games, parent, false);
            ImageView imageView = convertView.findViewById(R.id.ivGames);
            TextView tv = convertView.findViewById(R.id.tvGames);

            imageView.setImageResource(games_ics[position]);
            tv.setText(games[position]);
        }

        return convertView;
    }
}
