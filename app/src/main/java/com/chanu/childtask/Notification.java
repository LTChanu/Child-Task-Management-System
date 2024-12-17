package com.chanu.childtask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Notification extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout parentLayout;
    private View view;
    private static LayoutInflater inflater;
    private static Context context;
    static DatabaseReference Data = FirebaseDatabase.getInstance().getReference();

    @SuppressLint("CutPasteId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Find the parent layout in the fragment view
        parentLayout = view.findViewById(R.id.notification);
        Notification.inflater = inflater;

        NotificationHandler.loadNotification();

        return view;
    }

    public static void addNotification(String title, int id) {
        // Inflate the custom layout XML file for the clickable element
        View clickableElement = inflater.inflate(R.layout.clickable_notification_list, parentLayout, false);

        // Find the image view and text view in the clickable element
        //ImageView imageView = clickableElement.findViewById(R.id.image_view);
        TextView nameTextView = clickableElement.findViewById(R.id.notification_description);

        // Set the image resource ID and person's name for the clickable element
        //imageView.setImageResource(R.drawable.welcomebg1);
        nameTextView.setText(title);
        nameTextView.setId(id);

        // Set an onClickListener for the clickable element
        clickableElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                String key = NotificationHandler.notificationKey.get(id);
                new AlertDialog.Builder(context)
                        .setTitle("Delete Notification")
                        .setMessage("Are you sure you want to Delete?")
                        .setPositiveButton("Yes", (dialog, which) -> delete(key))
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        // Add the clickable element to the parent layout
        parentLayout.addView(clickableElement);
    }

    private static void delete(String key){
        Data.child("user").child(common.rDBEmail).child("Notification").child(key).removeValue();
        NotificationHandler.loadNotification();
    }

    public static void clearNotification() {
        // Remove all views from the parent layout
        parentLayout.removeAllViews();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        Notification.context = context;
    }

}