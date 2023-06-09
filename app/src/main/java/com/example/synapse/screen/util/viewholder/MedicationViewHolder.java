package com.example.synapse.screen.util.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.synapse.R;

import org.w3c.dom.Text;

import java.util.Calendar;

public class MedicationViewHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public TextView dose;
    public TextView tvIsTaken;
    public String time_of_medication;
    public TextView time;
    public TextView inTake;
    public ImageView pill_shape;
    public ImageView taken;
    public LinearLayout medicineBackground;

    public MedicationViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.tvNameOfPill);
        dose = itemView.findViewById(R.id.tvDoseOfPill);
        time = itemView.findViewById(R.id.tvTimeOfMedicine);
        pill_shape = itemView.findViewById(R.id.ivShapeOfPill);
        medicineBackground = itemView.findViewById(R.id.medicineBackground);
        tvIsTaken = itemView.findViewById(R.id.tvIsTaken);
        taken = itemView.findViewById(R.id.ivIsTaken);
        inTake = itemView.findViewById(R.id.tvInTake);
    }
}
