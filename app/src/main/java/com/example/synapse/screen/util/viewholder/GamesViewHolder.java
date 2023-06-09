package com.example.synapse.screen.util.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synapse.R;

public class GamesViewHolder extends RecyclerView.ViewHolder {

    public TextView gameName;
    public TextView gameAlarm;
    public ImageView ivGame;

    public GamesViewHolder(@NonNull View itemView) {
        super(itemView);

        gameName = itemView.findViewById(R.id.tvGameName);
        gameAlarm = itemView.findViewById(R.id.tvGameAlarm);
        ivGame = itemView.findViewById(R.id.ivGames);
    }
}
