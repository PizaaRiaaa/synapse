package com.example.synapse.screen.util.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synapse.R;

public class AppointmentViewHolder extends RecyclerView.ViewHolder {

    public TextView dateAndTime;
    public ImageView ivSpecialist;
    public TextView doctorName;
    public TextView doctorSpecialist;

    public AppointmentViewHolder(@NonNull View itemView) {
        super(itemView);

        dateAndTime = itemView.findViewById(R.id.tvDateAndTime);
        ivSpecialist = itemView.findViewById(R.id.ivSpecialist);
        doctorName = itemView.findViewById(R.id.tvDoctorName);
        doctorSpecialist = itemView.findViewById(R.id.tvSpecialist);
    }
}
