package com.example.albert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.albert.databinding.ActivityAdminBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String UserEmail;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Automatically created on Navigation Drawer Activity
        //The View Binding process inherently connects the Java code to the layout file with the same name (activity_admin.xml)
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth= FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        //activity_admin.xml: includes app_bar_admin.xml & contain nav_view
        //app_bar_admin.xml: includes content_admin.xml(show active content of mobile_navigation) & contains app bar
        //nav_view: nav_header_admin.xml(navigation's header) & activity_main_drawer.xml(view navigation & call to mobile_navigation(through id): load corresponding xml, Java code(through id))
        setSupportActionBar(binding.appBarAdmin.toolbar);
        binding.appBarAdmin.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        //activity_admin.xml: the attributes are initialized used binding
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_park,
                R.id.nav_block, R.id.nav_table, R.id.nav_area, R.id.nav_slot,
                R.id.nav_viewAvailTable, R.id.nav_viewAvailPark,
                R.id.nav_categories, R.id.nav_addProducts, R.id.nav_viewProducts, R.id.nav_categoryListener,
                R.id.nav_viewUsers, R.id.nav_staffRequests, R.id.nav_staff)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_admin);
        //mAppBarConfiguration: mobile_navigation & navController: content_admin.xml(loads mobile_navigation)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        updateNavHeader();
    }

    // used for creating menu at top: About and logout.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    //(create menu tab) onCreateOptionsMenu --> onOptionsItemSelected (what to do...)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.viewRestaurantDetails:
                Intent intent = new Intent(this, AboutAdminActivity.class);
                startActivity(intent);
                return true;

            case R.id.logout_settings:
                SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.albert", Context.MODE_PRIVATE);
                String name =  sharedPreferences.getString("name", "not found");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Logout Confirmation");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("LogOut", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        Toast.makeText(getApplicationContext(), "Logout Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminActivity.this, LoginActivity.class));
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_admin);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public void updateNavHeader(){
        //Admins Navigation Header - View Profile information: nav_header_admin.xml
        //from nav_view in admin_activity.xml 's
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.name);
        TextView navUserEmail=headerView.findViewById(R.id.email);
        navUserEmail.setText(mUser.getEmail());

        UserEmail=navUserEmail.getText().toString();
        UserEmail=encodeUserEmail(UserEmail);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(UserEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get username from email
                String Username=snapshot.child("Username").getValue(String.class).toUpperCase();
                navUsername.setText(Username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
}