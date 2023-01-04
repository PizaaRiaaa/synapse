package com.example.osca_admin.util.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.osca_admin.R;

import org.w3c.dom.Text;

public class CarerViewHolder extends RecyclerView.ViewHolder {

    public TextView id;
    public TextView fullName;
    public TextView email;
    public TextView address;
    public ImageView ivPicture;

    public CarerViewHolder(@NonNull View itemView) {
        super(itemView);

        id = itemView.findViewById(R.id.tvCarerID);
        fullName = itemView.findViewById(R.id.tvCarerFullName);
        email = itemView.findViewById(R.id.tvCarerEmail);
        address = itemView.findViewById(R.id.tvCarerAddress);
        ivPicture = itemView.findViewById(R.id.ivCarerPic);

    }
}
