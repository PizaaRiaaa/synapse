package com.example.synapse.screen.util.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.synapse.R;
import com.google.android.material.button.MaterialButton;

public class SendRequestSeniorViewHolder extends RecyclerView.ViewHolder {

    public TextView fullName;
    public TextView barangay;
    public TextView age;
    public ImageView profileImage;
    public MaterialButton btnSendRequest;

    public SendRequestSeniorViewHolder(@NonNull View itemView) {
        super(itemView);

        fullName = itemView.findViewById(R.id.tvFullNameSendRequest);
        barangay = itemView.findViewById(R.id.tvBarangay);
        profileImage = itemView.findViewById(R.id.ivSendRequestProfileImage);
        btnSendRequest = itemView.findViewById(R.id.btnSendRequest);
        age = itemView.findViewById(R.id.tvAge);
    }
}
