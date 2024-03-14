package com.example.albert;

import android.os.Bundle;
import android.widget.TextView;

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

public class CustomerActivityKartTable extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference, DataRef;
    CustomerActivityKartTableAdapter tableAdapter;
    ArrayList<CustomerActivityKartTableGet> arrayList;
    ArrayList<String> keylist;
    String key, categoryName;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

    String txtDate, tmBox;
    ArrayList<String> checkList;
    boolean flag;
    TextView txtCat;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_activity_kart_table);

        txtDate = getIntent().getExtras().getString("txtDate");
        tmBox = getIntent().getExtras().getString("tmBox");
        checkList = getIntent().getExtras().getStringArrayList("checklist");
        categoryName = getIntent().getExtras().getString("block");

        //View
        recyclerView = findViewById(R.id.viewTableCategory);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Table");
        DataRef = FirebaseDatabase.getInstance().getReference().child("Book");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<>();
        keylist = new ArrayList<>();
        tableAdapter = new CustomerActivityKartTableAdapter(this, arrayList, keylist, checkList, txtDate, tmBox);
        recyclerView.setAdapter(tableAdapter);

        txtCat = findViewById(R.id.txtCat);
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
                //view table details
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            //Same table id in Book
                            DataRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    flag=false;
                                    for(DataSnapshot snapshot2 : snapshot1.getChildren()){
                                        String table = snapshot2.child("Table").getValue(String.class);
                                        String time = snapshot2.child("Time").getValue(String.class);
                                        String date = snapshot2.child("Date").getValue(String.class);
                                        //"dataSnapshot.getKey()" is the table primary key and "table" is the table id in book table.
                                        if(((dataSnapshot.getKey()).equals(table)) && (txtDate.equals(date)) && (tmBox.equals(time))){
                                            //do not get details of table: only that are not available
                                            flag=true;
                                        }
                                    }

                                    //get details of table: only that are available
                                    if(!flag){
                                        CustomerActivityKartTableGet table = dataSnapshot.getValue(CustomerActivityKartTableGet.class);
                                        key = dataSnapshot.getKey();
                                        keylist.add(key);
                                        arrayList.add(table);
                                    }
                                    tableAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void filterList(String newText, String categoryName) {
        databaseReference.orderByChild("Check1").equalTo(categoryName+"_1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                keylist.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CustomerActivityKartTableGet table = dataSnapshot.getValue(CustomerActivityKartTableGet.class);

                    DataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            flag=false;
                            for(DataSnapshot snapshot2 : snapshot1.getChildren()){
                                String table = snapshot2.child("Table").getValue(String.class);
                                String time = snapshot2.child("Time").getValue(String.class);
                                String date = snapshot2.child("Date").getValue(String.class);
                                if(((dataSnapshot.getKey()).equals(table)) && (txtDate.equals(date)) && (tmBox.equals(time))){
                                    flag=true;
                                }
                            }
                            if(!flag && (table.getTableName().contains(newText))){
                                CustomerActivityKartTableGet table = dataSnapshot.getValue(CustomerActivityKartTableGet.class);
                                key = dataSnapshot.getKey();
                                keylist.add(key);
                                arrayList.add(table);
                            }
                            tableAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                tableAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}