package com.chanu.childtask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.NoSuchAlgorithmException;

public class ChildDetails extends AppCompatActivity {

    DatabaseReference Data;
    StorageReference storageRef;

    ImageView childImg;
    TextView name, nick;
    EditText nicknameEditText;
    PopupWindow popupWindow;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_details);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }



        FirebaseApp.initializeApp(this);
        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        childImg = findViewById(R.id.childImg);
        name = findViewById(R.id.child_name);
        nick = findViewById(R.id.child_nick_name);
        load();
    }

    public void openMap(View view) {
        startActivity(new Intent(this, Map.class));
    }

    public void editNickName(View view) {
        // Inflate the popup window layout
        View popupView = getLayoutInflater().inflate(R.layout.popup_nick_name, null);

        // Find the views within the popup window layout
        nicknameEditText = popupView.findViewById(R.id.nicknameEditText);
        Button okButton = popupView.findViewById(R.id.okButton);
        Button cancelButton = popupView.findViewById(R.id.cancelButton);

        // Set click listener for the OK button
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = nicknameEditText.getText().toString();
                // Handle the nickname input as needed
                if (!TextUtils.isEmpty(nickname)) {
                    Data.child("user/" + common.rDBEmail + "/ChildList/" + common.childEmail + "/nick").setValue(nickname).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ChildDetails.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
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

    public void changeImg(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void viewTaskList(View view) {
        startActivity(new Intent(this, ParentTaskList.class));
    }

    public void removeChild(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Child")
                .setMessage("Are you sure you want to remove?")
                .setPositiveButton("Yes", (dialog, which) -> remove())
                .setNegativeButton("No", null)
                .show();
    }

    private void remove() {
        Data.child("user/"+common.rDBEmail+"/ChildList/"+common.childEmail).removeValue();
        Data.child("user/"+common.childEmail+"/ParentList/"+common.rDBEmail).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ChildDetails.this, "Successfully Completed", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ChildDetails.this, Home.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChildDetails.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void load() {
        common.startLoading(this, "Loading");
        Data.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dbName = snapshot.child(common.childEmail).child("LoginData").child("name").getValue().toString();
                String dbNick = dbName;
                if (snapshot.child(common.rDBEmail).child("ChildList").child(common.childEmail).child("nick").exists()) {
                    dbNick = snapshot.child(common.rDBEmail).child("ChildList").child(common.childEmail).child("nick").getValue().toString();
                }
                name.setText(dbName);
                nick.setText(dbNick);
                common.nick=dbNick;
                if (snapshot.child(common.rDBEmail).child("ChildList").child(common.childEmail).child("img").exists()) {
                    String imgUri = snapshot.child(common.rDBEmail+"/ChildList/"+common.childEmail+"/img").getValue().toString();

                    storageRef.child(imgUri).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Decode the byte array to a Bitmap
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            // Display the image in an ImageView
                            childImg.setImageBitmap(bitmap);
                            common.stopLoading();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors that occurred during the download
                            Toast.makeText(ChildDetails.this, "Image download Fail.", Toast.LENGTH_SHORT).show();
                            common.stopLoading();
                        }
                    });
                }else
                    common.stopLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                common.stopLoading();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        common.startLoading(this, "Loading");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the image URI
            Uri imageUri = data.getData();

            // Create a reference to the location where you want to store the image in Firebase Storage
            StorageReference imageRef = storageRef.child("images/"+common.rDBEmail+"/"+common.childEmail+".jpg");

            // Upload the image to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image upload successful
                            // You can retrieve the download URL of the uploaded image
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    // Do something with the download URL (e.g., save it to Firebase Realtime Database)
                                    String imageUrl = downloadUrl.toString();
                                    String link = "images/"+common.rDBEmail+"/"+common.childEmail+".jpg";
                                    // Save the imageUrl to Firebase Realtime Database or perform any other operations
                                    Data.child("user/"+common.rDBEmail+"/ChildList/"+common.childEmail+"/img").setValue(link);
                                    common.stopLoading();
                                    Toast.makeText(ChildDetails.this, "Set Successful", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    common.stopLoading();
                                    // Error getting download URL
                                    // Handle the error
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            common.stopLoading();
                            // Image upload failed
                            // Handle the error
                        }
                    });
        }else
            common.stopLoading();
    }
}