package com.chanu.childtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HowToUse extends AppCompatActivity {

    DatabaseReference Data;
    LinearLayout parentContainer;
    LayoutInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_use);

        FirebaseApp.initializeApp(this);
        Data = FirebaseDatabase.getInstance().getReference();

        parentContainer = findViewById(R.id.video_list);
        inflater = LayoutInflater.from(this);

        load();
    }

    private void load(){
        Data.child("video").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parentContainer.removeAllViews();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String url = snapshot.child("url").getValue().toString();
                    String name = snapshot.child("name").getValue().toString();
                    videoAdd(name,url);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void videoAdd(String name, String url){
        // Inflate the XML layout to obtain the view object
        LinearLayout xmlView = (LinearLayout) inflater.inflate(R.layout.clickable_video_list, parentContainer, false);

        // Customize the view or set data to it
        TextView videoName=xmlView.findViewById(R.id.video_name);
        String youtubeLink = url;

        videoName.setText(name);

        xmlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Check if the YouTube app is installed
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Open the YouTube link in the app
                    startActivity(intent);
                } else {
                    // If the YouTube app is not installed, open the link in a web browser
                    intent.setPackage(null);
                    startActivity(intent);
                }
            }
        });

        // Add the view to the parent container
        parentContainer.addView(xmlView);
    }
}