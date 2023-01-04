package com.example.osca_admin.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osca_admin.Login;
import com.example.osca_admin.R;
import com.example.osca_admin.util.AuditTrail;
import com.example.osca_admin.util.PromptMessage;
import com.example.osca_admin.util.ReplaceFragment;
import com.example.osca_admin.util.readwrite.ReadWriteAdmin;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // Global variables
    ReplaceFragment replaceFragment = new ReplaceFragment();
    PromptMessage promptMessage = new PromptMessage();
    AuditTrail auditTrail = new AuditTrail();

    MaterialCardView btnLogout;
    ImageView ivPicture;
    String firstName;
    String lastName;

    ReadWriteAdmin admin;
    DatabaseReference referenceAdmin;
    TextView tvUsername;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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

    public void logoutUser(){

        auditTrail.auditTrail("Logout Account ",
                firstName + " " + lastName,
                "Admin", "Admin", referenceAdmin,
                getDefaults("userKey", getActivity()));

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.remove("userKey");
        editor.commit();


        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(
               Intent.FLAG_ACTIVITY_CLEAR_TOP |
               Intent.FLAG_ACTIVITY_CLEAR_TASK |
               Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().onBackPressed();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    void displayUsernameAndPic(){
        DatabaseReference referenceAdmin = FirebaseDatabase.getInstance().getReference("Users").child("Admins");
        referenceAdmin.child(getDefaults("userKey", getActivity())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    admin = snapshot.getValue(ReadWriteAdmin.class);
                    String username = admin.getUserName();
                    firstName = admin.getFirstName();
                    lastName = admin.getLastName();
                    tvUsername.setText(username);

                    String image = admin.getImageURL();

                    Picasso.get()
                            .load(admin.getImageURL())
                            .transform(new CropCircleTransformation())
                            .into(ivPicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(getActivity());

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // force app to light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        referenceAdmin = FirebaseDatabase.getInstance().getReference("Users").child("Admins");

        ImageButton ibBack = view.findViewById(R.id.ibBack);
        ImageView ivEditProfile = view.findViewById(R.id.btnEditProfile);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnLogout = view.findViewById(R.id.btnLogout);
        ivPicture = view.findViewById(R.id.ivAdminPicture);

        btnLogout.setOnClickListener(v -> logoutUser());
        ibBack.setOnClickListener(v -> replaceFragment.replaceFragment(new HomeFragment(), getActivity()));
        ivEditProfile.setOnClickListener(v -> replaceFragment.replaceFragment(new EditProfileFragment(), getActivity()));

        displayUsernameAndPic();

        return view;
    }
}