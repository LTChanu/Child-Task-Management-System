package com.chanu.childtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.NoSuchAlgorithmException;

public class ResetPassword extends AppCompatActivity {

    DatabaseReference Data;
    TextInputLayout password, cPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        FirebaseApp.initializeApp(this);
        Data = FirebaseDatabase.getInstance().getReference();

        password = findViewById(R.id.inPassword);
        cPassword = findViewById(R.id.inCPassword);
    }

    public void resetPassword(View view) throws NoSuchAlgorithmException {
        String stringPass=password.getEditText().getText().toString();
        String stringCPass=cPassword.getEditText().getText().toString();

        if(stringPass.equals(stringCPass)){
            common.startLoading(this, "Password Resetting");
            String hashPass = common.hashString(stringPass);
            Data.child("user").child(common.rDBEmail).child("LoginData").child("password").setValue(hashPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    new sendEmail(common.rEmail,"Successfully Reset Password", "Dear user,\n\nYour Password reset successful.\n\nIf didn't you request this, Please change your password in Child Manager Application");
                    common.stopLoading();
                    Toast.makeText(ResetPassword.this, "Successfully Reset Password", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ResetPassword.this, SignIn.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    common.stopLoading();
                    Toast.makeText(ResetPassword.this, "Password Not Reset!", Toast.LENGTH_LONG).show();
                }
            });
        }else
            Toast.makeText(this, "Please Enter same Password two times.", Toast.LENGTH_LONG).show();
    }
}