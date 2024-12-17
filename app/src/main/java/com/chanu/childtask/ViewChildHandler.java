package com.chanu.childtask;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewChildHandler {
    static DatabaseReference Data;
    public static ArrayList<String> viewChildKey = new ArrayList<>();

    @SuppressLint("LongLogTag")
    public static void loadViewChild() {
        ArrayList<String> viewChild = new ArrayList<>();
        ArrayList<String> imgUri = new ArrayList<>();

        Data = FirebaseDatabase.getInstance().getReference();

        Data.child("user").child(common.rDBEmail).child("ChildList").addValueEventListener(new ValueEventListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                viewChildKey.clear();
                viewChild.clear();
                imgUri.clear();
                ViewChild.clearViewChild();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the value of each child in the node
                    viewChild.add(snapshot.child("nick").getValue().toString());
                    viewChildKey.add(snapshot.getKey());
                    String url = "";
                    if (snapshot.child("img").exists())
                        url = snapshot.child("img").getValue().toString();
                    imgUri.add(url);
                }
                for (int i = viewChild.size() - 1; i >= 0; i--) {
                    ViewChild.addViewChild(viewChild.get(i), i, imgUri.get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
