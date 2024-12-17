package com.chanu.childtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;

public class TaskDetails extends AppCompatActivity {

    DatabaseReference Data;
    TextView name, category, time, description;
    PopupWindow popupWindow;
    LinearLayout parentContainer;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        parentContainer = findViewById(R.id.comment_list);
        inflater = LayoutInflater.from(this);

        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);

        name = findViewById(R.id.task_name);
        category = findViewById(R.id.task_category);
        time = findViewById(R.id.task_time);
        description = findViewById(R.id.task_details);

        load();
    }

    public void editTaskName(View view) {
        editValue("name");
    }

    public void removeTask(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Remove")
                .setMessage("Are you sure you want to remove task?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Data.child("user/" + common.rDBEmail + "/TaskList/" + common.taskID).removeValue();
                    startActivity(new Intent(this, Home.class));
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void editTaskCategory(View view) {

    }

    public void editTaskDetails(View view) {
        editValue("description");
    }

    public void editTaskTime(View view) {

    }

    private void load() {
        common.startLoading(this, "Loading");
        Data.child("user/" + common.rDBEmail + "/TaskList/" + common.taskID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("name").getValue().toString());
                category.setText(snapshot.child("category").getValue().toString());
                description.setText(snapshot.child("description").getValue().toString());
                String id = snapshot.child("time").getValue().toString() + "00";
                String dbDate = null;
                try {
                    String date = common.convertToDateFormat(id);
                    String Time = common.convertToDateFormat(id);
                    dbDate = date + "  " + Time;
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                time.setText(dbDate);
                if (snapshot.child("comment").exists()) {
                    for (DataSnapshot snap : snapshot.child("comment").getChildren()) {
                        String comment = snap.getValue().toString();
                        addAComment(comment);
                    }
                }
                common.stopLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                common.stopLoading();
            }
        });
    }

    private void editValue(String key) {
        // Inflate the popup window layout
        View popupView = getLayoutInflater().inflate(R.layout.popup_edit, null);

        // Find the views within the popup window layout
        EditText valueEditText = popupView.findViewById(R.id.valueEditText);
        Button okButton = popupView.findViewById(R.id.okButton);
        Button cancelButton = popupView.findViewById(R.id.cancelButton);

        // Set click listener for the OK button
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = valueEditText.getText().toString();
                // Handle the nickname input as needed
                if (!TextUtils.isEmpty(value)) {
                    Data.child("user/" + common.rDBEmail + "/TaskList/" + common.taskID + "/" + key).setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(TaskDetails.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                popupWindow.dismiss();
            }
        });

        // Set click listener for the Cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        // Create the popup window
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        // Show the popup window at the center of the screen
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    private void addAComment(String comment){
        // Inflate the XML layout to obtain the view object
        LinearLayout xmlView = (LinearLayout) inflater.inflate(R.layout.clickable_comment_list, parentContainer, false);

        // Customize the view or set data to it
        TextView com=xmlView.findViewById(R.id.comment);

        com.setText(comment);

        // Add the view to the parent container
        parentContainer.addView(xmlView);
    }

}

//code for change time
//if(key.equals("time")){
//        Data.child("user/" + common.rDBEmail + "/TaskList/" + common.taskID).addListenerForSingleValueEvent(new ValueEventListener() {
//@Override
//public void onDataChange(@NonNull DataSnapshot snapshot) {
//        Object data = snapshot.getValue();
//        Data.child("user/" + common.rDBEmail + "/TaskList/" + key).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
//@Override
//public void onSuccess(Void unused) {
//
//        }
//        });
//        }
//
//@Override
//public void onCancelled(@NonNull DatabaseError error) {
//
//        }
//        });
//        }