package com.example.synapse.screen.util.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synapse.R;

public class SearchSeniorViewHolder extends RecyclerView.ViewHolder {

    public TextView firstName;
    public TextView userType;
    public ImageView profileImage;


    public SearchSeniorViewHolder(@NonNull View itemView) {
        super(itemView);

        firstName = itemView.findViewById(R.id.tvSearch_FullName);
        profileImage = itemView.findViewById(R.id.ivSearchProfileImage);
        userType = itemView.findViewById(R.id.tvSearchUserType);
    }
}
