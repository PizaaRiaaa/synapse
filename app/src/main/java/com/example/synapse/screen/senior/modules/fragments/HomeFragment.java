package com.example.synapse.screen.senior.modules.fragments;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
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
import com.example.synapse.screen.senior.MyLocation;
import com.example.synapse.screen.senior.SearchPeople;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.ReplaceFragment;
import com.example.synapse.screen.util.notifications.FcmNotificationsSender;
import com.example.synapse.screen.util.notifications.wearmessage.MessageServiceStatus;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.example.synapse.screen.util.readwrite.ReadWriteUserSenior;
import com.example.synapse.screen.util.viewholder.SeniorViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // Global variables
    static final String TAG = "wear";
    ReplaceFragment replaceFragment = new ReplaceFragment();

    DatabaseReference referenceSeniorLocation;
    DatabaseReference referenceSenior;
    DatabaseReference referenceAssignedCarer;
    DatabaseReference referenceCarer;
    FirebaseUser mUser;
    String token;

    PromptMessage promptMessage;


    TextView tvSeniorName;
    AppCompatImageView ivProfilePic;
    FusedLocationProviderClient client;

    TextView swap;
    TextView tvHeartRate;
    TextView tvStatus;
    TextView tvStepCounts;

    String city;
    String country;
    String address;
    String carerID;

    Double latitude;
    Double longtitude;

    protected Handler handler;

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

        promptMessage = new PromptMessage();

        TextClock currentTime = view.findViewById(R.id.tcTime);
        MaterialCardView btnMedication = view.findViewById(R.id.btnMedication);
        MaterialCardView btnGames = view.findViewById(R.id.btnGames);
        MaterialCardView btnLogout = view.findViewById(R.id.btnLogout);
        MaterialCardView btnPhysicalActivity = view.findViewById(R.id.btnPhysicalActivity);
        MaterialCardView btnAppointment = view.findViewById(R.id.btnAppointment);
        MaterialCardView btnMyLocation = view.findViewById(R.id.btnMyLocation);
        ivProfilePic = view.findViewById(R.id.ivSeniorProfilePic);
        tvSeniorName = view.findViewById(R.id.tvSeniorFullName);
        tvHeartRate = view.findViewById(R.id.tvHeartRate);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvStepCounts = view.findViewById(R.id.tvStepCounts);

        swap = view.findViewById(R.id.swipe);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = mUser.getUid();
        referenceSenior = FirebaseDatabase.getInstance().getReference("Users").child("Seniors");
        referenceSeniorLocation = FirebaseDatabase.getInstance().getReference("SeniorLocation").child(mUser.getUid());
        referenceAssignedCarer = FirebaseDatabase.getInstance().getReference("AssignedSeniors");
        referenceCarer = FirebaseDatabase.getInstance().getReference("Users").child("Carers");

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnMedication.setOnClickListener(v -> replaceFragment.replaceFragment(new MedicationFragment(), getActivity()));
        btnGames.setOnClickListener(v -> replaceFragment.replaceFragment(new GamesFragment(), getActivity()));
        btnPhysicalActivity.setOnClickListener(v-> replaceFragment.replaceFragment(new PhysicalActivityFragment(), getActivity()));
        btnAppointment.setOnClickListener(v-> replaceFragment.replaceFragment(new AppointmentFragment(), getActivity()));
        btnMyLocation.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyLocation.class)));

        currentTime.setFormat12Hour("hh:mm a");

        showUserProfile(userID);

        // logout user
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth user = FirebaseAuth.getInstance();
            user.signOut();
            Intent intent = new Intent(getActivity(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().onBackPressed();
            //getActivity().getFragmentManager().popBackStack();
        });

        //message handler for the send thread.
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle stuff = msg.getData();
                logthisHeartRate(stuff.getString("logthis1"));
                logthisStatus(stuff.getString("logthis2"));
                logthisStepCounts(stuff.getString("logthis3"));
                return true;
            }
        });

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, messageFilter);

        return view;
    }

    //setup a broadcast receiver to receive the messages from the wear device via the MessageService.
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String heartrate = intent.getStringExtra("heartrate");
            String status = intent.getStringExtra("status");
            String stepcounts = intent.getStringExtra("stepcounts");
            String hhr = intent.getStringExtra("hhr");
            String lhr = intent.getStringExtra("lhr");

            Log.v(TAG, "Main activity received message: " + heartrate);
            Log.v(TAG, "Main activity received message: " + status);
            Log.v(TAG, "Main activity received message: " + stepcounts);
            Log.v(TAG, "Main activity received message: " + hhr);
            Log.v(TAG, "Main activity received message: " + lhr);

            // Display message in UI
            if(heartrate != null) {
                logthisHeartRate(heartrate);
                updateHealthStatus("heartrate", heartrate);
            }

            if(status != null) {
                logthisStatus(status);
                updateHealthStatus("status", status);
            }

            if(stepcounts != null) {
                logthisStepCounts(stepcounts);
                updateHealthStatus("stepcounts", stepcounts);
            }

            if(hhr != null) {
                alertHighHR(hhr);
                sendCarerAlertHighHR(hhr);
            }

            if(lhr != null){
                alertLowHR(lhr);
                sendCarerAlertLowHR(lhr);
            }
        }
    }

    /*
     * simple method to add the log TextView.
     */
    public void logthisHeartRate(String newinfo1) {
        if (newinfo1.compareTo("") != 0) {
            tvHeartRate.setText(newinfo1);
        }
    }

    public void logthisStatus(String newinfo) {
        if (newinfo.compareTo("") != 0) {
            tvStatus.setText(newinfo);
        }
    }

    public void logthisStepCounts(String newinfo) {
        if (newinfo.compareTo("") != 0) {
            tvStepCounts.setText(newinfo);
        }
    }

    public void alertHighHR(String newinfo) {
        if (newinfo.compareTo("") != 0) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("High Heart Rate!!")
                    .setIcon(R.drawable.heartrate)
                    .setMessage("WE DETECTED THAT YOUR HEART RATE ROSE ABOVE ⬆ 120 BPM WHILE YOUR INACTIVE")
                    .setPositiveButton("Close", (dialogInterface, i) -> dialogInterface.cancel())
                    .setCancelable(false)
                    .show();
        }
    }

    public void alertLowHR(String newinfo){
        if(newinfo.compareTo("") != 0) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Low Heart Rate!!")
                    .setIcon(R.drawable.heartrate)
                    .setMessage("WE DETECTED THAT YOUR HEART RATE FELL BELOW ⬇ 40 BPM WHILE YOUR INACTIVE")
                    .setPositiveButton("Close", (dialogInterface, i) -> dialogInterface.cancel())
                    .setCancelable(false)
                    .show();
        }
    }

    public void updateHealthStatus(String title, String newinfo){
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put(title, newinfo);

        referenceSenior.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.v(TAG, "Updated successfully");
            }
        });
    }

    public void sendCarerAlertHighHR(String newinfo) {
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
                                                                    "Alert! High Heart Rate of " + newinfo,
                                                                    "WE DETECTED THAT " + senior.getFirstName().toUpperCase() +
                                                                            " " +  senior.getLastName().toUpperCase() + " " +
                                                                          " HEART RATE ROSE ABOVE ⬆ 120 BPM ",
                                                                    "hhr",
                                                                    getActivity());
                                                            notificationsSender.SendNotifications();

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        promptMessage.defaultErrorMessageContext(getActivity());
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        promptMessage.defaultErrorMessageContext(getActivity());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            promptMessage.defaultErrorMessageContext(getActivity());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessageContext(getActivity());
            }
        });
    }

    public void sendCarerAlertLowHR(String newinfo) {
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
                                                                    "Alert! Low Heart Rate of " + newinfo ,
                                                                    "WE DETECTED THAT " + senior.getFirstName().toUpperCase() + " " +
                                                                            senior.getLastName().toUpperCase() +
                                                                            " HEART RATE FELL BELOW ⬇ 40 BPM",
                                                                    "lhr",
                                                                    getActivity());
                                                            notificationsSender.SendNotifications();

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        promptMessage.defaultErrorMessageContext(getActivity());
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        promptMessage.defaultErrorMessageContext(getActivity());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            promptMessage.defaultErrorMessageContext(getActivity());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessageContext(getActivity());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        generateToken();
        getLocation();
    }

    public void generateToken(){
        // retrieve  and generate token everytime user access the app
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

    public void getLocation(){
        // retrieve senior's current location and store to firebase
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
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
        client.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                            List<Address> current_address = null;

                            try {
                                current_address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            address = current_address.get(0).getAddressLine(0);
                            latitude = current_address.get(0).getLatitude();
                            longtitude = current_address.get(0).getLongitude();
                            city = current_address.get(0).getLocality();
                            country = current_address.get(0).getCountryName();

                            storeLocation(address, latitude, longtitude, city, country);
                        }
                    }
                });
    }

    public void storeLocation(String address, Double latitude,
                              Double longtitude, String city, String country){

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Address", address);
        hashMap.put("Latitude", latitude);
        hashMap.put("Longtitude", longtitude);
        hashMap.put("City", city);
        hashMap.put("Country", country);

        referenceSeniorLocation.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("Location", "Location is successfully stored");
                }
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
    
   // public class Receiver extends BroadcastReceiver{
   //     @Override
   //     public void onReceive(Context context, Intent intent){
   //         // upon receiving message from wearable , display the following text
   //         String message = intent.getExtras().getString("message");
   //         Toast.makeText(getActivity(), "I just received a message from the wearable" + message, Toast.LENGTH_SHORT).show();
   //     }
   // }


}