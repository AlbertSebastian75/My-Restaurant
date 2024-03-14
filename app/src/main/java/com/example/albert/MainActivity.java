package com.example.albert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    TabLayout tab;
    ViewPager viewPager;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView email,verifyMsg;
    Button signOutBtn, resendCode;
    DatabaseReference DataRef;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tab = findViewById(R.id.tab);
        viewPager = findViewById(R.id.viewPager);

        email = findViewById(R.id.email);
        verifyMsg = findViewById(R.id.verifyMsg);
        resendCode = findViewById(R.id.resendCode);
        signOutBtn = findViewById(R.id.signout);
        DataRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        //if the email is not yet verified. run the code
        if (!user.isEmailVerified()) {
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);
            signOutBtn.setVisibility(View.VISIBLE);
            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag", "onFailure: Email not sent " + e.getMessage());
                        }
                    });
                }
            });

            //sign out code
            signOutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });
        }
        else {

            email.setVisibility(View.VISIBLE);
            signOutBtn.setVisibility(View.VISIBLE);
            //Get logged user details
            if (user != null) {
                String personEmail = user.getEmail();
                email.setText(personEmail);

                // Temporary Data save
                SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.albert", Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("name", personEmail).apply();
                //String name = sharedPreferences.getString("name", "not found"); -->to get details
            }

            //signed in using the google account
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personEmail = acct.getEmail();

                SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.albert", Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("name", personEmail).apply();

                //Adding the details to realtime user db
                String userEmail = encodeUserEmail(personEmail);
                DataRef.child("users").child(userEmail).child("Username").setValue(personName);
//                name.setText(personName);
                email.setText(personEmail);
            }
            //sign out code
            signOutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Logout Confirmation");
                    builder.setMessage("Are you sure?");

                    builder.setPositiveButton("LogOut", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            signOut();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(), "Logout Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                }
            });

            // Adapter handles the tabs and corresponding fragment calls
            CustomerViewPagerAdapter customerViewPagerAdapter = new CustomerViewPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(customerViewPagerAdapter);
            tab.setupWithViewPager(viewPager);

        }
    }

    //location (package) menu > user.xml (create menu tab)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
    }

    //(create menu tab) onCreateOptionsMenu --> onOptionsItemSelected (what to do...)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.viewRestaurantDetails:
                    Intent intent = new Intent(this, AboutActivity.class);
                    startActivity(intent);
                    return true;
            case R.id.viewRestaurantReviews:
                SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.albert", Context.MODE_PRIVATE);
                String name =  sharedPreferences.getString("name", "not found");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Write Us A Review");
                builder.setMessage("\nWe'd like to know what you think!");

                //Adding review
                LayoutInflater inflater;
                inflater=LayoutInflater.from(this);
                View view1 = inflater.inflate(R.layout.review, null);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText reset_qty_pop=view1.findViewById(R.id.reset_qty_pop);

                        if(reset_qty_pop.getText().toString().isEmpty()){
                            Toast.makeText(MainActivity.this, "The Field can't be empty!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        DatabaseReference DataRef = FirebaseDatabase.getInstance().getReference().child("Review");
                        String key=DataRef.push().getKey();
                        HashMap hashMap=new HashMap<>();
                        hashMap.put("Name", name);
                        hashMap.put("ReviewText", reset_qty_pop.getText().toString());

                        DataRef.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MainActivity.this," Review added Successfully",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Review Adding Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setView(view1).create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    private String encodeUserEmail(String userEmail) {
            return userEmail.replace(".", ",");
    }

    private void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                Toast.makeText(getApplicationContext(), "Logout Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
}
