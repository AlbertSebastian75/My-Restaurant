package com.example.albert;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    TextView btn, forgotPassword;
    private EditText inputEmail, inputPassword;
    Button btnlogin, btnGoogle;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog progressDialog;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    AlertDialog.Builder reset_alert;
    LayoutInflater inflater;

    String UserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn=findViewById(R.id.textViewSignUp);
        inputEmail=findViewById(R.id.inputEmail);
        inputPassword=findViewById(R.id.inputPassword);
        btnlogin=findViewById(R.id.btnlogin);
        btnGoogle=findViewById(R.id.btnGoogle);
        forgotPassword=findViewById(R.id.forgotPassword);

        progressDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        reset_alert=new AlertDialog.Builder(this);
        inflater=this.getLayoutInflater();

        //for google sign in
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // for login button click
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCredentials();
            }
        });

        // for redirection if account already exist
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        //for forgot password
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = inflater.inflate(R.layout.reset_pop, null);
                reset_alert.setTitle("Reset Forgot password").setMessage("Enter your Email to get Password Reset link.").setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText reset_email_pop=view1.findViewById(R.id.reset_email_pop);
                        if(reset_email_pop.getText().toString().isEmpty()){
                            reset_email_pop.setError("Required Field");
                            return;
                        }   
                        mAuth.sendPasswordResetEmail(reset_email_pop.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LoginActivity.this,"Reset Email Sent",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("Cancel",null).setView(view1).create().show();
            }
        });
        setUpOnBackPressed();
    }

    private void setUpOnBackPressed() {
        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
//                if(isEnabled()){
//                    setEnabled(false);
//                    Intent intent = new Intent(this, LoginActivity.class);
//                    startActivity(intent);
//                }
            }
        });
    }

    private void checkCredentials() {
        // Validation Checker
        String email=inputEmail.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String password=inputPassword.getText().toString();
        UserEmail=encodeUserEmail(email);

        if(email.isEmpty() || !(email.matches(emailPattern))){
            showError(inputEmail, "Email is not valid");
        }
        else if(password.isEmpty()){
            showError(inputPassword, "The password must not be empty");
        }
        else{
            progressDialog.setMessage("Please wait while Login...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("users").child(UserEmail).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child("Usertype").getValue(String.class).equals("Customer")){
                                    //Customer Login
                                    sendUserToNextActivity();
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                }
                                if((snapshot.child("Usertype").getValue(String.class).equals("Admin"))||((snapshot.child("Status").getValue(String.class).equals("Manager")) && (snapshot.child("Usertype").getValue(String.class).equals("Staff")))) {
                                    //Admin Login and Manager Login
                                    sendAdminToNextActivity(email);
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                }
                                if((snapshot.child("Usertype").getValue(String.class).equals("Staff")) && (snapshot.child("Status").getValue(String.class).equals("0"))){
                                    Toast.makeText(LoginActivity.this, "Please, Wait for Admin Approval", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    return;
                                }
                                if((snapshot.child("Usertype").getValue(String.class).equals("Staff")) && (snapshot.child("Status").getValue(String.class).equals("Parking Staff"))) {
                                    //Admin Login
                                    sendParkToNextActivity(email);
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                }
                                if((snapshot.child("Usertype").getValue(String.class).equals("Staff")) && (snapshot.child("Status").getValue(String.class).equals("Waiter"))) {
                                    //Admin Login
                                    sendWaiterToNextActivity(email);
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendParkToNextActivity(String email) {
        Intent intent=new Intent(LoginActivity.this,ParkActivity.class);
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.albert", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("name", email).apply();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendWaiterToNextActivity(String email) {
        Intent intent=new Intent(LoginActivity.this,WaiterActivity.class);
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.albert", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("name", email).apply();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendUserToNextActivity() {
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void sendAdminToNextActivity(String email) {
        Intent intent=new Intent(LoginActivity.this,AdminActivity.class);
        intent.putExtra("keyEmail",email);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent,1000);
    }

    //for google sign
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        progressDialog.setMessage("Please wait while Login...");
        progressDialog.setTitle("Login");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                sendUserToNextActivity();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
}
