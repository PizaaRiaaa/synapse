package com.example.osca_admin.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osca_admin.R;
import com.example.osca_admin.util.AuditTrail;
import com.example.osca_admin.util.PromptMessage;
import com.example.osca_admin.util.ReplaceFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAdminFragment extends Fragment {

    PromptMessage promptMessage = new PromptMessage();
    AuditTrail auditTrail = new AuditTrail();
    ReplaceFragment replaceFragment = new ReplaceFragment();

    DatabaseReference referenceAdmin;

    TextInputLayout tilUsername;
    TextInputLayout tilFirstname;
    TextInputLayout tilLastname;
    TextInputLayout tilEmail;
    TextInputLayout tilPassword;

    TextInputEditText etUsername;
    TextInputEditText etFirstname;
    TextInputEditText etLastname;
    TextInputEditText etEmail;
    TextInputEditText etPassword;

    ImageView ivProfilePic;
    ProgressBar progressBar;

    String textUsername;
    String textFirstname;
    String textLastname;
    String textEmail;
    String textPassword;
    String textDateCreated;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "admin";
    private final String imageURL = "";

    private Uri uriImage;
    boolean isUsernameTaken = false;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddAdminFragment newInstance(String param1, String param2) {
        AddAdminFragment fragment = new AddAdminFragment();
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

    void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null & data.getData() != null){
            uriImage = data.getData();
            Picasso.get()
                    .load(uriImage)
                    //.fit()
                    .transform(new CropCircleTransformation())
                    .into(ivProfilePic);
        }
    }

    String getFileExtension(Uri uri){
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    boolean checkIfEmpty(TextInputEditText tit){
        return TextUtils.isEmpty(tit.getText());
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            tilUsername.setError(null);
            tilFirstname.setError(null);
            tilLastname.setError(null);
            tilEmail.setError(null);
            tilPassword.setError(null);
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    void checkValidation() {
        // obtain the entered data
        Date timestamp = Calendar.getInstance().getTime();
        textUsername = etUsername.getText().toString().trim();
        textFirstname = etFirstname.getText().toString();
        textLastname = etLastname.getText().toString();
        textEmail = etEmail.getText().toString().trim();
        textPassword = etPassword.getText().toString().trim();
        textDateCreated = timestamp.toString();

        // Password contain at least 6 characters, one digit, and one upper case letter
        String passwordRegex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])\\S{6,}$";
        Matcher passwordMatcher;
        Pattern passwordPattern = Pattern.compile(passwordRegex);
        passwordMatcher = passwordPattern.matcher(etPassword.getText());

        referenceAdmin.child("Admins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot adminKey : snapshot.getChildren()){
                    referenceAdmin.child("Admins").child(adminKey.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ignored : snapshot.getChildren()){
                                String adminUser = snapshot.child("userName").getValue().toString();
                                if(textUsername.equals(adminUser)){
                                    isUsernameTaken = true;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if (checkIfEmpty(etUsername)) {
            tilUsername.setError("This field can't be empty");
            tilUsername.requestFocus();
        }else if(isUsernameTaken == true){
            tilUsername.setError("Username is already taken");
            tilUsername.requestFocus();
        }
        else if(checkIfEmpty(etFirstname)){
            tilFirstname.setError("This field can't be empty");
            tilFirstname.requestFocus();
        }else if(checkIfEmpty(etLastname)){
            tilLastname.setError("This field can't be empty");
            tilLastname.requestFocus();
        }else if(checkIfEmpty(etEmail)){
            tilEmail.setError("This field can't be empty");
            tilEmail.requestFocus();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()){
            tilEmail.setError("Invalid email. Please re-enter your email");
            tilEmail.requestFocus();
        }else if(checkIfEmpty(etPassword)){
            tilPassword.setError("This field can't be empty");
            tilPassword.requestFocus();
        }else if(!passwordMatcher.find()){
            tilPassword.setError("Password contain at least 6 characters, 1 digit, and 1 upper case");
            tilPassword.requestFocus();
        } else if(uriImage == null){
            promptMessage.displayMessage(
                    "Empty profile picture",
                    "Please select your profile picture",
                    R.color.red_decline_request, getActivity());
        }

        else{
            isUsernameTaken = false;
            addAdmin(textUsername, textFirstname, textLastname, textEmail, textPassword, imageURL, textDateCreated);{
            }
        }
    }

    void addAdmin(String username, String firstname, String lastname, String email, String password, String imageURL, String textDateCreated){
        HashMap<String , Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userName", username);
        hashMap.put("firstName", firstname);
        hashMap.put("lastName", lastname);
        hashMap.put("email", email);
        hashMap.put("password", password);
        hashMap.put("imageURL", imageURL);
        hashMap.put("dateCreated", textDateCreated);

        String id = referenceAdmin.push().getKey();

        auditTrail.auditTrail("Added New Admin",
                firstname + " " + lastname,
                "Admin", "Admin", referenceAdmin,
                getDefaults("userKey", getActivity()));

        referenceAdmin.child("Admins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   progressBar.setVisibility(View.VISIBLE);
                   referenceAdmin.child("Admins").child(id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(uriImage != null){
                               StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProfilePics");
                               StorageReference fileReference = storageReference.child(id + "." + getFileExtension(uriImage));
                               fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                   @Override
                                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                       fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                           @Override
                                           public void onSuccess(Uri uri) {

                                               Log.d(TAG, "Download URL = "+ uri.toString());

                                               //Adding that URL to Realtime database
                                               referenceAdmin.child("Admins").child(id).child("imageURL").setValue(uri.toString());

                                               // finally set the display image of the user after upload
                                               //UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                               //        .setPhotoUri(uri).build();
                                               //firebaseUser.updateProfile(profileUpdates);

                                           }
                                       });
                                   }
                               });
                           }
                           if(task.isSuccessful()){
                               progressBar.setVisibility(View.GONE);
                               etUsername.setText("");
                               etFirstname.setText("");
                               etLastname.setText("");
                               etEmail.setText("");
                               etPassword.setText("");
                               ivProfilePic.setBackground(null);
                               promptMessage.displayMessage(
                                       "Successful",
                                       "Admin has been added successfully",
                                       R.color.dark_green, getActivity());
                           }
                       }
                   });
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

        // force app to light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_admin, container, false);

        referenceAdmin = FirebaseDatabase.getInstance().getReference("Users");

        MaterialButton btnAddAdmin = view.findViewById(R.id.btnAddAdmin);
        ImageView ibBack = view.findViewById(R.id.ibBack);
        TextView tvUploadProfile = view.findViewById(R.id.tvUploadProfile);

        progressBar = view.findViewById(R.id.progressBarRegister);
        tilUsername = view.findViewById(R.id.tilUserName);
        tilFirstname = view.findViewById(R.id.tilFirstName);
        tilLastname = view.findViewById(R.id.tilLastName);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);

        etUsername = view.findViewById(R.id.etUsername);
        etFirstname = view.findViewById(R.id.etFirstName);
        etLastname = view.findViewById(R.id.etLastName);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);

        btnAddAdmin.setOnClickListener(v -> checkValidation());
        tvUploadProfile.setOnClickListener(v -> openFileChooser());
        ibBack.setOnClickListener(v -> replaceFragment.replaceFragment(new HomeFragment(), getActivity()));

        etUsername.addTextChangedListener(textWatcher);
        etFirstname.addTextChangedListener(textWatcher);
        etLastname.addTextChangedListener(textWatcher);
        etEmail.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);

        return view;
    }
}