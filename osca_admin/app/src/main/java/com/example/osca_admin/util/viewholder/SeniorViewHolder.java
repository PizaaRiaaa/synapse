package com.example.osca_admin.util.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.osca_admin.R;

public class SeniorViewHolder extends RecyclerView.ViewHolder {

    public TextView id;
    public TextView fullName;
    public TextView email;
    public TextView address;
    public ImageView ivPicture;


    public SeniorViewHolder(@NonNull View itemView) {
        super(itemView);

        id = itemView.findViewById(R.id.tvSeniorID);
        fullName = itemView.findViewById(R.id.tvSeniorFullName);
        email = itemView.findViewById(R.id.tvSeniorEmail);
        address = itemView.findViewById(R.id.tvSeniorAddress);
        ivPicture = itemView.findViewById(R.id.ivSeniorPic);

    }
}
