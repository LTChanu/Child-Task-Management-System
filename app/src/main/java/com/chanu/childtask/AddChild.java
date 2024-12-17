package com.chanu.childtask;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;

public class AddChild extends Fragment {

    TextInputEditText childEmail, childPassword;
    DatabaseReference Data;

    boolean willAdd=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_child, container, false);

        Data = FirebaseDatabase.getInstance().getReference();

        Button addChildBtn = view.findViewById(R.id.addChildbtn);
        TextInputLayout email = view.findViewById(R.id.inChildEmail);
        TextInputLayout password = view.findViewById(R.id.inChildPassword);
        childEmail = (TextInputEditText) email.getEditText();
        childPassword = (TextInputEditText) password.getEditText();

        addChildBtn.setEnabled(false);
        childEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addChildBtn.setEnabled(childEmail.getText().length()>0 && childPassword.getText().length()>0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        childPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addChildBtn.setEnabled(childEmail.getText().length()>0 && childPassword.getText().length()>0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                willAdd=true;
                common.startLoading(getActivity(), "Adding");

                Data.child("user").addValueEventListener(new ValueEventListener() {
                    String dbEmail = childEmail.getText().toString().replaceAll("[.$\\[\\]#/\\\\]", "");

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(willAdd) {
                            if (snapshot.child(dbEmail).exists() && snapshot.child(dbEmail).child("LoginData").child("userType").getValue().toString().equals("child")) {
                                willAdd = false;
                                String hashPass = null;
                                try {
                                    hashPass = common.hashString(childPassword.getText().toString());
                                } catch (NoSuchAlgorithmException e) {
                                    throw new RuntimeException(e);
                                }
                                String finalHashPass = hashPass;
                                if (snapshot.child(dbEmail + "/LoginData/password").getValue().toString().equals(finalHashPass) && !snapshot.child(common.rDBEmail + "/ChildList/" + dbEmail).exists() && snapshot.child(dbEmail + "/LoginData/userType").getValue().toString().equals("child")) {
                                    String pName = snapshot.child(common.rDBEmail + "/LoginData/name").getValue().toString();
                                    Data.child("user/" + dbEmail + "/ParentList/" + common.rDBEmail + "/nick").setValue(pName);
                                    String cName = snapshot.child(dbEmail + "/LoginData/name").getValue().toString();
                                    Data.child("user/" + common.rDBEmail + "/ChildList/" + dbEmail + "/nick").setValue(cName).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            childEmail.setText("");
                                            childPassword.setText("");
                                            common.stopLoading();
                                            common.showMessage(getActivity(), "Done", "Child added successfully");
                                            return;
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            common.stopLoading();
                                            common.showMessage(getActivity(), "Error", "Child not add");
                                            return;
                                        }
                                    });

                                } else {
                                    common.stopLoading();
                                    common.showMessage(getActivity(), "Error", "Child not add");
                                    return;
                                }
                            } else {
                                common.stopLoading();
                                common.showMessage(getActivity(), "Error", "Child not add");
                                return;
                            }
                        }else
                            common.stopLoading();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        return view;
    }

    public void addChild(View view) {
    }

}