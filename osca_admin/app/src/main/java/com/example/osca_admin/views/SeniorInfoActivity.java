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
import com.google.android.material.textfield.TextInputLayout;
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
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class SeniorInfoActivity extends AppCompatActivity {

    PromptMessage promptMessage = new PromptMessage();
    AuditTrail auditTrail = new AuditTrail();

    DatabaseReference referenceSenior;
    DatabaseReference referenceAdmin;

    private StorageReference storageReference;
    private DatePickerDialog datePickerDialog;
    private static final String TAG = "RegisterActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private final String imageURL = "";
    private String seniorKey;
    private Uri uriImage;

    ArrayAdapter<String> itemAdapter1;
    ArrayAdapter<String> itemAdapter2;
    String [] barangays = {"Addition Hills","Bagong Silang","Barangay Drive","Barangka Ibaba",
            "Barangka Ilaya","Barangka Bato","Burol","Hagdang Bato Itaas","Hagdang Bato Libis",
            "Harapin Ang Bukas","Highway Hills","Hulo","Mabini-J. Rizal","Malamig","Mauway",
            "Namayan","New Zañiga","Pag-asa","Plainview","Pleasant Hills","Poblacion","San Jose",
            "Vergara","Wack-Wack Greehills"};
    String [] genders = {"Male","Female"};

    TextView etID;
    TextView etDateCreated;
    TextView etFirstName;
    TextView etMiddle;
    TextView etLastName;
    TextView etDOB;
    TextView etEmail;
    TextView etMobile;

    TextInputLayout tilFirstName;
    TextInputLayout tilMiddle;
    TextInputLayout tilLastName;
    TextInputLayout tilDOB;
    TextInputLayout tilGender;
    TextInputLayout tilEmail;
    TextInputLayout tilMobile;
    TextInputLayout tilBarangay;

    ImageView ivSeniorProfilePicture;
    ImageView ivBack;
    TextView tvUploadProfile;
    AutoCompleteTextView etGender;
    AutoCompleteTextView etBarangay;
    MaterialButton btnUpdate;
    MaterialButton btnDelete;

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

        datePickerDialog = new DatePickerDialog(SeniorInfoActivity.this, style, dateSetListener, year, month, day);
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
                    .into(ivSeniorProfilePicture);
        }
    }

    // obtain file extension of the image
    String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    void displaySeniorInfo(String seniorKey){
        referenceSenior.child(seniorKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    ReadWriteCarer carer = snapshot.getValue(ReadWriteCarer.class);
                    etID.setText(seniorKey);
                    etDateCreated.setText(carer.getDate_created());
                    etFirstName.setText(carer.getFirstName());
                    etLastName.setText(carer.getLastName());
                    etMiddle.setText(carer.getMiddle());
                    etDOB.setText(carer.getDob());
                    etEmail.setText(carer.getEmail());
                    etMobile.setText(carer.getMobileNumber());

                    String barangay = carer.getAddress();
                    String gender = carer.getGender();

                    for(int i = 0;i < barangays.length; i++){
                      if(Objects.equals(barangay, barangays[i])){
                         etBarangay.setText(itemAdapter1.getItem(i),false);
                      }
                    }

                    for(int i = 0; i < genders.length; i++){
                        if(Objects.equals(gender, genders[i])){
                            etGender.setText(itemAdapter2.getItem(i), false);
                        }
                    }


                    Picasso.get()
                            .load(carer.getImageURL())
                            .transform(new CropCircleTransformation())
                            .into(ivSeniorProfilePicture);
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
            tilDOB.setError("This field can't be empty");
            tilDOB.requestFocus();
        }else if(TextUtils.isEmpty(etGender.getText())){
            tilGender.setError("This field can't be empty");
            tilGender.requestFocus();
        }else if(TextUtils.isEmpty(etBarangay.getText())){
            tilBarangay.setError("This field can't be empty");
            tilBarangay.requestFocus();
        }else{
            updateProfile(carerKey);
        }
    }

    void updateProfile(String seniorKey){
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("firstName", etFirstName.getText().toString());
        hashMap.put("middle", etMiddle.getText().toString());
        hashMap.put("lastName", etLastName.getText().toString());
        hashMap.put("gender", etGender.getText().toString());
        hashMap.put("dob", etDOB.getText().toString());
        hashMap.put("address", etBarangay.getText().toString());

        auditTrail.auditTrail("Updated Senior Account",
                etFirstName.getText().toString() + " " + etLastName.getText().toString(),
                "Admin", "Admin", referenceAdmin,
                getDefaults("userKey", getApplicationContext()));

        referenceSenior.child(seniorKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // store profile picture of carer
                storageReference = FirebaseStorage.getInstance().getReference("ProfilePics");
                // user upload profile pic
                if(uriImage != null){
                    // save profile pic with userid filename
                    StorageReference fileReference = storageReference.child(seniorKey + "."
                            + getFileExtension(uriImage));
                    fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Log.d(TAG, "Download URL = "+ uri.toString());

                                    //Adding that URL to Realtime database
                                    referenceSenior.child(seniorKey).child("imageURL").setValue(uri.toString());
                                }
                            });
                        }
                    });
                }

                if(task.isSuccessful()){
                    promptMessage.displayMessage(
                            "Success",
                            "Senior info has been updated successfully",
                            R.color.dark_green, SeniorInfoActivity.this);
                }
            }
        });
    }

    void deleteSenior(String carerKey){
        referenceSenior.child(carerKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    auditTrail.auditTrail("Deleted Senior Account",
                            etFirstName.getText().toString() + " " + etLastName.getText().toString(),
                            "Admin", "Admin", referenceAdmin,
                            getDefaults("userKey", getApplicationContext()));

                    referenceSenior.child(carerKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SeniorInfoActivity.this, "Senior has been deleted successfully", Toast.LENGTH_SHORT).show();
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
                .setTitle("Delete Senior")
                .setMessage("Are you sure you want to delete this senior?")
                .setPositiveButton("Yes",(dialogInterface, i) -> deleteSenior(seniorKey))
                .setNegativeButton("No",((dialogInterface, i) -> dialogInterface.cancel()))
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_info);

        referenceSenior = FirebaseDatabase.getInstance().getReference("Users").child("Seniors");
        referenceAdmin = FirebaseDatabase.getInstance().getReference("Users").child("Admins");

        etID = findViewById(R.id.etSeniorID);
        etDateCreated = findViewById(R.id.etDateCreated);
        etFirstName = findViewById(R.id.etSeniorFirstName);
        etMiddle = findViewById(R.id.etSeniorMiddle);
        etLastName = findViewById(R.id.etSeniorLastName);
        etDOB = findViewById(R.id.dropdown_dob);
        etBarangay = findViewById(R.id.drop_barangay);
        etMobile = findViewById(R.id.etSeniorMobileNumber);
        etGender = findViewById(R.id.drop_gender);
        etEmail = findViewById(R.id.etSeniorEmail);

        tilFirstName = findViewById(R.id.tilFirstName);
        tilMiddle = findViewById(R.id.tilMiddle);
        tilLastName = findViewById(R.id.tilLastName);
        tilDOB = findViewById(R.id.tilDOB);
        tilGender = findViewById(R.id.tilGender);
        tilBarangay = findViewById(R.id.menuDrop);

        ivBack = findViewById(R.id.ivBack);
        ivSeniorProfilePicture = findViewById(R.id.ivSeniorProfilePic);
        tvUploadProfile = findViewById(R.id.tvUploadProfile);
        btnUpdate = findViewById(R.id.btnUpdateSenior);
        btnDelete = findViewById(R.id.btnDeleteSenior);


        itemAdapter1 = new ArrayAdapter<String>(SeniorInfoActivity.this, R.layout.dropdown_items, barangays);
        etBarangay.setAdapter(itemAdapter1);

        itemAdapter2 = new ArrayAdapter<String>(SeniorInfoActivity.this, R.layout.dropdown_items, genders);
        etGender.setAdapter(itemAdapter2);

        seniorKey = getIntent().getStringExtra( "seniorKey");
        if(seniorKey != null) displaySeniorInfo(seniorKey);

        etDOB.setOnClickListener(v -> {
            initDatePicker();
            etDOB.setOnClickListener(v1 -> {
                Calendar calendar = Calendar.getInstance();
                // 60yrs and above 2022 - 60 = 1964
                calendar.add(Calendar.YEAR, - 60);
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePickerDialog.show();
                // dropdown_dob.setText(getTodaysDate());
            });
        });

        btnUpdate.setOnClickListener(v -> checkValidation(seniorKey));
        btnDelete.setOnClickListener(v -> displayPromptBeforeDelete(seniorKey));
        tvUploadProfile.setOnClickListener(v -> openFileChooser());
        ivBack.setOnClickListener(v -> finish());

    }
}