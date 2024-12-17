package com.chanu.childtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;

public class DeleteAccount extends AppCompatActivity {

    DatabaseReference Data;
    boolean willDelete = false;

    TextInputLayout inPassword, inReason;
    SharedVariable sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        FirebaseApp.initializeApp(this);
        Data = FirebaseDatabase.getInstance().getReference();

        sv = new SharedVariable(DeleteAccount.this);

        inPassword = findViewById(R.id.inPassword);
        inReason = findViewById(R.id.inFeedback);
        //getEditText().getText().toString()
    }

    public void deleteAccount(View view) {
        willDelete = true;
        String password = inPassword.getEditText().getText().toString();
        if (!password.isEmpty()) {
            Data.child("user/" + common.rDBEmail + "/LoginData").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (willDelete) {
                        willDelete = false;
                        String pass = null;
                        try {
                            pass = common.hashString(password);
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                        if (snapshot.child("password").getValue().toString().equals(pass)){
                            Data.child("deleteUser/"+common.rDBEmail).setValue(inReason.getEditText().getText().toString());
                            if(common.isParent)
                                deleteFromChild();
                            else
                                deleteFromParent();
                            Data.child("user/"+common.rDBEmail).removeValue();
                            sv.setWhileLogin("", false, false, false);
                            startActivity(new Intent(DeleteAccount.this, Welcome.class));
                            finishAffinity();
                        }else
                            Toast.makeText(DeleteAccount.this, "Wrong password.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else
            Toast.makeText(this, "Please Enter Password.", Toast.LENGTH_SHORT).show();
    }

    private void deleteFromParent() {
        willDelete=true;
        Data.child("user/"+common.rDBEmail+"/ParentList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(willDelete){
                    willDelete=false;
                    for (DataSnapshot snap : snapshot.getChildren()){
                        String mail = snap.getKey().toString();
                        Data.child("user/"+mail+"/ChildList/"+common.rDBEmail).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteFromChild() {
        willDelete=true;
        Data.child("user/"+common.rDBEmail+"/ChildList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(willDelete){
                    willDelete=false;
                    for (DataSnapshot snap : snapshot.getChildren()){
                        String mail = snap.getKey().toString();
                        Data.child("user/"+mail+"/ParentList/"+common.rDBEmail).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}