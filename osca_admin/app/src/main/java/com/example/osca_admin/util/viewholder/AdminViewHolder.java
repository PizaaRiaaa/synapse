package com.example.osca_admin.util.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.osca_admin.R;

public class AdminViewHolder extends RecyclerView.ViewHolder {

    public TextView id;
    public TextView fullName;
    public TextView email;
    public TextView username;
    public ImageView ivPicture;

    public AdminViewHolder(@NonNull View itemView) {
        super(itemView);

        id = itemView.findViewById(R.id.tvID);
        username = itemView.findViewById(R.id.tvAdminUsername);
        fullName = itemView.findViewById(R.id.tvFullName);
        email = itemView.findViewById(R.id.tvAdminEmail);
        ivPicture = itemView.findViewById(R.id.ivAdminPic);

    }
}
