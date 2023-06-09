package com.example.synapse.screen.carer.modules.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.synapse.ForgotPassword;
import com.example.synapse.R;
import com.example.synapse.screen.Login;
import com.example.synapse.screen.util.AuditTrail;
import com.example.synapse.screen.util.PromptMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.A;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePassword#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePassword extends Fragment {

    TextInputEditText etEmail;
    TextInputLayout tilEmail;
    ProgressDialog dialog;
    PromptMessage promptMessage;
    FirebaseAuth auth;
    FirebaseUser mUser;
    DatabaseReference referenceProfile;

    AuditTrail auditTrail = new AuditTrail();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChangePassword() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangePassword.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangePassword newInstance(String param1, String param2) {
        ChangePassword fragment = new ChangePassword();
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
        View view = inflater.inflate(R.layout.fragment_carer_settings_change_password, container, false);

        MaterialButton btnSubmit = view.findViewById(R.id.btnSubmit);
        promptMessage = new PromptMessage();
        referenceProfile = FirebaseDatabase.getInstance().getReference("Users").child("Carers");

        etEmail = view.findViewById(R.id.etEmail);
        tilEmail = view.findViewById(R.id.tilEmail);
        auth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Loading....");

        btnSubmit.setOnClickListener(v -> {
            changePassword();
        });

        return view;
    }

    private Boolean validateEmail(){
        if(TextUtils.isEmpty(etEmail.getText())){
            tilEmail.setError("This field can't be empty");
            tilEmail.requestFocus();
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()){
            tilEmail.setError("Invalid email. Please re-enter your email");
            tilEmail.requestFocus();
            return false;
        }else{
            tilEmail.setError(null);
            return true;
        }
    }

    private void changePassword(){
        if(!validateEmail()){
            return;
        }

        dialog.show();

        auth.sendPasswordResetEmail(etEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if(task.isSuccessful()){
                    auditTrail.auditTrail(
                            "Changed password",auth.getUid(),
                            "Carer", "Carer", referenceProfile, mUser);

                    promptMessage.displayMessage(
                            "Success",
                            "Please Check your Email Address!",
                            R.color.dark_green, getActivity());
                }else{
                    promptMessage.displayMessage(
                            "Error",
                            "Enter your correct email address",
                            R.color.red1, getActivity());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}