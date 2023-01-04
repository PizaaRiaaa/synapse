package com.example.osca_admin.util.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.osca_admin.R;

public class BarangayViewHolder extends RecyclerView.ViewHolder {

    public TextView barangayName;
    public TextView editBarangayName;
    public ImageView editButton;
    public ImageView rightButton;

    public BarangayViewHolder(@NonNull View itemView) {
        super(itemView);
        barangayName = itemView.findViewById(R.id.tvBarangayName);
        rightButton = itemView.findViewById(R.id.ivArrow);
        editBarangayName = itemView.findViewById(R.id.etEditBarangay);
        editButton = itemView.findViewById(R.id.ivEdit);
    }
}
