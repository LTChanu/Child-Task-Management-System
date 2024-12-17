package com.chanu.childtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmailVerification extends AppCompatActivity {

    DatabaseReference Data;
    Button submitBtn, sendBtn;
    TextInputLayout otp;
    private String genOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        FirebaseApp.initializeApp(this);
        Data = FirebaseDatabase.getInstance().getReference();

        submitBtn = findViewById(R.id.submitBtn);
        sendBtn = findViewById(R.id.sendBtn);
        otp = findViewById(R.id.inOTP);

        submitBtn.setEnabled(false);
        otp.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp.getEditText().getText().toString().length() == 6) {
                    submitBtn.setEnabled(true);
                } else {
                    submitBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void submit(View view) {
        if (otp.getEditText().getText().toString().equals(genOTP)) {
            common.startLoading(this,"Registering");
            String regDate = common.getCDateTime();
            Data.child("user").child(common.rDBEmail).child("Notification").child("1").setValue("Welcome To Child Manage");
            Data.child("user").child(common.rDBEmail).child("LoginData").child("name").setValue(common.rName);
            Data.child("user").child(common.rDBEmail).child("LoginData").child("email").setValue(common.rEmail);
            Data.child("user").child(common.rDBEmail).child("LoginData").child("password").setValue(common.rPassword);
            Data.child("user").child(common.rDBEmail).child("LoginData").child("isGoogleLogin").setValue("0");
            Data.child("user").child(common.rDBEmail).child("LoginData").child("regDate").setValue(regDate);
            Data.child("user").child(common.rDBEmail).child("LoginData").child("userType").setValue(common.rType).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    common.isGoogleLogin = false;
                    common.isLogin = true;
                    new SharedVariable(EmailVerification.this).setIsLogIn(true);
                    new SharedVariable(EmailVerification.this).setIsGoogle(false);
                    new SharedVariable(EmailVerification.this).setDBEmail(common.rDBEmail);
                    common.stopLoading();
                    Toast.makeText(EmailVerification.this, "Successfully Registered", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(EmailVerification.this, Home.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    common.stopLoading();
                    Toast.makeText(EmailVerification.this, "Database Error", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(EmailVerification.this, "OTP is invalid", Toast.LENGTH_LONG).show();
        }
    }

    public void sendOTP(View view) {
        common.startLoading(this,"Sending OTP");
        genOTP = common.generateOTP();
        String body = "Dear " + common.rName + ", \n\nWelcome to Child Manage Application. \n\nYour Registration OTP is " + genOTP + ".\n\n If didn't you request this, Please ignore this massage";
        sendEmail se = new sendEmail(common.rEmail, "Child Manage Registration OTP", body);
        boolean isSent = se.getIsSent();
        common.stopLoading();
        if (isSent) {
            Toast.makeText(EmailVerification.this, "Email sent.", Toast.LENGTH_LONG).show();
            sendBtn.setEnabled(false); // disable the button initially
            new CountDownTimer(60000, 1000) { // 60000 milliseconds = 1 minutes
                public void onTick(long millisUntilFinished) {
                    // do nothing
                }

                public void onFinish() {
                    sendBtn.setEnabled(true); // enable the button after 5 minutes
                }
            }.start();
        } else
            Toast.makeText(EmailVerification.this, "Email not send!", Toast.LENGTH_LONG).show();
    }


}