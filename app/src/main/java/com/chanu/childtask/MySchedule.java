package com.chanu.childtask;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;

public class MySchedule extends Fragment {
    private static LinearLayout parentLayout;
    private static Context context;
    private static LayoutInflater inflater;

    static boolean willLoad=false;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_schedule, container, false);

        // Find the parent layout in the fragment view
        parentLayout = view.findViewById(R.id.task_list);
        MySchedule.inflater = inflater;

        getData();

        return view;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        MySchedule.context = context;
    }

    private static void getData() {
        willLoad=true;
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
        Data.child("user/" + common.rDBEmail + "/TaskList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(willLoad) {
                    willLoad=false;
                    parentLayout.removeAllViews();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String id = "";
                        if (snapshot.child("time").getValue() != null) {
                            id = snapshot.child("time").getValue().toString();
                        }
                        String name = "";
                        if (snapshot.child("name").getValue() != null) {
                            name = snapshot.child("name").getValue().toString();
                        }
                        String category = "";
                        if (snapshot.child("category").getValue() != null) {
                            category = snapshot.child("category").getValue().toString();
                        }
                        TaskAdd(name, id, category);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static void TaskAdd(String name, String id, String category) {
        // Inflate the custom layout XML file for the clickable element
        View clickableElement = inflater.inflate(R.layout.clickable_task_list, parentLayout, false);

        // Find the image view and text view in the clickable element
        TextView nameTextView = clickableElement.findViewById(R.id.task_name);
        TextView categoryTextView = clickableElement.findViewById(R.id.task_category);
        TextView timeTextView = clickableElement.findViewById(R.id.task_time);

        String date = null;
        String ID = id+"00";
        String taskID = id;
        try {
            date = common.convertToDateFormat(ID);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String time = null;
        try {
            time = common.convertToTimeFormat(ID);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String lastTime = date + "  " + time;

        nameTextView.setText(name);
        categoryTextView.setText(category);
        timeTextView.setText(lastTime);

        // Set an onClickListener for the clickable element
        clickableElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                common.taskID = taskID;
                context.startActivity(new Intent(context, TaskDetails.class));
            }
        });

        // Add the clickable element to the parent layout
        parentLayout.addView(clickableElement);
    }
}