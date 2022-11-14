package com.example.synapse.screen.carer.modules.fragments;

import static android.app.appsearch.AppSearchResult.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;

import com.example.synapse.R;
import com.example.synapse.screen.carer.register.RegisterCarer;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.ReplaceFragment;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateProfileFragment extends Fragment {

    private StorageReference storageReference;
    private DatePickerDialog datePickerDialog;
    private static final String TAG = "RegisterActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private final String imageURL = "";
    private Uri uriImage;

    FirebaseUser mUser;
    DatabaseReference referenceUser;

    PromptMessage promptMessage;
    ReplaceFragment replaceFragment;

    AutoCompleteTextView etGender;
    AppCompatImageView updatePicture;
    MaterialButton btnUpdate;
    ImageView ivCarerPic;
    ImageView ivBack;

    TextInputEditText etFirstName;
    TextInputEditText etMiddle;
    TextInputEditText etlastName;
    TextInputEditText etDOB;
    TextInputEditText etEmail;
    TextInputEditText etAddress;
    TextInputEditText etMobile;

    TextInputLayout tilFirstName;
    TextInputLayout tilMiddle;
    TextInputLayout tilLastName;
    TextInputLayout tilDOB;
    TextInputLayout tilGender;
    TextInputLayout tilEmail;
    TextInputLayout tilMobile;
    TextInputLayout tilAddress;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UpdateProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateProfileFragment newInstance(String param1, String param2) {
        UpdateProfileFragment fragment = new UpdateProfileFragment();
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

    void displayCarerInfo(){
        referenceUser.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ReadWriteUserDetails carer = snapshot.getValue(ReadWriteUserDetails.class);
                    etFirstName.setText(carer.getFirstName());
                    etMiddle.setText(carer.getMiddle());
                    etlastName.setText(carer.getLastName());
                    etDOB.setText(carer.getDOB());
                    etGender.setText(carer.getGender());
                    etEmail.setText(carer.getEmail());
                    etAddress.setText(carer.address);
                    etMobile.setText(carer.getMobileNumber());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void displayProfilePicture(){
        referenceUser.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails userProfile = snapshot.getValue(ReadWriteUserDetails.class);
                if(userProfile != null){
                    Uri uri = mUser.getPhotoUrl();
                    Picasso.get()
                            .load(uri)
                            .transform(new CropCircleTransformation())
                            .into(ivCarerPic);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(getActivity());
            }
        });
    }

    void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month +1;
                String date = makeDateString(day, month, year);
                etDOB.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year){
        return month + " " + day + " " + year;
    }

    void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null & data.getData() != null){
            uriImage = data.getData();
            Picasso.get()
                    .load(uriImage)
                    //.fit()
                    .transform(new CropCircleTransformation())
                    .into(ivCarerPic);
        }
    }

    // obtain file extension of the image
    String getFileExtension(Uri uri){
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    void checkValidation(){
        if(TextUtils.isEmpty(etFirstName.getText())){
            tilFirstName.setError("This field can't be empty");
            tilFirstName.requestFocus();
        }else if(TextUtils.isEmpty(etMiddle.getText())){
            tilMiddle.setError("This field can't be empty");
            tilMiddle.requestFocus();
        }else if(TextUtils.isEmpty(etlastName.getText())){
            tilLastName.setError("This field can't be empty");
            tilLastName.requestFocus();
        }else if(TextUtils.isEmpty(etGender.getText())){
            tilGender.setError("This field can't be empty");
            tilGender.requestFocus();
        }else if(TextUtils.isEmpty(etDOB.getText())){
            tilDOB.setError("This field can't be empty");
            tilDOB.requestFocus();
        }else if(TextUtils.isEmpty(etAddress.getText())){
            tilAddress.setError("This field can't be empty");
            tilAddress.requestFocus();
        }else{
            updateProfile();
        }
    }

    void updateProfile(){
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("firstName", etFirstName.getText().toString());
        hashMap.put("middle", etMiddle.getText().toString());
        hashMap.put("lastName", etlastName.getText().toString());
        hashMap.put("gender", etGender.getText().toString());
        hashMap.put("dob", etDOB.getText().toString());
        hashMap.put("address", etAddress.getText().toString());
        hashMap.put("imageURL", imageURL);

        referenceUser.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // store profile picture of carer
                storageReference = FirebaseStorage.getInstance().getReference("ProfilePics");
                // user upload profile pic
                if(uriImage != null){
                    // save profile pic with userid filename
                    StorageReference fileReference = storageReference.child(mUser.getUid() + "."
                            + getFileExtension(uriImage));
                    fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Log.d(TAG, "Download URL = "+ uri.toString());

                                    //Adding that URL to Realtime database
                                    referenceUser.child(mUser.getUid()).child("imageURL").setValue(uri.toString());

                                    // finally set the display image of the user after upload
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri).build();
                                    mUser.updateProfile(profileUpdates);
                                }
                            });
                        }
                    });
                }

                if(task.isSuccessful()){
                    promptMessage.displayMessage(
                            "Success",
                            "Your profile was updated successfully",
                            R.color.dark_green, getActivity());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carer_update_profile, container, false);

        tilFirstName = view.findViewById(R.id.tilFirstName);
        tilMiddle = view.findViewById(R.id.tilMiddle);
        tilLastName = view.findViewById(R.id.tilLastName);
        tilDOB = view.findViewById(R.id.tilDOB);
        tilGender = view.findViewById(R.id.tilGender);
        tilAddress = view.findViewById(R.id.tilAddress);
        tilMobile = view.findViewById(R.id.tilMobileNumber);
        tilEmail = view.findViewById(R.id.tilEmail);

        ivCarerPic = view.findViewById(R.id.ivCarerProfilePic);
        updatePicture = view.findViewById(R.id.carer_update_profile_pic);
        btnUpdate = view.findViewById(R.id.btnUpdate);


        ivBack = view.findViewById(R.id.ibBack);
        etFirstName = view.findViewById(R.id.etCarerFirstName);
        etMiddle = view.findViewById(R.id.etCarerMiddle);
        etEmail = view.findViewById(R.id.etCarerEmail);
        etlastName = view.findViewById(R.id.etCarerLastName);
        etDOB = view.findViewById(R.id.dropdown_dob);
        etMobile = view.findViewById(R.id.etCarerMobileNumber);
        etGender = view.findViewById(R.id.drop_gender);
        etAddress = view.findViewById(R.id.etAddress);

        promptMessage = new PromptMessage();
        replaceFragment = new ReplaceFragment();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        referenceUser = FirebaseDatabase.getInstance().getReference("Users").child("Carers");

        String [] gender = {"Male","Female"};
        ArrayAdapter<String> itemAdapter2 = new ArrayAdapter<>(getActivity(), R.layout.dropdown_items, gender);
        etGender.setAdapter(itemAdapter2);

        displayCarerInfo();
        displayProfilePicture();

        etDOB.setOnClickListener(v -> {
            initDatePicker();
            etDOB.setOnClickListener(v1 -> {
                Calendar calendar = Calendar.getInstance();
                // 18yrs and above 2022 - 18 = 2004
                calendar.add(Calendar.YEAR, - 18);
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePickerDialog.show();
                // dropdown_dob.setText(getTodaysDate());
            });
        });

        updatePicture.setOnClickListener(v -> openFileChooser());
        btnUpdate.setOnClickListener(v -> checkValidation());
        ivBack.setOnClickListener(v -> replaceFragment.replaceFragment(new HomeFragment(), getActivity()) );

        return view;
    }
}