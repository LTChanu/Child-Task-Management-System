package com.chanu.childtask;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class WhoGuideMe extends Fragment {
    private LinearLayout parentLayout;
    private static Context context;
    private static LayoutInflater inflater;
    StorageReference storageRef;
    DatabaseReference Data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Home.setOnTabSelectedListener((TabLayout.OnTabSelectedListener) this);
        View view = inflater.inflate(R.layout.fragment_who_guide_me, container, false);

        // Initialize the inflater object
        WhoGuideMe.inflater = LayoutInflater.from(context);
        FirebaseApp.initializeApp(context);
        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Find the parent layout in the fragment view
        parentLayout = view.findViewById(R.id.who_guide_me);

        common.startLoading(context, "Loading");
        Data.child("user/" + common.rDBEmail + "/ParentList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                parentLayout.removeAllViews();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String id = snap.getKey().toString();
                    String name = snap.child("nick").getValue().toString();
                    String url = "";
                    if (snap.child("img").exists())
                        url = snap.child("img").getValue().toString();
                    addAParent(id, name, url);
                }
                common.stopLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                common.stopLoading();
            }
        });


        return view;

    }

    public void onAttach(Context context) {
        super.onAttach(context);
        WhoGuideMe.context = context;
    }

    private void addAParent(String id, String name, String url) {
        // Inflate the custom layout XML file for the clickable element
        View clickableElement = inflater.inflate(R.layout.clickable_parent_list, parentLayout, false);

        // Find the image view and text view in the clickable element
        ImageView imageView = clickableElement.findViewById(R.id.image_view);
        TextView nameTextView = clickableElement.findViewById(R.id.name_text_view);

        // Set the image resource ID and person's name for the clickable element
        String parentID = id;
        nameTextView.setText(name);
        if (!url.equals("")) {
            storageRef.child(url).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Decode the byte array to a Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    // Display the image in an ImageView
                    imageView.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle any errors that occurred during the download
                    Toast.makeText(context, "Image download Fail.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Set an onClickListener for the clickable element
        clickableElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                common.parentEmail = parentID;
                context.startActivity(new Intent(context, ParentDetails.class));
            }
        });

        // Add the clickable element to the parent layout
        parentLayout.addView(clickableElement);
    }

}