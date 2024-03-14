package com.example.albert.ui.viewusers;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.albert.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ParkingAvailableListener extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference, DataRef;
    ParkingAvailableListenerAdapter slotAdapter;
    ArrayList<Slot> arrayList;
    ArrayList<String> keylist;
    String key, categoryName;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    String txtDate, tmBox, bookKey;
    boolean flag;
    TextView txtCat;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_fragment_parking_available_listener);
        txtDate = getIntent().getExtras().getString("txtDate");
        tmBox = getIntent().getExtras().getString("tmBox");
        bookKey = getIntent().getExtras().getString("bookKey");
        categoryName = getIntent().getExtras().getString("area");

        //View
        recyclerView=findViewById(R.id.viewSlotCategory);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Slot");
        DataRef = FirebaseDatabase.getInstance().getReference().child("Park");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        txtCat = findViewById(R.id.txtCat);
        txtCat.setText("Category: "+categoryName.toUpperCase());

        arrayList = new ArrayList<>();
        keylist = new ArrayList<>();
        slotAdapter = new ParkingAvailableListenerAdapter(this, arrayList, keylist, txtDate, tmBox, bookKey);
        recyclerView.setAdapter(slotAdapter);

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

                            DataRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    flag=false;
                                    for(DataSnapshot snapshot2 : snapshot1.getChildren()){
                                        String park = snapshot2.child("Park").getValue(String.class);
                                        String time = snapshot2.child("Time").getValue(String.class);
                                        String date = snapshot2.child("Date").getValue(String.class);
                                        if(((dataSnapshot.getKey()).equals(park)) && (txtDate.equals(date)) && (tmBox.equals(time))){
                                            flag=true;
                                        }
                                    }

                                    if(!flag) {
                                        Slot slot = dataSnapshot.getValue(Slot.class);
                                        key = dataSnapshot.getKey();
                                        arrayList.add(slot);
                                        keylist.add(key);
                                    }
                                    slotAdapter.notifyDataSetChanged();
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
                    Slot slot = dataSnapshot.getValue(Slot.class);

                    if(slot.getSlotName().contains(newText)) {
                        key = dataSnapshot.getKey();
                        arrayList.add(slot);
                        keylist.add(key);
                    }
                }
                slotAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
