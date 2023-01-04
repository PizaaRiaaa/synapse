package com.example.osca_admin.util;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.osca_admin.util.readwrite.ReadWriteAdmin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class AuditTrail {

    DatabaseReference referenceAuditTrail = FirebaseDatabase.getInstance().getReference("Audit Trail").child("logs");
    public void auditTrail(String action, String newInfo, String type, String madeBy,
                    DatabaseReference db_profile, String adminID){

        db_profile.child(adminID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ReadWriteAdmin carer = snapshot.getValue(ReadWriteAdmin.class);
                    String name = carer.getFirstName() + " " + carer.getLastName() + " (" + madeBy + ")";
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("UserID", adminID);
                    hashMap.put("Name", name);
                    hashMap.put("ActionMade", action);
                    hashMap.put("Info", newInfo);
                    hashMap.put("ActionMadeBy", madeBy);
                    hashMap.put("Type", type);
                    hashMap.put("Timestamp", ServerValue.TIMESTAMP);
                    referenceAuditTrail.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("Audit","Successfully added the audit");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
