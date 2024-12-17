package com.chanu.childtask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;

public class SignIn extends AppCompatActivity {

    private GoogleSignInClient client;
    DatabaseReference Data;
    SharedVariable sharedVariable;
    TextInputLayout email, password;
    TextView forgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        FirebaseApp.initializeApp(this);
        Data = FirebaseDatabase.getInstance().getReference();

        sharedVariable = new SharedVariable(SignIn.this);

        email = findViewById(R.id.inEmail);
        password = findViewById(R.id.inPassword);
        forgotPass = findViewById(R.id.forgetPassword);

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, ForgotPassword.class));
            }
        });

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        client= GoogleSignIn.getClient(this, options);

        SignInButton googleSignInButton =findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }

    public void openRegistration(View view) {
        startActivity(new Intent(this, Registration.class));
    }


    public void login(View view) {
        common.startLoading(this, "Checking");
        String pass = password.getEditText().getText().toString();
        String stringEmail = email.getEditText().getText().toString();
        String dbEmail = stringEmail.replaceAll("[.$\\[\\]#/\\\\]", "");

        if (!pass.isEmpty() && !stringEmail.isEmpty()) {
            if (isNetworkConnected()) {

                Data.child("user").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(dbEmail).exists()) {
                            String hashPass = null;
                            try {
                                hashPass = common.hashString(pass);
                            } catch (NoSuchAlgorithmException e) {
                                throw new RuntimeException(e);
                            }
                            String finalHashPass = hashPass;

                            if (snapshot.child(dbEmail).child("LoginData").child("password").getValue().toString().equals(finalHashPass)) {
                                //Toast.makeText(SignIn.this, "Successful SignIn", Toast.LENGTH_SHORT).show();

                                common.rEmail = stringEmail;
                                common.rDBEmail = dbEmail;
                                common.isGoogleLogin = false;
                                common.isLogin = true;
                                boolean isParent = false;
                                if (snapshot.child(dbEmail).child("LoginData").child("userType").getValue().toString().equals("parent")) {
                                    isParent = true;
                                }
                                sharedVariable.setWhileLogin(dbEmail, isParent, true, false);

                                common.stopLoading();
                                startActivity(new Intent(SignIn.this, Home.class));
                            } else {
                                common.stopLoading();
                                common.showMessage(SignIn.this, "Try Again", "Email or Password is wrong");
                            }
                        } else {
                            common.stopLoading();
                            common.showMessage(SignIn.this, "Try Again", "Email or Password is wrong");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } else {
                // No internet connection available
                common.stopLoading();
                Toast.makeText(SignIn.this, "No internet connection available", Toast.LENGTH_LONG).show();
            }
        } else {
            common.stopLoading();
            Toast.makeText(SignIn.this, "Please fill all details.", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void signInWithGoogle() {
        common.willGoogleLogin = true;
        Intent i = client.getSignInIntent();
        startActivityForResult(i, 1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String email = currentUser.getEmail();
                            String name = currentUser.getDisplayName();
                            String dbEmail = email.replaceAll("[.$\\[\\]#/\\\\]", "");
                            if (isNetworkConnected()) {

                                Data.child("user").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (common.willGoogleLogin) {
                                            common.willGoogleLogin = false;
                                            boolean isParent = true;
                                            try {
                                                if (snapshot.child(dbEmail).child("LoginData").child("userType").getValue().toString().equals("child")) {
                                                    isParent = false;
                                                }
                                            } catch (Exception e) {
                                                isParent = true;
                                            }

                                            if (isParent) {
                                                common.rName = name;
                                                common.rEmail = email;
                                                common.rDBEmail = dbEmail;
                                                common.rType = "parent";
                                                if(!snapshot.child(common.rDBEmail).exists()) {
                                                    String regDate = common.getCDateTime();
                                                    Data.child("user").child(common.rDBEmail).child("LoginData").child("regDate").setValue(regDate);
                                                    Data.child("user").child(common.rDBEmail).child("Notification").child("1").setValue("Welcome To Child Manage");
                                                    Data.child("user").child(common.rDBEmail).child("LoginData").child("name").setValue(common.rName);
                                                    Data.child("user").child(common.rDBEmail).child("LoginData").child("email").setValue(common.rEmail);
                                                    Data.child("user").child(common.rDBEmail).child("LoginData").child("userType").setValue(common.rType);
                                                }
                                                Data.child("user").child(common.rDBEmail).child("LoginData").child("isGoogleLogin").setValue("1").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(SignIn.this, "Successful SignIn As a Parent", Toast.LENGTH_SHORT).show();

                                                        common.isGoogleLogin = true;
                                                        common.isLogin = true;

                                                        sharedVariable.setWhileLogin(dbEmail, true, true, true);
                                                        startActivity(new Intent(SignIn.this, Home.class));

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(SignIn.this, "Database Error", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            } else {
                                                common.showMessage(SignIn.this, "Child Account", "Please login using email and password because your child. Only parent can use this.");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else {
                                // No internet connection available
                                Toast.makeText(SignIn.this, "No internet connection available", Toast.LENGTH_LONG).show();
                            }
                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onBackPressed(){
        startActivity(new Intent(this, Registration.class));
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if(user!=null){
//            Intent intent = new Intent(this, Home.class);
//            startActivity(intent);
//        }
//    }
}