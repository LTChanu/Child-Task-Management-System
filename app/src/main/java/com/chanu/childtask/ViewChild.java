package com.chanu.childtask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewChild extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout parentLayout;
    private View view;
    private static Context context;
    private static LayoutInflater inflater;
    static DatabaseReference Data;
    static StorageReference storageRef;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_child, container, false);

        Data = FirebaseDatabase.getInstance().getReference();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Find the parent layout in the fragment view
        parentLayout = view.findViewById(R.id.view_child);
        ViewChild.inflater = inflater;

        ViewChildHandler.loadViewChild();

        return view;
    }

    public static void addViewChild(String title, int id, String url) {
        // Inflate the custom layout XML file for the clickable element
        View clickableElement = inflater.inflate(R.layout.clickable_child_list, parentLayout, false);

        // Find the image view and text view in the clickable element
        ImageView imageView = clickableElement.findViewById(R.id.image_view);
        TextView nameTextView = clickableElement.findViewById(R.id.name_text_view);

        // Set the image resource ID and person's name for the clickable element
        imageView.setImageResource(R.drawable.baseline_child_care_24);
        nameTextView.setText(title);
        nameTextView.setId(id);
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
                int childID = nameTextView.getId();
                common.childEmail=ViewChildHandler.viewChildKey.get(childID);
                context.startActivity(new Intent(context, ChildDetails.class));
            }
        });

        // Add the clickable element to the parent layout
        parentLayout.addView(clickableElement);
    }

    public static void clearViewChild() {
        // Remove all views from the parent layout
        parentLayout.removeAllViews();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        ViewChild.context = context;
    }
}