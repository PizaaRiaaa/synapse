package com.example.osca_admin.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.osca_admin.R;
import com.example.osca_admin.util.AuditTrail;
import com.example.osca_admin.util.PromptMessage;
import com.example.osca_admin.util.readwrite.ReadWriteAdmin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class AdminInfoActivity extends AppCompatActivity {

    DatabaseReference referenceAdmin;
    StorageReference storageReference;

    TextInputLayout tilUsername;
    TextInputLayout tilFirstName;
    TextInputLayout tilLastName;
    TextInputLayout tilEmail;
    TextInputLayout tilPassword;

    TextInputEditText etUsername;
    TextInputEditText etFirstName;
    TextInputEditText etLastName;
    TextInputEditText etEmail;
    TextInputEditText etPassword;
    TextInputEditText etID;
    TextInputEditText etDateCreated;

    private static final String TAG = "RegisterActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private final String imageURL = "";
    private Uri uriImage;

    AuditTrail auditTrail;
    PromptMessage promptMessage;

    ImageView ivAdminProfilePic;
    ImageView ibBack;
    MaterialButton btnUpdateAdmin;
    MaterialButton btnDeleteAdmin;
    TextView tvUploadImage;
    String adminKey;

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    void displayAdminInfo(String adminKey){
       referenceAdmin.child(adminKey).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
              if(snapshot.exists()){
                  ReadWriteAdmin admin = snapshot.getValue(ReadWriteAdmin.class);
                  etID.setText(adminKey);
                  etUsername.setText(admin.getUserName());
                  etFirstName.setText(admin.getFirstName());
                  etLastName.setText(admin.getLastName());
                  etEmail.setText(admin.getEmail());
                  etPassword.setText(admin.getPassword());
                  etDateCreated.setText(admin.getDateCreated());

                  Picasso.get()
                          .load(admin.getImageURL())
                          .transform(new CropCircleTransformation())
                          .into(ivAdminProfilePic);
              }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               promptMessage.defaultErrorMessage(AdminInfoActivity.this);
           }
       });
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
                    .into(ivAdminProfilePic);
        }
    }

    String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    void checkValidation(String adminKey){
        // Password contain at least 6 characters, one digit, and one upper case letter
        String passwordRegex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])\\S{6,}$";
        Matcher passwordMatcher;
        Pattern passwordPattern = Pattern.compile(passwordRegex);
        passwordMatcher = passwordPattern.matcher(etPassword.getText());

        if(TextUtils.isEmpty(etUsername.getText())){
            tilUsername.setError("This field can't be empty");
            tilUsername.requestFocus();
        }else if(TextUtils.isEmpty(etFirstName.getText())){
            tilFirstName.setError("This field can't be empty");
            tilFirstName.requestFocus();
        }else if(TextUtils.isEmpty(etLastName.getText())){
            tilLastName.setError("This field can't be empty");
            tilLastName.requestFocus();
        }else if(TextUtils.isEmpty(etEmail.getText())){
            tilEmail.setError("This field can't be empty");
            tilEmail.requestFocus();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()){
            tilEmail.setError("Invalid email. Please re-enter your email");
            tilEmail.requestFocus();
        }else if(TextUtils.isEmpty(etPassword.getText())){
            tilPassword.setError("This fied can't be empty");
            tilPassword.requestFocus();
        }else if(!passwordMatcher.find()){
            tilPassword.setError("Password contain atleast 6 characters, 1 digit, and 1 upper case");
            tilPassword.requestFocus();
        } else if(TextUtils.isEmpty(etPassword.getText())){
            tilPassword.setError("This field can't be empty");
            tilPassword.requestFocus();
        }else{
            updateProfile(adminKey);
        }
    }

    void updateProfile(String adminKey){
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("firstName", etFirstName.getText().toString());
        hashMap.put("lastName", etLastName.getText().toString());
        hashMap.put("email", etEmail.getText().toString().trim());
        hashMap.put("userName", etUsername.getText().toString().trim());
        hashMap.put("password", etPassword.getText().toString().trim());

        auditTrail.auditTrail("Updated Admin Profile",
                etFirstName.getText().toString() + " " + etLastName.getText().toString(),
                "Admin", "Admin", referenceAdmin,
                getDefaults("userKey", getApplicationContext()));

        referenceAdmin.child(adminKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // store profile picture of carer
                storageReference = FirebaseStorage.getInstance().getReference("ProfilePics");
                // user upload profile pic
                if(uriImage != null){
                    // save profile pic with userid filename
                    StorageReference fileReference = storageReference.child(adminKey + "."
                            + getFileExtension(uriImage));
                    fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Log.d(TAG, "Download URL = "+ uri.toString());

                                    //Adding that URL to Realtime database
                                    referenceAdmin.child(adminKey).child("imageURL").setValue(uri.toString());
                                }
                            });
                        }
                    });
                }

                if(task.isSuccessful()){
                    promptMessage.displayMessage(
                            "Success",
                            "Admin info has been updated successfully",
                            R.color.dark_green,
                            AdminInfoActivity.this);
                }
            }
        });
    }

    void deleteAdmin(String adminKey){
        referenceAdmin.child(adminKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    auditTrail.auditTrail("Deleted Admin Account",
                            etFirstName.getText().toString() + " " + etLastName.getText().toString(),
                            "Admin", "Admin", referenceAdmin,
                            getDefaults("userKey", getApplicationContext()));

                    referenceAdmin.child(adminKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(AdminInfoActivity.this, "Admin has been deleted successfully", Toast.LENGTH_SHORT).show();
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

    void displayPromptBeforeDelete(String adminKey){
        new AlertDialog.Builder(this)
                .setTitle("Delete Admin")
                .setMessage("Are you sure you want to delete this admin?")
                .setPositiveButton("Yes",(dialogInterface, i) -> deleteAdmin(adminKey))
                .setNegativeButton("No",((dialogInterface, i) -> dialogInterface.cancel()))
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_info);

        referenceAdmin = FirebaseDatabase.getInstance().getReference("Users").child("Admins");
        auditTrail = new AuditTrail();
        promptMessage = new PromptMessage();

        etUsername = findViewById(R.id.etUsername);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etID = findViewById(R.id.etID);
        etDateCreated = findViewById(R.id.etDateCreated);

        tilUsername = findViewById(R.id.tilUserName);
        tilFirstName = findViewById(R.id.tilFirstName);
        tilLastName = findViewById(R.id.tilLastName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);

        ivAdminProfilePic = findViewById(R.id.ivAdminProfilePic);
        ibBack = findViewById(R.id.ibBack);
        tvUploadImage = findViewById(R.id.tvUploadProfile);
        btnUpdateAdmin = findViewById(R.id.btnUpdateAdmin);
        btnDeleteAdmin = findViewById(R.id.btnDeleteAdmin);

        adminKey = getIntent().getStringExtra( "adminKey");
        if(adminKey != null) displayAdminInfo(adminKey);

        tvUploadImage.setOnClickListener(v -> openFileChooser());
        btnUpdateAdmin.setOnClickListener(v -> checkValidation(adminKey));
        btnDeleteAdmin.setOnClickListener(v -> displayPromptBeforeDelete(adminKey));

        ibBack.setOnClickListener(v -> finish());
    }
}