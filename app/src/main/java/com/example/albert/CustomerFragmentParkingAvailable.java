package com.example.albert;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerFragmentParkingAvailable extends AppCompatActivity implements CustomerFragmentParkingInterface {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    CustomerFragmentParkingAvailableAdapter myAdapter;
    ArrayList<String> arrayList;
    String value;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    String txtDate, tmBox, bookKey;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_fragment_parking_available);
        txtDate = getIntent().getExtras().getString("txtDate");
        tmBox = getIntent().getExtras().getString("tmBox");
        bookKey = getIntent().getExtras().getString("bookKey");

        recyclerView = findViewById(R.id.viewArea);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Area");
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        arrayList = new ArrayList<>();
        myAdapter = new CustomerFragmentParkingAvailableAdapter(this, arrayList, this);
        recyclerView.setAdapter(myAdapter);

        //Refreshing
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //getActivity().recreate();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //Searching Products
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                filterList(newText);
                return true;
            }
        });

        databaseReference.orderByValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(!dataSnapshot.getValue().toString().equals("0")) {
                        value = dataSnapshot.getValue().toString();
                        arrayList.add(value);
                    }
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filterList(String newText) {
        databaseReference.orderByValue().startAt(newText).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(!dataSnapshot.getValue().toString().equals("0")) {
                        value = dataSnapshot.getValue().toString();
                        arrayList.add(value);
                    }
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCategorySelected(int position) {
        //Moving to another Activity
        Intent intent = new Intent(this, CustomerFragmentParkingAvailableListener.class);
        intent.putExtra("txtDate", txtDate);
        intent.putExtra("tmBox", tmBox);
        intent.putExtra("bookKey", bookKey);
        intent.putExtra("area", arrayList.get(position));
        this.startActivity(intent);
    }
}
