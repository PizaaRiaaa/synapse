package com.example.osca_admin.util.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.osca_admin.R;

public class AuditTrailViewHolder extends RecyclerView.ViewHolder {

    public TextView UserID;
    public TextView ActionMade;
    public TextView By;
    public TextView Details;
    public TextView DateTime;

    public AuditTrailViewHolder(@NonNull View itemView) {
        super(itemView);

        UserID = itemView.findViewById(R.id.tvUserIDAudit);
        ActionMade = itemView.findViewById(R.id.tvActionMadeAudit);
        By = itemView.findViewById(R.id.tvFullNameAudit);
        Details = itemView.findViewById(R.id.tvDetailsAudit);
        DateTime = itemView.findViewById(R.id.tvDateTimeAudit);

    }
}
