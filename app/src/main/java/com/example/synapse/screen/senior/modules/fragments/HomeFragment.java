package com.example.synapse.screen.senior.modules.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.synapse.R;
import com.example.synapse.screen.Login;
import com.example.synapse.screen.senior.SearchPeople;
import com.example.synapse.screen.util.ReplaceFragment;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // Global variables
     static final String TAG = "";
     DatabaseReference referenceSenior;
     FirebaseUser mUser;
     String token;
     TextView tvSeniorName;
     AppCompatImageView ivProfilePic;
     ReplaceFragment replaceFragment = new ReplaceFragment();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_senior_home, container, false);

        FirebaseMessaging.getInstance().subscribeToTopic("hello");

        TextClock currentTime = view.findViewById(R.id.tcTime);
        MaterialCardView btnMedication = view.findViewById(R.id.btnMedication);
        MaterialCardView btnGames = view.findViewById(R.id.btnGames);
        MaterialCardView btnSearchPeople = view.findViewById(R.id.btnSearchPeople);
        MaterialCardView btnLogout = view.findViewById(R.id.btnLogout);
        ivProfilePic = view.findViewById(R.id.ivSeniorProfilePic);
        tvSeniorName = view.findViewById(R.id.tvSeniorFullName);

        referenceSenior = FirebaseDatabase.getInstance().getReference("Users").child("Seniors");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = mUser.getUid();

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnMedication.setOnClickListener(v -> replaceFragment.replaceFragment(new MedicationFragment(), getActivity()));
        btnGames.setOnClickListener(v -> replaceFragment.replaceFragment(new GamesFragment(),getActivity()));
        btnSearchPeople.setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchPeople.class)));
        currentTime.setFormat12Hour("hh:mm a");

        showUserProfile(userID);

        // logout user
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth user = FirebaseAuth.getInstance();
            user.signOut();
            startActivity(new Intent(getActivity(), Login.class));
            getActivity().getFragmentManager().popBackStack();
        });

        return view;
    }

    // retrieve  and generate token everytime user access the app
    @Override
    public void onStart() {
        super.onStart();
        HashMap hashMap = new HashMap();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        token = task.getResult();
                        hashMap.put("token", token);
                        referenceSenior.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                            }
                        });
                        String msg = token;
                        Log.d("Token:", msg);
                    }
                });
    }



    // retrieve senior's profile picture
    public void showUserProfile(String firebaseUser){
        referenceSenior.child(firebaseUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ReadWriteUserDetails userProfile = snapshot.getValue(ReadWriteUserDetails.class);
                    if(userProfile != null){
                        String fullname = userProfile.firstName + " " + userProfile.lastName;
                        tvSeniorName.setText(fullname);

                        // display carer profile pic
                        Uri uri = mUser.getPhotoUrl();
                        Picasso.get()
                                .load(uri)
                                .transform(new CropCircleTransformation())
                                .into(ivProfilePic);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}