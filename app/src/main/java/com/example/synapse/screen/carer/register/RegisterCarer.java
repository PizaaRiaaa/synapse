package com.example.synapse.screen.carer.register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.synapse.R;
import com.example.synapse.screen.PickRole;
import com.example.synapse.screen.carer.verification.OTP;
import com.example.synapse.screen.util.AuditTrail;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class RegisterCarer extends AppCompatActivity {

    // Global variables
    PromptMessage promptMessage = new PromptMessage();
    AuditTrail auditTrail = new AuditTrail();
    String userType, token;
    private StorageReference storageReference;
    private static final String TAG = "RegisterActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private final String imageURL = "";
    private Uri uriImage;

    private ImageView ivProfilePic;
    private TextInputEditText dropdown_dob;
    private TextInputEditText etAddress;
    private DatePickerDialog datePickerDialog;
    private AutoCompleteTextView autocompleteGender;
    private CheckBox cbAgree;
    ProgressBar progressBar;
    private TextInputEditText
            etFirstName,
            etMiddle,
            etLastName,
            etEmail,
            etPassword,
            etMobileNumber;

    private TextInputLayout
            tilFirstName,
            tilMiddle,
            tilLastName,
            tilDOB,
            tilGender,
            tilEmail,
            tilAddress,
            tilMobileNumber,
            tilPassword;

    String textPassword,
           textGender,
           textMobileNumber,
           textFirstName,
           textMiddle,
           textLastName,
           textEmail,
           textDOB,
           textAddress,
           textCity,
           textDateCreated,
           user_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_carer);

        ImageButton ibBack = findViewById(R.id.ibRegisterCarerBack);
        AppCompatImageView chooseProfilePic = findViewById(R.id.ic_carer_choose_profile_pic);
        Button btnSignup = findViewById(R.id.btnSignupCarer);
        TextView termsAndDataPolicy = findViewById(R.id.termsAndDataPolicy);
        progressBar = findViewById(R.id.progressBarRegister);
        cbAgree = findViewById(R.id.cbAgree);
        etFirstName = findViewById(R.id.etCarerFirstName);
        etMiddle = findViewById(R.id.etCarerMiddle);
        etLastName = findViewById(R.id.etCarerLastName);
        etEmail = findViewById(R.id.etCarerEmail);
        etPassword = findViewById(R.id.etRegisterCarerPassword);
        etMobileNumber = findViewById(R.id.etCarerMobileNumber);
        dropdown_dob = findViewById(R.id.dropdown_dob);
        ivProfilePic = findViewById(R.id.ivCarerProfilePic);
        autocompleteGender = findViewById(R.id.drop_gender);
        etAddress = findViewById(R.id.etAddress);
        tilFirstName = findViewById(R.id.tilFirstName);
        tilMiddle = findViewById(R.id.tilMiddle);
        tilLastName = findViewById(R.id.tilLastName);
        tilDOB = findViewById(R.id.tilDOB);
        tilGender = findViewById(R.id.tilGender);
        tilEmail = findViewById(R.id.tilEmail);
        tilAddress = findViewById(R.id.tilAddress);
        tilMobileNumber = findViewById(R.id.tilMobileNumber);
        tilPassword = findViewById(R.id.tilPassword);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();
                        // Log and toast
                        String msg = token;
                        Log.d("Token:", msg);
                    }
                });

        String [] gender = {"Male","Female"};
        ArrayAdapter<String> itemAdapter2 = new ArrayAdapter<>(RegisterCarer.this, R.layout.dropdown_items, gender);
        autocompleteGender.setAdapter(itemAdapter2);

        ibBack.setOnClickListener(view -> startActivity(new Intent(RegisterCarer.this, PickRole.class)));
        chooseProfilePic.setOnClickListener(view -> openFileChooser());
        termsAndDataPolicy.setOnClickListener(view -> startActivity(new Intent(RegisterCarer.this, TermsAndDataPolicy.class)));

        initDatePicker();
        dropdown_dob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            // 18yrs and above 2022 - 18 = 2004
            calendar.add(Calendar.YEAR, - 18);
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            datePickerDialog.show();
           // dropdown_dob.setText(getTodaysDate());
        });

        etFirstName.addTextChangedListener(textWatcher);
        dropdown_dob.addTextChangedListener(textWatcher);
        autocompleteGender.addTextChangedListener(textWatcher);
        etEmail.addTextChangedListener(textWatcher);
        etAddress.addTextChangedListener(textWatcher);
        etMobileNumber.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
        btnSignup.setOnClickListener(v -> checkValidation());
    }

    String getTodaysDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month  = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month +1;
                String date = makeDateString(day, month, year);
                dropdown_dob.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year){
        return month + " " + day + " " + year;
    }

    void checkValidation() {
        // obtain the entered data
        Date timestamp = Calendar.getInstance().getTime();
        user_token = token;
        userType = "Carer";
        textFirstName = etFirstName.getText().toString();
        textMiddle = etMiddle.getText().toString();
        textLastName = etLastName.getText().toString();
        textEmail = etEmail.getText().toString().trim();
        textGender = autocompleteGender.getText().toString();
        textPassword = etPassword.getText().toString().trim();
        textMobileNumber = etMobileNumber.getText().toString().trim();
        textDOB = dropdown_dob.getText().toString();
        textAddress = etAddress.getText().toString();
        textCity = "Mandaluyong City";
        textGender = autocompleteGender.getText().toString().trim();
        textDateCreated = timestamp.toString();

        // validate mobile number using matcher and regex
        String mobileRegex = "^(09|\\+639)\\d{9}$"; // first no. can be {09 or +639} and rest 9 no. can be any no.
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(etMobileNumber.getText());

        // Password contain at least 6 characters, one digit, and one upper case letter
        String passwordRegex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])\\S{6,}$";
        Matcher passwordMatcher;
        Pattern passwordPattern = Pattern.compile(passwordRegex);
        passwordMatcher = passwordPattern.matcher(etPassword.getText());

        if (checkIfEmpty(etFirstName)) {
            tilFirstName.setError("This field can't be empty");
            tilFirstName.requestFocus();
        }else if(checkIfEmpty(etMiddle)){
            tilMiddle.setError("This field can't be empty");
            tilMiddle.requestFocus();
        }else if(checkIfEmpty(etLastName)){
            tilLastName.setError("This field can't be empty");
            tilLastName.requestFocus();
        }else if(checkIfEmpty(dropdown_dob)){
            tilDOB.setError("This field can't be empty");
            tilDOB.requestFocus();
        }else if(TextUtils.isEmpty(autocompleteGender.getText())){
            tilGender.setError("This field can't be empty");
            tilGender.requestFocus();
        }else if(checkIfEmpty(etEmail)){
            tilEmail.setError("This field can't be empty");
            tilEmail.requestFocus();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()){
            tilEmail.setError("Invalid email. Please re-enter your email");
            tilEmail.requestFocus();
        }else if(checkIfEmpty(etAddress)){
            tilAddress.setError("This field can't be empty");
            tilAddress.requestFocus();
        }else if(checkIfEmpty(etMobileNumber)){
            tilMobileNumber.setError("This field can't be empty");
            tilMobileNumber.requestFocus();
        }else if(etMobileNumber.getText().length() < 10){
            tilMobileNumber.setError("Mobile no. should be 11 digits. e.g. 09166992880");
            tilMobileNumber.requestFocus();
        }else if(!mobileMatcher.find()){
            tilMobileNumber.setError("Mobile no. is not valid. e.g. 091669928880");
            tilMobileNumber.requestFocus();
        }else if(checkIfEmpty(etPassword)){
            tilPassword.setError("This field can't be empty");
            tilPassword.requestFocus();
        }else if(!passwordMatcher.find()){
            tilPassword.setError("Password contain at least 6 characters, 1 digit, and 1 upper case");
            tilPassword.requestFocus();
        }else if(uriImage == null){
            promptMessage.displayMessage("Empty profile picture", "Please select your profile picture", R.color.red_decline_request, this);
        }else if(!cbAgree.isChecked()){
            promptMessage.displayMessage("Check box", "Please check the agreement checkbox to proceed further", R.color.red1, this);
            cbAgree.requestFocus();
        }

        else{
            signupUser(textFirstName,textMiddle, textLastName, textEmail, textMobileNumber, textPassword, textDOB, textAddress, textCity,
                    textGender, userType, imageURL, user_token, textDateCreated);
        }

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
            tilFirstName.setError(null);
            tilMiddle.setError(null);
            tilLastName.setError(null);
            tilGender.setError(null);
            tilEmail.setError(null);
            tilAddress.setError(null);
            tilDOB.setError(null);
            tilMobileNumber.setError(null);
            tilPassword.setError(null);
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    // register user using the credentials given
    private void signupUser(String textFirstName, String textMiddle, String textLastName, String textEmail, String textMobileNumber, String textPassword, String textDOB,
                            String textAddress, String textCity, String textGender, String userType, String imageURL, String textToken, String textDateCreated){

        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Create UserProfile
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterCarer.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){


                            progressBar.setVisibility(View.VISIBLE);

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            // enter user data into the firebase realtime database
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFirstName,textMiddle, textLastName, textEmail,
                                    textMobileNumber, textPassword, textDOB, textAddress, textCity, textGender, userType, imageURL, textToken, textDateCreated);

                            // extracting user reference from database for "Registered Users"
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users").child("Carers");

                            auditTrail.auditTrail(
                                    "Register an account",
                                    textFirstName + " " + textLastName + " " + userType,
                                    "Carer", "Carer", referenceProfile, firebaseUser);

                            // store profile picture of carer
                            storageReference = FirebaseStorage.getInstance().getReference("ProfilePics");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    // user upload profile pic
                                    if(uriImage != null){

                                        // save profile pic with userid filename
                                        StorageReference fileReference = storageReference.child(firebaseUser.getUid() + "."
                                                + getFileExtension(uriImage));

                                        fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {

                                                        Log.d(TAG, "Download URL = "+ uri.toString());

                                                        //Adding that URL to Realtime database
                                                        referenceProfile.child(firebaseUser.getUid()).child("imageURL").setValue(uri.toString());

                                                        // finally set the display image of the user after upload
                                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                .setPhotoUri(uri).build();
                                                        firebaseUser.updateProfile(profileUpdates);
                                                    }
                                                });
                                            }
                                        });
                                    }


                                    // if all inputs are valid
                                    if(task.isSuccessful()){
                                        // send verification email
                                        firebaseUser.sendEmailVerification();

                                        // sign out the user to prevent automatic sign in, right after successful register

                                                            // send mobile and email intent
                                                            Intent intent = new Intent(RegisterCarer.this, OTP.class);
                                                            Bundle data = new Bundle();
                                                            data.putString("Mobile", textMobileNumber);
                                                            intent.putExtras(data);
                                                            startActivity(intent);
                                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                            //auth.signOut();

                                    }else{
                                        Toast.makeText(RegisterCarer.this, "User registered failed. Please try again",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    finish();
                                }
                            });
                        }else{
                            try{
                                throw task.getException();
                            }catch(FirebaseAuthWeakPasswordException e){
                                tilPassword.setError("Your password is to weak. At least 6 characters, 1 digit, 1 upper case");
                                tilPassword.requestFocus();
                            }catch(FirebaseAuthInvalidCredentialsException e){
                                tilEmail.setError("Your email is invalid or already in use. Kindly re-enter");
                                tilEmail.requestFocus();
                            }catch(FirebaseAuthUserCollisionException e){
                                tilEmail.setError("Email is already registered. Please use another email.");
                                tilEmail.requestFocus();
                            }catch(Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(RegisterCarer.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
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

    // obtain file extension of the image
    String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}