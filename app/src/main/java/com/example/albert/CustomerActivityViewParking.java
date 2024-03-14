package com.example.albert;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerActivityViewParking extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    CustomerActivityViewParkingAdapter customerActivityViewParkingAdapter;
    ArrayList<CustomerFragmentParkingGet> arrayList;
    ArrayList<String> keylist;
    String key;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_activity_view_parking);

        recyclerView=findViewById(R.id.viewParking);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Park");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<>();
        keylist = new ArrayList<>();
        customerActivityViewParkingAdapter = new CustomerActivityViewParkingAdapter(this, arrayList, keylist);
        recyclerView.setAdapter(customerActivityViewParkingAdapter);


        sharedPreferences=this.getSharedPreferences("com.example.albert", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "not found");

        //Refreshing
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(getIntent());
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        databaseReference.orderByChild("Username").equalTo(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CustomerFragmentParkingGet parking = dataSnapshot.getValue(CustomerFragmentParkingGet.class);
                    key = dataSnapshot.getKey();
                    arrayList.add(parking);
                    keylist.add(key);
                }
                customerActivityViewParkingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
