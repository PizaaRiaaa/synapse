package com.example.osca_admin.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.osca_admin.R;
import com.example.osca_admin.util.AuditTrail;
import com.example.osca_admin.util.PromptMessage;
import com.example.osca_admin.util.readwrite.ReadWriteCarer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.checkerframework.checker.units.qual.A;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class CarerInfoActivity extends AppCompatActivity {

    DatabaseReference referenceCarer;
    DatabaseReference referenceAdmin;

    private StorageReference storageReference;
    private DatePickerDialog datePickerDialog;
    private static final String TAG = "RegisterActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private final String imageURL = "";
    private String carerKey;
    private Uri uriImage;

    String [] genders = {"Male","Female"};
    ArrayAdapter<String> itemAdapter2;

    TextInputEditText etID;
    TextInputEditText etDateCreated;
    TextInputEditText etFirstName;
    TextInputEditText etMiddle;
    TextInputEditText etLastName;
    TextInputEditText etDOB;
    TextInputEditText etEmail;
    TextInputEditText etAddress;
    TextInputEditText etMobile;

    TextInputLayout tilFirstName;
    TextInputLayout tilMiddle;
    TextInputLayout tilLastName;
    TextInputLayout tilFDOB;
    TextInputLayout tilGender;
    TextInputLayout tilEmail;
    TextInputLayout tilAddress;
    TextInputLayout tilMobile;

    ImageView ivCarerProfilePicture;
    ImageView ivBack;
    TextView tvUploadProfile;
    AutoCompleteTextView etGender;
    MaterialButton btnUpdate;
    MaterialButton btnDelete;

    AuditTrail auditTrail;
    PromptMessage promptMessage;

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
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

        datePickerDialog = new DatePickerDialog(CarerInfoActivity.this, style, dateSetListener, year, month, day);
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
                    .transform(new CropCircleTransformation())
                    .into(ivCarerProfilePicture);
        }
    }

    // obtain file extension of the image
    String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    void displayCarerInfo(String carerKey){
        referenceCarer.child(carerKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    ReadWriteCarer carer = snapshot.getValue(ReadWriteCarer.class);
                    etID.setText(carerKey);
                    etDateCreated.setText(carer.getDate_created());
                    etFirstName.setText(carer.getFirstName());
                    etLastName.setText(carer.getLastName());
                    etMiddle.setText(carer.getMiddle());
                    etDOB.setText(carer.getDob());
                    etEmail.setText(carer.getEmail());
                    etAddress.setText(carer.getAddress());
                    etMobile.setText(carer.getMobileNumber());

                    String gender = carer.getGender();

                    for(int i = 0; i < genders.length; i++){
                        if(Objects.equals(gender, genders[i])){
                            etGender.setText(itemAdapter2.getItem(i), false);
                        }
                    }

                    Picasso.get()
                            .load(carer.getImageURL())
                            .transform(new CropCircleTransformation())
                            .into(ivCarerProfilePicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void checkValidation(String carerKey){
        if(TextUtils.isEmpty(etFirstName.getText())){
            tilFirstName.setError("This field can't be empty");
            tilFirstName.requestFocus();
        }else if(TextUtils.isEmpty(etLastName.getText())){
            tilLastName.setError("This field can't be empty");
            tilLastName.requestFocus();
        }else if(TextUtils.isEmpty(etMiddle.getText())){
            tilMiddle.setError("This field can't be empty");
            tilMiddle.requestFocus();
        } else if(TextUtils.isEmpty(etEmail.getText())){
            tilEmail.setError("This field can't be empty");
            tilEmail.requestFocus();
        }else if(TextUtils.isEmpty(etDOB.getText())){
            tilFDOB.setError("This field can't be empty");
            tilFDOB.requestFocus();
        }else if(TextUtils.isEmpty(etGender.getText())){
            tilGender.setError("This field can't be empty");
            tilGender.requestFocus();
        }else if(TextUtils.isEmpty(etAddress.getText())){
            tilAddress.setError("This field can't be empty");
            tilAddress.requestFocus();
        }else{
            updateProfile(carerKey);
        }
    }

    void updateProfile(String carerKey){
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("firstName", etFirstName.getText().toString());
        hashMap.put("middle", etMiddle.getText().toString());
        hashMap.put("lastName", etLastName.getText().toString());
        hashMap.put("gender", etGender.getText().toString());
        hashMap.put("dob", etDOB.getText().toString());
        hashMap.put("address", etAddress.getText().toString());

        auditTrail.auditTrail("Updated Carer Account",
                etFirstName.getText().toString() + " " + etLastName.getText().toString(),
                "Admin", "Admin", referenceAdmin,
                getDefaults("userKey", getApplicationContext()));

        referenceCarer.child(carerKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // store profile picture of carer
                storageReference = FirebaseStorage.getInstance().getReference("ProfilePics");
                // user upload profile pic
                if(uriImage != null){
                    // save profile pic with userid filename
                    StorageReference fileReference = storageReference.child(carerKey + "."
                            + getFileExtension(uriImage));
                    fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Log.d(TAG, "Download URL = "+ uri.toString());

                                    //Adding that URL to Realtime database
                                    referenceCarer.child(carerKey).child("imageURL").setValue(uri.toString());
                                }
                            });
                        }
                    });
                }

                if(task.isSuccessful()){
                    promptMessage.displayMessage(
                            "Success",
                            "Carer info has been updated successfully",
                            R.color.dark_green, CarerInfoActivity.this);
                }
            }
        });
    }

    void deleteCarer(String carerKey){
        referenceCarer.child(carerKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    auditTrail.auditTrail("Deleted Carer Account",
                            etFirstName.getText().toString() + " " + etLastName.getText().toString(),
                            "Admin", "Admin", referenceAdmin,
                            getDefaults("userKey", getApplicationContext()));

                    referenceCarer.child(carerKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(CarerInfoActivity.this, "Carer has been deleted successfully", Toast.LENGTH_SHORT).show();
                                finish();
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

    void displayPromptBeforeDelete(String carerKey){
        new AlertDialog.Builder(this)
                .setTitle("Delete Carer")
                .setMessage("Are you sure you want to delete this carer?")
                .setPositiveButton("Yes",(dialogInterface, i) -> deleteCarer(carerKey))
                .setNegativeButton("No",((dialogInterface, i) -> dialogInterface.cancel()))
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_info);

        referenceCarer = FirebaseDatabase.getInstance().getReference("Users").child("Carers");
        referenceAdmin = FirebaseDatabase.getInstance().getReference("Users").child("Admins");

        etFirstName = findViewById(R.id.etCarerFirstName);
        etLastName = findViewById(R.id.etCarerLastName);
        etMiddle = findViewById(R.id.etCarerMiddle);
        etDOB = findViewById(R.id.dropdown_dob);
        etGender = findViewById(R.id.drop_gender);
        etEmail = findViewById(R.id.etCarerEmail);
        etAddress = findViewById(R.id.etAddress);
        etMobile = findViewById(R.id.etCarerMobileNumber);
        etID = findViewById(R.id.etID);
        etDateCreated = findViewById(R.id.etDateCreated);

        ivCarerProfilePicture = findViewById(R.id.ivCarerProfilePic);
        tvUploadProfile = findViewById(R.id.tvUploadProfile);
        ivBack = findViewById(R.id.ivBack);
        btnUpdate = findViewById(R.id.btnUpdateCarer);
        btnDelete = findViewById(R.id.btnDeleteCarer);

        itemAdapter2 = new ArrayAdapter<String>(CarerInfoActivity.this, R.layout.dropdown_items, genders);
        etGender.setAdapter(itemAdapter2);

        promptMessage = new PromptMessage();
        auditTrail = new AuditTrail();

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

        carerKey = getIntent().getStringExtra( "carerKey");
        if(carerKey != null) displayCarerInfo(carerKey);

        btnUpdate.setOnClickListener(v -> checkValidation(carerKey));
        btnDelete.setOnClickListener(v -> displayPromptBeforeDelete(carerKey));
        tvUploadProfile.setOnClickListener(v -> openFileChooser());
        ivBack.setOnClickListener(v -> finish());
    }
}