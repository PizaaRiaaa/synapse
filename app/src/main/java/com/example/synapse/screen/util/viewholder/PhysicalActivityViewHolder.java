package com.example.synapse.screen.util.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.synapse.R;

public class PhysicalActivityViewHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public TextView duration;
    public ImageView ic_activity;

    public PhysicalActivityViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.tvNameOfPhysicalActivity);
        duration = itemView.findViewById(R.id.tvDuration);
        ic_activity = itemView.findViewById(R.id.ivPhysicalActivity);

    }
}
