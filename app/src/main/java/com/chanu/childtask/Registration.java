package com.chanu.childtask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;

public class Registration extends AppCompatActivity {

    DatabaseReference Data;
    SharedVariable sharedVariable;
    String userType = "child";
    TextInputLayout name, email, password, cPassword;
    TextView cPassNote;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        FirebaseApp.initializeApp(this);
        Data = FirebaseDatabase.getInstance().getReference();

        name = findViewById(R.id.inName);
        email = findViewById(R.id.inEmail);
        password = findViewById(R.id.inPassword);
        cPassword = findViewById(R.id.inConfirmPassword);
        cPassNote = findViewById(R.id.cPassNote);

        sharedVariable = new SharedVariable(this);
        sharedVariable.setIsParent(false);
        sharedVariable.setMood("study");
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch mySwitch = findViewById(R.id.isParentBtn);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userType = "parent";
                    sharedVariable.setIsParent(true);
                } else {
                    userType = "child";
                    sharedVariable.setIsParent(false);
                }
            }
        });
    }

    public void register(View view) {
        common.startLoading(this,"Tyring");
        cPassNote.setText("");
        String pass = password.getEditText().getText().toString();
        String cPass = cPassword.getEditText().getText().toString();
        String stringName = name.getEditText().getText().toString();
        String stringEmail = email.getEditText().getText().toString();
        String dbEmail = stringEmail.replaceAll("[.$\\[\\]#/\\\\]", "");
        if(!stringName.isEmpty() && !stringEmail.isEmpty() && !pass.isEmpty() && !cPass.isEmpty()) {
            if (pass.equals(cPass)) {
                if (isNetworkConnected()) {
                    Data.child("user").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(dbEmail).exists()) {
                                common.showMessage(Registration.this, "Please Login", "Your Are Already Registered.");
                                common.stopLoading();
                            } else {
                                common.rName = stringName;
                                common.rEmail = stringEmail;
                                try {
                                    common.rPassword = common.hashString(pass);
                                } catch (NoSuchAlgorithmException e) {
                                    throw new RuntimeException(e);
                                }
                                common.rType = userType;
                                common.rDBEmail = dbEmail;
                                common.stopLoading();
                                startActivity(new Intent(Registration.this, EmailVerification.class));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            common.stopLoading();
                        }
                    });

                } else {
                    // No internet connection available
                    common.stopLoading();
                    Toast.makeText(Registration.this, "No internet connection available", Toast.LENGTH_LONG).show();
                }
            } else {
                common.stopLoading();
                cPassNote.setText("Confirm password is not match.");
            }
        }else {
            common.stopLoading();
            Toast.makeText(Registration.this, "Please fill all details.", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void showPass(View view) {
    }

    public void showComPass(View view) {
    }

    public void onBackPressed(){
        startActivity(new Intent(this, Welcome.class));
    }
    public void openLogin(View view) {
        startActivity(new Intent(this, SignIn.class));
    }
}