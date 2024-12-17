package com.chanu.childtask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Stack;

public class NotificationHandler {
    public static ArrayList<String> notifiList = new ArrayList<>();
    static DatabaseReference Data;
    public static ArrayList<String> notificationKey = new ArrayList<>();
    @SuppressLint("LongLogTag")
    public static void loadNotification() {
        ArrayList<String> notification = new ArrayList<>();

        Data = FirebaseDatabase.getInstance().getReference();

        Data.child("user").child(common.rDBEmail).child("Notification").addValueEventListener(new ValueEventListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationKey.clear();
                notification.clear();
                Notification.clearNotification();
                //snapshot.child(common.rDBEmail).child("Notification").child("1").getValue().toString()
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the value of each child in the node
                    notification.add(snapshot.getValue(String.class));
                    notificationKey.add(snapshot.getKey());
                }
                for (int i=notification.size()-1; i>=0; i--) {
                    Notification.addNotification(notification.get(i), i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}