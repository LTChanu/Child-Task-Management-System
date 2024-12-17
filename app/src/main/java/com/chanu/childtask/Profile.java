package com.chanu.childtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;

public class Profile extends AppCompatActivity {

    TextView name, email, type, regData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        type = findViewById(R.id.account_type);
        regData = findViewById(R.id.register_date);

        load();
    }

    public void deleteAccount(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete account?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    startActivity(new Intent(this, DeleteAccount.class));
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void load(){
        common.startLoading(this, "Loading");
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);

        Data.child("user/"+common.rDBEmail+"/LoginData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("name").getValue().toString());
                email.setText(snapshot.child("email").getValue().toString());
                type.setText(snapshot.child("userType").getValue().toString());
                String date = null;
                try {
                    date = common.convertToDateFormat(snapshot.child("regDate").getValue().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                regData.setText(date);
                common.stopLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                common.stopLoading();
            }
        });
    }
}