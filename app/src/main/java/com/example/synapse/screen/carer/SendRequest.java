package com.example.synapse.screen.carer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.synapse.R;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.example.synapse.screen.util.viewholder.SendRequestSeniorViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class SendRequest extends AppCompatActivity {

    private DatabaseReference mUserRef;
    private DatabaseReference requestRef;
    private FirebaseUser mUser;
    private RecyclerView recyclerView;
    public MaterialButton btnSendRequest, btnOk;
    private String currentState = "nothing_happen";
    private String seniorUserID;
    Dialog dialog;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);

        // show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // transparent status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.grey4));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.grey4));

        // extracting user reference from database "Registered Users" and "Relquest" nodes
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btnSendRequest = findViewById(R.id.btnSendRequest);

        dialog = new Dialog(SendRequest.this);
        dialog.setContentView(R.layout.custom_dialog_box_send_request);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background1));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation1;

        btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(view -> startActivity(new Intent(SendRequest.this, MainActivity.class)));

        // set layout for recyclerview
        recyclerView = findViewById(R.id.recyclerview_sendRequest);
        recyclerView.setLayoutManager(new LinearLayoutManager(SendRequest.this));

        // search senior users
        SearchView searchview = findViewById(R.id.search_field_send_request);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // prevent display recyclerview if search view is empty
                if(newText.equals("") ){
                    recyclerView.setVisibility(View.GONE);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    LoadUsers(newText);
                }
                return false;
            }
        });

    }

    // calculate user's age
    public int calculateAge(long date){
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if(today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)){
            age--;
        }
        return age;
    }

    // display all senior users in recycle view
    private void LoadUsers(String s){
        // search users by fullName
        Query query = mUserRef.orderByChild("fullName").startAt(s).endAt(s+"\uf8ff");

        FirebaseRecyclerOptions<ReadWriteUserDetails> options = new FirebaseRecyclerOptions.Builder<ReadWriteUserDetails>().setQuery(query, ReadWriteUserDetails.class).build();

        FirebaseRecyclerAdapter<ReadWriteUserDetails, SendRequestSeniorViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteUserDetails, SendRequestSeniorViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull SendRequestSeniorViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteUserDetails model) {
                // prevent current login user display in recycler view
                if (!mUser.getUid().equals(getRef(position).getKey())) {

                    // hide carer users
                    if(model.getUserType().equals("Carer")){
                        holder.itemView.setVisibility(View.GONE);
                        holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    }

                    // display profile pic of every senior users
                    Log.i("success",model.getImageURL());
                    Picasso.get()
                            .load(model.getImageURL())
                            .fit()
                            .centerCrop()
                            .transform(new CropCircleTransformation())
                            .into(holder.profileImage);

                    // get user's age from date of birth
                    String user_dob = model.getDOB();
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("MM dd yyyy", Locale.ENGLISH);
                    try {
                        cal.setTime(Objects.requireNonNull(format.parse(user_dob)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // display details of every senior users
                    holder.fullName.setText(model.getFullName());
                    holder.barangay.setText("Brgy." + model.getAddress() + ",");
                    holder.age.setText(Integer.toString(calculateAge(cal.getTimeInMillis())) + " yrs");

                    // send request
                    holder.btnSendRequest.setOnClickListener(v -> {
                           seniorUserID = getRef(position).getKey();
                           PerformAction(seniorUserID);
                           dialog.show();
                     });
                } else {
                    // hide recyclerview
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }

            @NonNull
            @Override
            public SendRequestSeniorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_send_request_to_senior, parent, false);
                return new SendRequestSeniorViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    // perform send request action
    private void PerformAction(String userID){
        if(currentState.equals("nothing_happen")){
            HashMap hashMap = new HashMap();
            hashMap.put("status","pending");
            requestRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SendRequest.this, "You have send request", Toast.LENGTH_SHORT).show();
                        currentState = "I_sent_pending";
                    }else{
                        Toast.makeText(SendRequest.this, "" + Objects.requireNonNull(task.getException()), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}