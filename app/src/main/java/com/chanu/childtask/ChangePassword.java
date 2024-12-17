package com.chanu.childtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;

public class ChangePassword extends AppCompatActivity {

    DatabaseReference Data;
    TextInputLayout inOldPassword, inPassword, inCPassword;
    boolean willChange = false; // for block change password while onchange

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        FirebaseApp.initializeApp(this);
        Data = FirebaseDatabase.getInstance().getReference();

        inOldPassword = findViewById(R.id.inOldPassword);
        inPassword = findViewById(R.id.inPassword);
        inCPassword = findViewById(R.id.inCPassword);
    }

    public void changePassword(View view) {
        willChange = true;
        common.startLoading(this, "Changing");
        String oldPassword = inOldPassword.getEditText().getText().toString();
        String password = inPassword.getEditText().getText().toString();
        String CPassword = inCPassword.getEditText().getText().toString();

        if (!oldPassword.isEmpty() && !password.isEmpty() && !CPassword.isEmpty()) {
            if (password.equals(CPassword)) {
                Data.child("user/" + common.rDBEmail + "/LoginData").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (willChange) {
                            willChange = false;
                            String hashPass = null;
                            try {
                                hashPass = common.hashString(oldPassword);
                            } catch (NoSuchAlgorithmException e) {
                                throw new RuntimeException(e);
                            }
                            String finalHashPass = hashPass;

                            if (snapshot.child("password").getValue().toString().equals(finalHashPass)) {
                                String Pass = null;
                                try {
                                    Pass = common.hashString(password);
                                } catch (NoSuchAlgorithmException e) {
                                    throw new RuntimeException(e);
                                }
                                String finalPass = Pass;
                                Data.child("user").child(common.rDBEmail).child("LoginData").child("password").setValue(finalPass).addOnSuccessListener(unused -> {
                                    inOldPassword.getEditText().setText("");
                                    inPassword.getEditText().setText("");
                                    inCPassword.getEditText().setText("");
                                    Toast.makeText(ChangePassword.this, "Successfull", Toast.LENGTH_LONG).show();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(ChangePassword.this, "Database Error", Toast.LENGTH_LONG).show();
                                });
                            } else {
                                common.showMessage(ChangePassword.this, "Wrong", "Old Password is wrong. Please enter correct one or you can reset password after log out");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Toast.makeText(this, "incorrect confirm password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show();
        }
        common.stopLoading();
    }
}