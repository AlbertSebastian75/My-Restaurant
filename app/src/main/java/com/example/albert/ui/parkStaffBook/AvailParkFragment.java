package com.example.albert.ui.parkStaffBook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.albert.ParkActivity;
import com.example.albert.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AvailParkFragment extends Fragment implements BlockSelectionListener {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    MyAdapter myAdapter;
    ArrayList<String> arrayList;
    String value;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    String txtDate, tmBox, bookKey;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
        View root = inflater.inflate(R.layout.fragment_avail_park, container, false);
        txtDate = getArguments().getString("txtDate");
        tmBox = getArguments().getString("tmBox");
        bookKey = getArguments().getString("bookKey");

        recyclerView = root.findViewById(R.id.viewBlocks);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Area");
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        arrayList = new ArrayList<>();
        myAdapter = new MyAdapter(getActivity(), arrayList, this);
        recyclerView.setAdapter(myAdapter);

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

        setUpOnBackPressed();
        return root;
    }

    private void setUpOnBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(isEnabled()){
                    setEnabled(false);
                    Intent intent = new Intent(getContext(), ParkActivity.class);
                    getContext().startActivity(intent);
                }
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
        //Moving to another fragment
        AvailParkListenerFragment categoryListenerFragment = new AvailParkListenerFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("block", arrayList.get(position));
        args.putString("txtDate", txtDate);
        args.putString("tmBox", tmBox);
        args.putString("bookKey", bookKey);
        categoryListenerFragment.setArguments(args);
        fragmentTransaction.replace(R.id.container, categoryListenerFragment).commit();
    }
}
