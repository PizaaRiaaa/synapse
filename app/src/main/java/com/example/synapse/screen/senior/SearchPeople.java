package com.example.synapse.screen.senior;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synapse.R;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.example.synapse.screen.util.viewholder.SearchSeniorViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class SearchPeople extends AppCompatActivity {
    private DatabaseReference mUserRef;
    private FirebaseUser mUser;
    private RecyclerView recyclerView;
    private TextView tvSeniorResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_search_people);

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        tvSeniorResults = findViewById(R.id.tvSeniorResults);

        // set layout for recyclerview
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchPeople.this));

        // set bottomNavigationView to transparent
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        // count all carer users
        Query query =  mUserRef.orderByChild("userType").equalTo("Carer");
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int counter = (int) snapshot.getChildrenCount();

                // convert counter to string
                String carerCounter = String.valueOf(counter);

                // change substring color
                String str1 = " About " + carerCounter;
                String str2 = " carer";
                String str3 = " results";
                tvSeniorResults.setText(Html.fromHtml(str1 + "<font color=\"#049599\">" + str2 +  "</font> " + str3, Html.FROM_HTML_MODE_COMPACT));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

        // search senior user
        SearchView searchview = findViewById(R.id.search_field);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                // TODO BE ABLE TO SEARCH WITH IGNORECASE

                LoadUsers(newText);
                return false;
            }
        });

        // invoke to display all senior users
        LoadUsers("");
    }

    // display all senior users in recycle view
    private void LoadUsers(String s){

        // search users by fullName
        Query query = mUserRef.orderByChild("firstName").startAt(s).endAt(s+"\uf8ff");

        FirebaseRecyclerOptions<ReadWriteUserDetails> options = new FirebaseRecyclerOptions.Builder<ReadWriteUserDetails>().setQuery(query, ReadWriteUserDetails.class).build();

        FirebaseRecyclerAdapter<ReadWriteUserDetails, SearchSeniorViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteUserDetails, SearchSeniorViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SearchSeniorViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteUserDetails model) {

                // prevent current login user to display in recycler view
                if (!mUser.getUid().equals(getRef(position).getKey())) {

                    // hide senior users
                    if(model.getUserType().equals("Senior")){
                        holder.itemView.setVisibility(View.GONE);
                        holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    }

                    // display profile pic of every user
                    Log.i("success",model.getImageURL());
                    Picasso.get()
                            .load(model.getImageURL())
                            .fit()
                            .transform(new CropCircleTransformation())
                            .into(holder.profileImage);

                    // display details of user
                    holder.firstName.setText(model.getFirstName());
                    holder.userType.setText(model.getUserType());

                } else {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(SearchPeople.this, ViewPeople.class);
                    intent.putExtra("userKey", getRef(position).getKey());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public SearchSeniorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_send_request, parent, false);
                return new SearchSeniorViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}