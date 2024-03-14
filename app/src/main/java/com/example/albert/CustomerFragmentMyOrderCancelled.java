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

public class CustomerFragmentMyOrderCancelled extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    CustomerFragmentMyOrdersCancelledAdapter customerFragmentMyOrdersAdapter;
    ArrayList<CustomerFragmentOrdersGet> arrayList;
    ArrayList<String> keylist;
    String key;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_fragment_my_order);

        recyclerView=findViewById(R.id.viewOrders);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Book");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<>();
        keylist = new ArrayList<>();
        customerFragmentMyOrdersAdapter = new CustomerFragmentMyOrdersCancelledAdapter(this, arrayList, keylist);
        recyclerView.setAdapter(customerFragmentMyOrdersAdapter);


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

        databaseReference.orderByChild("Check").equalTo(name+"_cancelled").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CustomerFragmentOrdersGet products = dataSnapshot.getValue(CustomerFragmentOrdersGet.class);
                    key = dataSnapshot.getKey();
                    arrayList.add(products);
                    keylist.add(key);

                }
                customerFragmentMyOrdersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
