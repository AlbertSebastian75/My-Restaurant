package com.example.albert.ui.parkStaffBook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
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

import java.util.ArrayList;

public class AvailParkListenerFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference, DataRef;
    AvailParkAdapter parkAdapter;
    ArrayList<Slot> arrayList;
    ArrayList<String> keylist;
    String key;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    String txtDate, tmBox, bookKey;
    boolean flag;
    TextView txtCat;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
        View root = inflater.inflate(R.layout.fragment_avail_park_listener, container, false);
        String categoryName = getArguments().getString("block");
        txtDate = getArguments().getString("txtDate");
        tmBox = getArguments().getString("tmBox");
        bookKey = getArguments().getString("bookKey");

        //View
        recyclerView=root.findViewById(R.id.viewParkCategory);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Slot");
        DataRef = FirebaseDatabase.getInstance().getReference().child("Park");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        txtCat = root.findViewById(R.id.txtCat);
        txtCat.setText("Category: "+categoryName.toUpperCase());

        arrayList = new ArrayList<>();
        keylist = new ArrayList<>();
        parkAdapter = new AvailParkAdapter(getActivity(), arrayList, keylist, txtDate, tmBox, bookKey);
        recyclerView.setAdapter(parkAdapter);

        //Refreshing
        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                getActivity().recreate();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //Searching Products
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
                filterList(newText, categoryName);
                return true;
            }
        });

        databaseReference.orderByChild("Check1").equalTo(categoryName+"_1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //view park(slot) details
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                            //checking same park(slot) id in Book
                            DataRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    flag=false;
                                    for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                                        String park = snapshot2.child("Park").getValue(String.class);
                                        String time = snapshot2.child("Time").getValue(String.class);
                                        String date = snapshot2.child("Date").getValue(String.class);
                                        if(((dataSnapshot.getKey()).equals(park)) && (txtDate.equals(date)) && (tmBox.equals(time))){
                                            flag=true;
                                        }
                                    }
                                    if(!flag){
                                        Slot slot = dataSnapshot.getValue(Slot.class);
                                        key = dataSnapshot.getKey();
                                        keylist.add(key);
                                        arrayList.add(slot);
                                    }
                                    parkAdapter.notifyDataSetChanged();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error1) {
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //handle back press event
        setUpOnBackPressed();
        return root;
    }

    private void setUpOnBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(isEnabled()){
                    setEnabled(false);
                    AvailParkFragment parkFragment = new AvailParkFragment();
                    Bundle args = new Bundle();
                    args.putString("txtDate", txtDate);
                    args.putString("tmBox", tmBox);
                    args.putString("bookKey", bookKey);
                    parkFragment.setArguments(args);
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, parkFragment).commit();
                }
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

                            //checking same park(slot) id in Booking
                            DataRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    flag=false;
                                    for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                                        Park park = snapshot2.getValue(Park.class);
                                        if(((dataSnapshot.getKey()).equals(park.getPark())) && (txtDate.equals(park.getDate())) && (tmBox.equals(park.getTime()))){
                                            flag=true;
                                        }
                                    }
                                    if(!flag){
                                        Slot slot = dataSnapshot.getValue(Slot.class);
                                        if(slot.getSlotName().contains(newText)) {
                                            key = dataSnapshot.getKey();
                                            keylist.add(key);
                                            arrayList.add(slot);
                                        }
                                    }
                                    parkAdapter.notifyDataSetChanged();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error1) {
                                }
                            });

                        }
                    }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

