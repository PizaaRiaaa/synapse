package com.example.synapse.screen.carer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ViewPeople extends AppCompatActivity {

    DatabaseReference mUserRef, requestRef, assignedCompanionRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ImageButton ibBack;
    String imageURL,fullName;
    ImageView ivProfilePic;
    TextView tvFullName;
    Button btnRequest, btnDecline;
    String currentState = "nothing_happen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_view_people);

        // bring back user to SearchSenior screen
        ibBack = findViewById(R.id.ibBack);
        ibBack.setOnClickListener(view -> startActivity(new Intent(ViewPeople.this, SearchPeople.class)));

        // get the clicked user's id
        final String userID = getIntent().getStringExtra( "userKey");
        Toast.makeText(this,"" + userID, Toast.LENGTH_SHORT).show();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Registered Users").child(userID);
        requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        assignedCompanionRef = FirebaseDatabase.getInstance().getReference().child("Companion");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        ivProfilePic = findViewById(R.id.ivViewProfilePic);
        tvFullName = findViewById(R.id.tvViewFullName);

        btnRequest = findViewById(R.id.btnSendRequest);
        btnDecline = findViewById(R.id.btnDeclineRequest);

        // send request to senior
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformAction(userID);
            }
        });

        // invoke to display user info
        LoadUser();

        checkUserExistence(userID);
    }

    // prevent display request button if it is already tap
    private void checkUserExistence(String userID){
        assignedCompanionRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    currentState = "Companion";
                    btnRequest.setText("Send SMS");
                    btnDecline.setText("Decline");
                    btnDecline.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        assignedCompanionRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    currentState = "Companion";
                    btnRequest.setText("Send SMS");
                    btnDecline.setText("Remove Care Recipient");
                    btnDecline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                     if(snapshot.child("status").getValue().toString().equals("pending")){
                         currentState = "I_sent_pending";
                         btnRequest.setText("Cancel carer request");
                         btnDecline.setVisibility(View.GONE);

                     }
                    if(snapshot.child("status").getValue().toString().equals("Decline")){
                        currentState = "I_sent_decline";
                        btnRequest.setText("Cancel carer request");
                        btnDecline.setVisibility(View.GONE);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("status").getValue().toString().equals("pending")){
                        currentState = "he_sent_pending";
                        btnRequest.setText("Accept request");
                        btnDecline.setText("Decline request");
                        btnDecline.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(currentState.equals("nothing_happen")){
            currentState = "nothing_happen";
            btnRequest.setText("Send carer request");
            btnDecline.setVisibility(View.GONE);
        }
    }

    // display user info
    private void LoadUser(){
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    // extracting userType reference from the db for "Registered Users"
                   String userType = snapshot.child("userType").getValue().toString();

                   if(userType.equals("Carer")){
                      btnDecline.setVisibility(View.GONE);
                      btnRequest.setVisibility(View.GONE);
                   }

                    imageURL = Objects.requireNonNull(snapshot.child("imageURL").getValue()).toString();
                    fullName = Objects.requireNonNull(snapshot.child("fullName").getValue()).toString();

                    Picasso.get()
                           .load(imageURL)
                           .fit()
                           .transform(new CropCircleTransformation())
                           .into(ivProfilePic);

                    tvFullName.setText(fullName);

                }else{
                    Toast.makeText(ViewPeople.this, "Data not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ViewPeople.this, "" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();

             }
        });
    }

    // send request
    private void PerformAction(String userID){
        if(currentState.equals("nothing_happen")){
            HashMap hashMap = new HashMap();
            hashMap.put("status","pending");
            requestRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ViewPeople.this, "You have send carer request", Toast.LENGTH_SHORT).show();
                        btnDecline.setVisibility(View.GONE);
                        currentState = "I_sent_pending";
                        btnRequest.setText("Cancel request");
                    }else{
                        Toast.makeText(ViewPeople.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(currentState.equals("I_sent_pending") || currentState.equals("I_sent_decline")){
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ViewPeople.this, "You have cancelled request", Toast.LENGTH_SHORT).show();
                        currentState = "nothing_happen";
                        btnRequest.setText("Send carer request");
                        btnDecline.setVisibility(View.GONE);
                    }else{
                        Toast.makeText(ViewPeople.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(currentState.equals("he_sent_pending")){
             requestRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                     if(task.isSuccessful()){
                         HashMap hashMap = new HashMap();
                         hashMap.put("status","companion");
                         hashMap.put("fullName",fullName);
                         hashMap.put("imageURL",imageURL);

                         assignedCompanionRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                             @Override
                             public void onComplete(@NonNull Task task) {
                                 if(task.isSuccessful()){
                                     assignedCompanionRef.child(userID).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                         @Override
                                         public void onComplete(@NonNull Task task) {
                                            Toast.makeText(ViewPeople.this, "You added your senior companion", Toast.LENGTH_SHORT).show();
                                            currentState = "Companion";
                                            btnRequest.setText("Send SMS");
                                            btnDecline.setText("remove care recipient");
                                            btnDecline.setVisibility(View.VISIBLE);
                                         }
                                     });
                                 }

                             }
                         });
                     }
                 }
             });
        }
       // if(currentState.equals("Companion")){
       //     //
       // }
    }
}