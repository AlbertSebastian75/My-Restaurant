package com.example.albert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    TextView btn;
    private EditText inputUsername, inputPhone, inputEmail, inputPassword, inputConformPassword;
    Button btnRegister;
    RadioGroup radioGroup;
    RadioButton radioButton;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog progressDialog;
    DatabaseReference DataRef;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btn=findViewById(R.id.alreadyHaveAccount);
        inputUsername=findViewById(R.id.inputUsername);
        inputPhone=findViewById(R.id.inputPhone);
        inputEmail=findViewById(R.id.inputEmail);
        inputPassword=findViewById(R.id.inputPassword);
        inputConformPassword=findViewById(R.id.inputConformPassword);
        radioGroup=findViewById(R.id.radioGroup);
        btnRegister=findViewById(R.id.btnRegister);

        progressDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        DataRef= FirebaseDatabase.getInstance().getReference();

        // for register button click
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First Validation check
                checkCrededentials();
            }
        });

        // for redirection if account already exist
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    private void checkCrededentials() {
        // Validation Checker
        //Get data into string variables
        String username=inputUsername.getText().toString().toLowerCase(Locale.ROOT);
        String phone=inputPhone.getText().toString();
        String email=inputEmail.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String password=inputPassword.getText().toString();
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$");
        String conformpassword=inputConformPassword.getText().toString();
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        String radioBtn=radioButton.getText().toString();

        if(username.isEmpty() || username.matches(".*[^A-Z^a-z ].*") || username.length()<3){
            showError(inputUsername,"Your username is not valid!");
        }
        else if(phone.isEmpty() || phone.length()!=10){
            showError(inputPhone, "Number is not valid");
        }
        else if(email.isEmpty() || !(email.matches(emailPattern))){
            showError(inputEmail, "Email is not valid");
        }
        else if(password.isEmpty() || !passwordPattern.matcher(password).matches() || password.length()<8){
            showError(inputPassword, "The password must be at least 8 characters long and include a number, lowercase letter, uppercase letter and special character");
        }
        else if(conformpassword.isEmpty() || !conformpassword.equals(password)){
            showError(inputConformPassword, "Password not match!");
        }
        else{
            progressDialog.setMessage("Please wait while Registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        //send verification link
                        FirebaseUser user=mAuth.getCurrentUser();
                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(RegisterActivity.this, "Verification email has been sent", Toast.LENGTH_SHORT).show();;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                            }
                        });

                        //Adding username and phone to the real time database
                        String userEmail = encodeUserEmail(email);
                        DataRef.child("users").child(userEmail).child("Username").setValue(username);
                        DataRef.child("users").child(userEmail).child("Mobile").setValue(phone);
                        DataRef.child("users").child(userEmail).child("Password").setValue(password);
                        DataRef.child("users").child(userEmail).child("Usertype").setValue(radioBtn);
                        if (radioBtn.equals("Staff")){
                            DataRef.child("users").child(userEmail).child("Status").setValue("0");
                        }

                        //After completion, dialog box must be dismissed
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }); 
        }
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    private void showError(EditText input, String s) {
        // Print error message
        input.setError(s);
        input.requestFocus();
    }
}