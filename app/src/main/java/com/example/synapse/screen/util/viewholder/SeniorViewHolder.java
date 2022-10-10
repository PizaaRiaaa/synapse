package com.example.synapse.screen.util.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.synapse.R;

public class SeniorViewHolder extends RecyclerView.ViewHolder {

    public TextView fullName;
    public TextView barangay;
    public TextView city;
    public TextView dob;
    public ImageView image;

    public SeniorViewHolder(@NonNull View itemView) {
        super(itemView);

        fullName = itemView.findViewById(R.id.tvSeniorFullName);
        barangay = itemView.findViewById(R.id.tvSeniorBarangay);
        city = itemView.findViewById(R.id.tvSeniorCity);
        dob = itemView.findViewById(R.id.tvSeniorAge);
        image = itemView.findViewById(R.id.ivSearchProfileImage);
    }
}
