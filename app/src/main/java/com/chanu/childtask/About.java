package com.chanu.childtask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void openLinkedIn(View view) {
        String linkedinProfileUrl = "https://www.linkedin.com/in/chanuka-liyanage/";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(linkedinProfileUrl));
        intent.setPackage("com.linkedin.android");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // LinkedIn app is not installed, fallback to opening in a browser
            intent.setPackage(null);
            startActivity(intent);
        }
    }
}