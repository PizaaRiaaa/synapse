package com.example.synapse.screen.senior;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentContainerView;

import com.example.synapse.R;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.notifications.FcmNotificationsSender;
import com.example.synapse.screen.util.notifications.FirebaseMessagingService;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.example.synapse.screen.util.readwrite.ReadWriteUserSenior;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

public class MyLocation extends AppCompatActivity{

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    TextView tvCurrentLocation;
    DatabaseReference referenceAssignedCarer, referenceCarer;
    String latitude, longtitude, addresss, city, country;
    PromptMessage promptMessage = new PromptMessage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        referenceAssignedCarer = FirebaseDatabase.getInstance().getReference("AssignedSeniors");
        referenceCarer = FirebaseDatabase.getInstance().getReference("Users").child("Carers");

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        MaterialCardView btnGetLocation = findViewById(R.id.btnGetLocation);
        MaterialCardView btnLocationDetails = findViewById(R.id.bottomLocation);
        MaterialCardView btnScreenshot = findViewById(R.id.screenShot);
        ImageView btnSendMyLocation = findViewById(R.id.sendMyLocation);
        tvCurrentLocation = findViewById(R.id.tvCurrentLocation);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        // initialized fused location
        client = LocationServices.getFusedLocationProviderClient(this);

        // check permission
        if (ActivityCompat.checkSelfPermission(MyLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // when permission granted
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(MyLocation.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        btnGetLocation.setOnClickListener(v -> getCurrentLocation());
        btnLocationDetails.setOnClickListener(v -> locationDetails());
        btnScreenshot.setOnClickListener(v -> {
           View v1 = getWindow().getDecorView().getRootView();
           v1.setDrawingCacheEnabled(true);
           v1.buildDrawingCache();
           share(screenShot(v1));
        });

        btnSendMyLocation.setOnClickListener(v -> {
            sendMyLocation();
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // when
                if(location != null){
                    //sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                            MarkerOptions options = new MarkerOptions().position(latLng)
                                    .title("I am here");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            googleMap.addMarker(options);
                            Geocoder geocoder = new Geocoder(MyLocation.this, Locale.getDefault());
                            List<Address> current_address = null;
                            try {
                                current_address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            tvCurrentLocation.setText(current_address.get(0).getAddressLine(0));
                            latitude = String.valueOf(current_address.get(0).getLatitude());
                            longtitude = String.valueOf(current_address.get(0).getLongitude());
                            city = current_address.get(0).getLocality();
                            country = current_address.get(0).getCountryName();
                            addresss = current_address.get(0).getAddressLine(0);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    void locationDetails(){
        new AlertDialog.Builder(this)
                .setTitle("Your Current Location")
                .setMessage("Lattitude: " + latitude + "\n" +
                        "Longtitude: " + longtitude + "\n" +
                        "Address: " + addresss + "\n" +
                        "Country: " + country + "\n" +
                        "City: " + city + "\n")
                .setPositiveButton("Close",(dialogInterface, i) -> dialogInterface.cancel())
                .setCancelable(false)
                .show();
    }

    private Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        view.layout(0, 0, view.getLayoutParams().width, view.getLayoutParams().height);
        view.draw(canvas);
        return bitmap;
    }

    private void share(Bitmap bitmap){
        String pathofBmp=
                MediaStore.Images.Media.insertImage(this.getContentResolver(),
                        bitmap,"My Current Location", null);
        Uri uri = Uri.parse(pathofBmp);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Synapse-My-Current-Location");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        this.startActivity(Intent.createChooser(shareIntent, "Share Current Location Via..."));
    }

    private void sendMyLocation(){
            AlertDialog.Builder boite;
            boite = new AlertDialog.Builder(this);
            boite.setTitle("Send Location to Your Carer");
            boite.setIcon(getDrawable(R.drawable.ic_gps_senior));
            boite.setMessage("I am near " + addresss);

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            boite.setView(input);

            boite.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //whatever action
                    getCarerTokenAndSendLocation(input.getText() + " - I am near ", addresss);
                }
            });
            boite.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //whatever action
                }
            });
            boite.show();
        }

  private void getCarerTokenAndSendLocation(String message, String location) {
      referenceAssignedCarer.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              for (DataSnapshot key : snapshot.getChildren()) {
                  String carerKey = key.getKey();

                  referenceAssignedCarer.child(carerKey).addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot snapshot) {
                          for (DataSnapshot key : snapshot.getChildren()) {
                              String key1 = key.getKey();

                              referenceAssignedCarer.child(carerKey).child(key1).addListenerForSingleValueEvent(new ValueEventListener() {
                                  @Override
                                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                                      if(snapshot.hasChild("seniorID")){
                                          ReadWriteUserSenior senior = snapshot.getValue(ReadWriteUserSenior.class);
                                          String seniorID = senior.getSeniorID();

                                          FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

                                          if(seniorID.equals(mUser.getUid())){
                                              referenceCarer.child(carerKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                  @Override
                                                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                      if(snapshot.exists()){
                                                          ReadWriteUserDetails carer = snapshot.getValue(ReadWriteUserDetails.class);
                                                          String carerToken = carer.getToken();

                                                                           FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                                                                                   carerToken,
                                                                                   senior.getFirstName() + " " + senior.getLastName() + " current location",
                                                                                   message + " " + location,
                                                                                   "",
                                                                                   MyLocation.this);
                                                                           notificationsSender.SendNotifications();

                                                      }

                                                  }

                                                  @Override
                                                  public void onCancelled(@NonNull DatabaseError error) {
                                                      promptMessage.defaultErrorMessageContext(MyLocation.this);
                                                  }
                                              });

                                          }
                                      }
                                  }

                                  @Override
                                  public void onCancelled(@NonNull DatabaseError error) {
                                      promptMessage.defaultErrorMessageContext(MyLocation.this);
                                  }
                              });

                          }

                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {
                          promptMessage.defaultErrorMessageContext(MyLocation.this);
                      }
                  });
                 }
              }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {
              promptMessage.defaultErrorMessageContext(MyLocation.this);
          }
      });
  }

}