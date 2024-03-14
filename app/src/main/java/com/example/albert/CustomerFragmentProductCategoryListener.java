package com.example.albert;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
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

public class CustomerFragmentProductCategoryListener extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    CustomerFragmentProductCategoryListenerAdapter categoryAdapter;
    ArrayList<CustomerFragmentProductGet> arrayList;
    ArrayList<String> keylist;
    String key;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    TextView txtCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_fragment_product_category_listener);

        String categoryName = getIntent().getExtras().getString("category");

        recyclerView=findViewById(R.id.viewProductCategory);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Product");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<>();
        keylist = new ArrayList<>();
        categoryAdapter = new CustomerFragmentProductCategoryListenerAdapter(this, arrayList, keylist);
        recyclerView.setAdapter(categoryAdapter);

        txtCat=findViewById(R.id.txtCat);
        txtCat.setText("Category: "+categoryName.toUpperCase());

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
                filterList(newText, categoryName);
                return true;
            }
        });

        databaseReference.orderByChild("Check1").equalTo(categoryName+"_1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CustomerFragmentProductGet products = dataSnapshot.getValue(CustomerFragmentProductGet.class);
                        key = dataSnapshot.getKey();
                        arrayList.add(products);
                        keylist.add(key);

                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setUpOnBackPressed();
    }

    private void filterList(String newText, String categoryName) {
        databaseReference.orderByChild("Check1").equalTo(categoryName+"_1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                keylist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CustomerFragmentProductGet products = dataSnapshot.getValue(CustomerFragmentProductGet.class);

                        if (products.getProductname().contains(newText)) {
                            key = dataSnapshot.getKey();
                            arrayList.add(products);
                            keylist.add(key);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setUpOnBackPressed() {
        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
//                    CustomerFragmentProduct myFragment = new CustomerFragmentProduct();
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.container, myFragment);
//                    fragmentTransaction.commit();

                Intent intent = new Intent(CustomerFragmentProductCategoryListener.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
