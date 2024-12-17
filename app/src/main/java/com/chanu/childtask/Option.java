package com.chanu.childtask;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Option extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
    }


    public void logout(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (new SharedVariable(Option.this).getIsGoogle()) {
                        FirebaseAuth.getInstance().signOut();//Sing out google credential
                    }
                    new SharedVariable(Option.this).setIsLogIn(false);
                    startActivity(new Intent(Option.this, Welcome.class));
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void profile(View view) {
        startActivity(new Intent(this, Profile.class));
    }

    public void changePassword(View view) {
        startActivity(new Intent(this, ChangePassword.class));
    }

    public void howToUse(View view) {
        startActivity(new Intent(this, HowToUse.class));
    }

    public void about(View view) {
        startActivity(new Intent(this, About.class));
    }

    public void feedback(View view) {
        startActivity(new Intent(this, Feedback.class));
    }
}