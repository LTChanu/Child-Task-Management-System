package com.chanu.childtask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Feedback extends AppCompatActivity {

    DatabaseReference Data;

    TextInputLayout inFeedback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        FirebaseApp.initializeApp(this);
        Data = FirebaseDatabase.getInstance().getReference();

        inFeedback = findViewById(R.id.inFeedback);
    }

    public void submitFeedback(View view) {;
        String feedback = inFeedback.getEditText().getText().toString();
        if(!feedback.isEmpty()){
            String time = common.getCDateTime();
            Data.child("Feedbacks/"+common.rDBEmail+"/"+time).setValue(feedback).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    inFeedback.getEditText().setText("");
                    Toast.makeText(Feedback.this, "Successfully added", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this, "Please type something.", Toast.LENGTH_SHORT).show();
        }
    }
}