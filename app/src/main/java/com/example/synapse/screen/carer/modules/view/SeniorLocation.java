package com.example.synapse.screen.carer.modules.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class SeniorLocation extends AppCompatActivity{

    DatabaseReference referenceSenior, referenceSeniorID;
    SupportMapFragment supportMapFragment;
    PromptMessage promptMessage = new PromptMessage();
    TextView tvSeniorLocation;
    ImageView seniorImage, ibBack;
    String seniorFullName, imageURL;
    Double latitude, longtitude;
    String addresss, city, country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_location);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        tvSeniorLocation = findViewById(R.id.tvSeniorLocation);
        MaterialCardView locationDetails = findViewById(R.id.bottomLocation);
        MaterialCardView getLocation = findViewById(R.id.btnGetLocation);
        seniorImage = findViewById(R.id.ivSeniorImage);
        ibBack = findViewById(R.id.ibBack);

        referenceSeniorID = FirebaseDatabase.getInstance()
                .getReference("Users").
                child("Seniors")
                .child(getDefaults("seniorKey", this));
        referenceSenior = FirebaseDatabase.getInstance()
                .getReference("SeniorLocation")
                .child(getDefaults("seniorKey", this));

        locationDetails.setOnClickListener(v -> displayLocationDetails());
        getLocation.setOnClickListener(v -> getLatitudeLongtitude());
        ibBack.setOnClickListener(v -> finish());

        showSeniorDetails();
        getLatitudeLongtitude();

    }


    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    private void getLatitudeLongtitude(){
        referenceSenior.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        latitude = snapshot.child("Latitude").getValue(Double.class);
                        longtitude = snapshot.child("Longtitude").getValue(Double.class);
                        //sync map
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                LatLng latLng = new LatLng(latitude, longtitude);

                                MarkerOptions options = new MarkerOptions().position(latLng)
                                        .title("Senior is here");
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                googleMap.addMarker(options);
                                Geocoder geocoder = new Geocoder(SeniorLocation.this, Locale.getDefault());
                                List<Address> current_address = null;
                                try {
                                    current_address = geocoder.getFromLocation(latitude, longtitude, 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                city = current_address.get(0).getLocality();
                                country = current_address.get(0).getCountryName();
                                addresss = current_address.get(0).getAddressLine(0);
                                tvSeniorLocation.setText(seniorFullName + " is near " + current_address.get(0).getAddressLine(0));

                            }
                        });
                    }

                }else{
                    promptMessage.defaultErrorMessage(SeniorLocation.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(SeniorLocation.this);
            }
        });
    }

    private void displayLocationDetails() {
        new AlertDialog.Builder(this)
                .setTitle(seniorFullName + " current location")
                .setMessage("Lattitude: " + latitude + "\n" +
                        "Longtitude: " + longtitude + "\n" +
                        "Address: " + addresss + "\n" +
                        "Country: " + country + "\n" +
                        "City: " + city + "\n")
                .setPositiveButton("Close",(dialogInterface, i) -> dialogInterface.cancel())
                .setCancelable(false)
                .show();
    }

    void showSeniorDetails(){
        referenceSeniorID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ReadWriteUserDetails senior = snapshot.getValue(ReadWriteUserDetails.class);
                    seniorFullName = senior.firstName + " " + senior.lastName;

                    imageURL = Objects.requireNonNull(snapshot.child("imageURL").getValue()).toString();
                    Picasso.get()
                            .load(imageURL)
                            .transform(new CropCircleTransformation())
                            .into(seniorImage);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(SeniorLocation.this);
            }
        });
    }

}