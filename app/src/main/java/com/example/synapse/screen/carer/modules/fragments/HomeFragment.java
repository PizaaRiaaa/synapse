package com.example.synapse.screen.carer.modules.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.synapse.R;
import com.example.synapse.screen.carer.SelectSenior;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.ReplaceFragment;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // global variables
    PromptMessage promptMessage = new PromptMessage();
    ReplaceFragment replaceFragment = new ReplaceFragment();
    private DatabaseReference referenceSenior, referenceCarer;
    private ImageView ivProfilePic, ivSeniorProfilePic;
    private TextView tvSeniorFullName;
    private TextView tvBarangay;
    private TextView tvSeniorAge;
    private TextView tvSeniorCity;
    private String imageURL;
    private FirebaseUser user;

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
     * @return A new instance of fragment CarerHomeFragment.
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
        View view = inflater.inflate(R.layout.fragment_carer_home, container, false);

        ShapeableImageView btnMedication, btnPhysicalActivity, btnAppointment, btnGames;
        ivProfilePic = view.findViewById(R.id.ivCarerProfilePic);
        ivSeniorProfilePic = view.findViewById(R.id.ivSeniorProfilePic);
        tvSeniorFullName = view.findViewById(R.id.tvSeniorFullName);
        tvSeniorCity = view.findViewById(R.id.tvSeniorCity);
        tvBarangay = view.findViewById(R.id.tvSeniorBarangay);
        tvSeniorAge = view.findViewById(R.id.tvSeniorAge);
        btnMedication = view.findViewById(R.id.btnMedication);
        btnPhysicalActivity = view.findViewById(R.id.btnPhysicalActivity);
        btnAppointment = view.findViewById(R.id.btnAppointment);
        btnGames = view.findViewById(R.id.btnGames);

        user = FirebaseAuth.getInstance().getCurrentUser();
        referenceCarer = FirebaseDatabase.getInstance().getReference("Users").child("Carers");
        referenceSenior = FirebaseDatabase.getInstance().getReference("Users").child("Seniors");
        String userID = user.getUid();

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        showUserProfile(userID);
        showSeniorProfile(getDefaults("seniorKey",getActivity()));

        btnMedication.setOnClickListener(v -> replaceFragment.replaceFragment(new MedicationFragment(), getActivity()));
        btnPhysicalActivity.setOnClickListener(v -> replaceFragment.replaceFragment(new PhysicalActivityFragment(), getActivity()));
        btnAppointment.setOnClickListener(v -> replaceFragment.replaceFragment(new AppointmentFragment(), getActivity()));
        btnGames.setOnClickListener(v -> replaceFragment.replaceFragment(new GamesFragment(), getActivity()));

        return view;
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    // display carer profile pic
    private void showUserProfile(String firebaseUser){
        referenceCarer.child(firebaseUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails userProfile = snapshot.getValue(ReadWriteUserDetails.class);
                if(userProfile != null){
                    Uri uri = user.getPhotoUrl();
                    Picasso.get()
                            .load(uri)
                            .transform(new CropCircleTransformation())
                            .into(ivProfilePic);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(getActivity());
            }
        });
    }

    // calculate senior's age
    int calculateAge(long date){
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if(today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)){
            age--;
        }
        return age;
    }

    // display assigned senior info
    void showSeniorProfile(String seniorKey){
            referenceSenior.child(seniorKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        ReadWriteUserDetails senior = snapshot.getValue(ReadWriteUserDetails.class);
                        String user_dob = senior.dob;
                        String fullName = senior.firstName + " " + senior.lastName;
                        String barangay = senior.address;
                        String city = senior.city;

                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat format = new SimpleDateFormat("MM dd yyyy", Locale.ENGLISH);

                        try {
                            cal.setTime(Objects.requireNonNull(format.parse(user_dob)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        tvSeniorFullName.setText(fullName);
                        tvBarangay.setText("Brgy." + barangay + ",");
                        tvSeniorCity.setText(city);
                        tvSeniorAge.setText(Integer.toString(calculateAge(cal.getTimeInMillis()))+ " yrs");

                        imageURL = Objects.requireNonNull(snapshot.child("imageURL").getValue()).toString();
                        Picasso.get()
                               .load(imageURL)
                               .fit()
                               .transform(new CropCircleTransformation())
                               .into(ivSeniorProfilePic);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    promptMessage.defaultErrorMessage(getActivity());
                }
            });
       }
}