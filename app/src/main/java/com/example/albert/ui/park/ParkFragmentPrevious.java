package com.example.albert.ui.park;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.albert.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ParkFragmentPrevious extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    MyAdapter myAdapter;
    ArrayList<Park> arrayList;
    ArrayList<String> keyList=new ArrayList<>();
    String key;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    Button viewOrdersFuture, viewOrdersPrevious, viewOrdersPresent, cancelledProduct;

    //current date
    SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
    String date = sdf.format(System.currentTimeMillis());

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
        View root = inflater.inflate(R.layout.fragment_park, container, false);

        recyclerView=root.findViewById(R.id.viewParking);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Park");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        arrayList = new ArrayList<>();
        myAdapter = new MyAdapter(getActivity(), arrayList, keyList);
        recyclerView.setAdapter(myAdapter);

        viewOrdersFuture = root.findViewById(R.id.viewParkingFuture);
        viewOrdersPrevious = root.findViewById(R.id.viewParkingPrevious);
        viewOrdersPresent = root.findViewById(R.id.viewParkingPresent);
        cancelledProduct = root.findViewById(R.id.cancelledProduct);

        //Refreshing
        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().recreate();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        cancelledProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelledParkFragmentPrevious cancelledFragment = new CancelledParkFragmentPrevious();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, cancelledFragment).commit();
            }
        });

        //Searching Staff Requests
        searchView = root.findViewById(R.id.searchView);
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

        databaseReference.orderByChild("Status").equalTo("placed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Park park = dataSnapshot.getValue(Park.class);

                    Date date1 = null;
                    try {
                        date1 = sdf.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date date2 = null;
                    try {
                        date2 = sdf.parse(park.getDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(date1.compareTo(date2)>0) {
                        key = dataSnapshot.getKey();
                        arrayList.add(park);
                        keyList.add(key);
                    }
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewOrdersPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayList.clear();
                keyList.clear();
                databaseReference.orderByChild("Status").equalTo("placed").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Park park = dataSnapshot.getValue(Park.class);

                            Date date1 = null;
                            try {
                                date1 = sdf.parse(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date date2 = null;
                            try {
                                date2 = sdf.parse(park.getDate());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(date1.compareTo(date2)>0) {
                                key = dataSnapshot.getKey();
                                arrayList.add(park);
                                keyList.add(key);
                            }
                        }
                        myAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        viewOrdersPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParkFragment parkFragment= new ParkFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, parkFragment).commit();
            }
        });

        viewOrdersFuture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParkFragmentFuture parkFragmentFuture = new ParkFragmentFuture();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, parkFragmentFuture).commit();

            }
        });

        return root;
    }

    private void filterList(String newText) {
        databaseReference.orderByChild("Status").equalTo("placed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                keyList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Park park = dataSnapshot.getValue(Park.class);

                    Date date1 = null;
                    try {
                        date1 = sdf.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date date2 = null;
                    try {
                        date2 = sdf.parse(park.getDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if ((park.getUsername().contains(newText)) && (date1.compareTo(date2)>0)) {
                        key = dataSnapshot.getKey();
                        arrayList.add(park);
                        keyList.add(key);
                    }
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}