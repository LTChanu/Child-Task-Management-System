package com.chanu.childtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;

public class ForgotPassword extends AppCompatActivity {

    DatabaseReference Data;
    TextInputLayout email, otp;
    Button submitBtn, sendBtn;
    private String genOTP;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        FirebaseApp.initializeApp(this);
        Data = FirebaseDatabase.getInstance().getReference();

        submitBtn = findViewById(R.id.submitBtn);
        sendBtn = findViewById(R.id.sendBtn);
        otp = findViewById(R.id.inOTP);
        email = findViewById(R.id.inEmail);

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

        sendBtn.setEnabled(false);
        email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!email.getEditText().getText().toString().isEmpty()) {
                    sendBtn.setEnabled(true);
                } else {
                    sendBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void submit(View view) {
        String stringOTP = otp.getEditText().getText().toString();
        if (stringOTP.equals(genOTP)) {
            Toast.makeText(this, "Verified", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ResetPassword.class));
        } else
            Toast.makeText(this, "Wrong OTP", Toast.LENGTH_SHORT).show();
    }

    public void sendOTP(View view) {
        common.startLoading(this,"Sending OTP");
        String stringEmail = email.getEditText().getText().toString();
        String dbEmail = stringEmail.replaceAll("[.$\\[\\]#/\\\\]", "");
        common.rEmail = stringEmail;
        common.rDBEmail = dbEmail;
        common.willSend = true;
        if (isNetworkConnected()) {

            Data.child("user").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (common.willSend) {
                        common.willSend =false;
                        if (snapshot.child(dbEmail).exists()) {
                            genOTP = common.generateOTP();
                            String body = "Dear " + common.rName + ", \n\nYour Password Reset OTP is " + genOTP + ".\n\n If didn't you request this, Please contact admin";
                            sendEmail se = new sendEmail(stringEmail, "Child Manage Password Reset OTP", body);
                            boolean isSent = se.getIsSent();
                            common.stopLoading();
                            if(isSent) {
                                Toast.makeText(ForgotPassword.this, "Email sent.", Toast.LENGTH_LONG).show();
                                sendBtn.setEnabled(false); // disable the button initially
                                new CountDownTimer(60000, 1000) { // 60000 milliseconds = 1 minutes
                                    public void onTick(long millisUntilFinished) {
                                        // do nothing
                                    }

                                    public void onFinish() {
                                        sendBtn.setEnabled(true); // enable the button after 5 minutes
                                    }
                                }.start();
                            } else {
                                Toast.makeText(ForgotPassword.this, "Email not send!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            common.stopLoading();
                            common.showMessage(ForgotPassword.this, "Not Found", "Your Are Not Registered.");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            // No internet connection available
            Toast.makeText(ForgotPassword.this, "No internet connection available", Toast.LENGTH_LONG).show();
        }

    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}