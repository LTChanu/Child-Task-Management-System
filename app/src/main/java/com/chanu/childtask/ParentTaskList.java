package com.chanu.childtask;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;

public class ParentTaskList extends AppCompatActivity {

    DatabaseReference Data;
    TextView title;
    LinearLayout parentContainer;
    LayoutInflater inflater;
    PopupWindow popupWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_task_list);

        FirebaseApp.initializeApp(this);
        Data = FirebaseDatabase.getInstance().getReference();

        title = findViewById(R.id.title);
        parentContainer = findViewById(R.id.tasks);
        inflater = LayoutInflater.from(this);

        load();
    }

    private void load() {
        loadTitle();
        loadTasks();
    }

    private void loadTasks() {
        Data.child("user/"+common.childEmail+"/TaskList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parentContainer.removeAllViews();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    // Get the value of each child in the node
                    String id = snapshot.child("time").getValue().toString();
                    String category = snapshot.child("category").getValue().toString();
                    String name = snapshot.child("name").getValue().toString();
                    try {
                        taskAdd(id,category,name);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void taskAdd(String id, String category, String name) throws ParseException {
        // Inflate the XML layout to obtain the view object
        LinearLayout xmlView = (LinearLayout) inflater.inflate(R.layout.clickable_task_list, parentContainer, false);

        // Customize the view or set data to it
        TextView taskCategory=xmlView.findViewById(R.id.task_category);
        TextView taskName=xmlView.findViewById(R.id.task_name);
        TextView taskTime=xmlView.findViewById(R.id.task_time);

        taskCategory.setText(category);
        taskName.setText(name);
        String tId = id+"00";
        String date = common.convertToDateFormat(tId);
        String time = common.convertToTimeFormat(tId);
        String Time = date+"  "+time;
        taskTime.setText(Time);

        xmlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskID = String.valueOf(id);
                // Inflate the popup window layout
                View popupView = getLayoutInflater().inflate(R.layout.popup_comment, null);

                // Find the views within the popup window layout
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                EditText commentEditText = popupView.findViewById(R.id.commentEditText);
                Button okButton = popupView.findViewById(R.id.okButton);
                Button cancelButton = popupView.findViewById(R.id.cancelButton);

                // Set click listener for the OK button
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String comment = commentEditText.getText().toString();
                        // Handle the nickname input as needed
                        if (!TextUtils.isEmpty(comment)) {
                            Data.child("user/" + common.childEmail + "/TaskList/" + taskID + "/comment/"+common.getCDateTime()).setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ParentTaskList.this, "Successfully Added", Toast.LENGTH_SHORT).show();
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
        });

        // Add the view to the parent container
        parentContainer.addView(xmlView);
    }

    private void loadTitle() {
        Data.child("user/"+common.rDBEmail+"/ChildList/"+common.childEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nick = snapshot.child("nick").getValue().toString();
                String tit = nick+ "'s Tasks";
                title.setText(tit);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}