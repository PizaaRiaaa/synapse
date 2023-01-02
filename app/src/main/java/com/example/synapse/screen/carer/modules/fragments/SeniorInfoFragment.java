package com.example.synapse.screen.carer.modules.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.synapse.R;
import com.example.synapse.screen.util.readwrite.ReadWriteUserSenior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SeniorInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SeniorInfoFragment extends Fragment {

    DatabaseReference referenceSenior;

    String imageURL;
    ImageView ivSeniorProfilePic;

    TextView etFullName;
    TextView etDOB;
    TextView etBarangay;
    TextView etEmail;
    TextView etMobileNo;
    TextView etGender;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SeniorInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SeniorInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SeniorInfoFragment newInstance(String param1, String param2) {
        SeniorInfoFragment fragment = new SeniorInfoFragment();
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

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    void displaySeniorInfo(){
        referenceSenior.child(getDefaults("seniorKey",getActivity())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   ReadWriteUserSenior senior = snapshot.getValue(ReadWriteUserSenior.class);
                   etFullName.setText("Name: " + senior.getFirstName() + " " + senior.getMiddle() + ". " + senior.getLastName());
                   etGender.setText("Gender: " + senior.getGender());
                   etDOB.setText("Date of Birth: " + senior.getDob());
                   etBarangay.setText("Barangay: " + senior.getAddress());
                   etEmail.setText("Email: " + senior.getEmail());
                   etMobileNo.setText("Mobile no. : " + senior.getMobileNumber());

                   imageURL = Objects.requireNonNull(snapshot.child("imageURL").getValue()).toString();
                   Picasso.get()
                           .load(imageURL)
                           .transform(new CropCircleTransformation())
                           .into(ivSeniorProfilePic);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_senior_info, container, false);

        ivSeniorProfilePic = view.findViewById(R.id.ivSeniorProfilePic);
        etFullName = view.findViewById(R.id.tvSeniorName);
        etDOB = view.findViewById(R.id.tvSeniorDOB);
        etBarangay = view.findViewById(R.id.tvSeniorBarangay);
        etEmail = view.findViewById(R.id.tvSeniorEmail);
        etMobileNo = view.findViewById(R.id.tvSeniorMobile);
        etGender = view.findViewById(R.id.tvSeniorGender);

        referenceSenior = FirebaseDatabase.getInstance().getReference("Users").child("Seniors");

        displaySeniorInfo();


        return view;
    }
}