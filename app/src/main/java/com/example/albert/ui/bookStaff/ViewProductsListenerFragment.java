package com.example.albert.ui.bookStaff;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ViewProductsListenerFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ViewProductsListenerAdapter viewProductsListenerAdapter;
    ArrayList<Products> arrayList;
    ArrayList<String> keylist;
    String key;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    Button kartBtn;
    String txtDate, tmBox, table, name;
    TextView txtCat;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
        View root = inflater.inflate(R.layout.fragment_category_listener_book, container, false);
        String categoryName = getArguments().getString("category");

        txtDate = getArguments().getString("txtDate");
        tmBox = getArguments().getString("tmBox");
        table = getArguments().getString("table");
        name = getArguments().getString("name");

        recyclerView=root.findViewById(R.id.viewProductCategory);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        arrayList = new ArrayList<>();
        keylist = new ArrayList<>();
        viewProductsListenerAdapter = new ViewProductsListenerAdapter(getActivity(), arrayList, keylist, name);
        recyclerView.setAdapter(viewProductsListenerAdapter);

        kartBtn = root.findViewById(R.id.kartBtn);
        txtCat = root.findViewById(R.id.txtCat);
        txtCat.setText("Category: "+categoryName.toUpperCase());

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

        databaseReference.child("Product").orderByChild("Check1").equalTo(categoryName+"_1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Products products = dataSnapshot.getValue(Products.class);
                        key = dataSnapshot.getKey();
                        arrayList.add(products);
                        keylist.add(key);
                    }
                viewProductsListenerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //view admin kart
        kartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Moving to another fragment
                ViewKartFragment viewKartFragment = new ViewKartFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putString("table", table);
                args.putString("txtDate", txtDate);
                args.putString("tmBox", tmBox);
                args.putString("name", name);
                viewKartFragment.setArguments(args);
                fragmentTransaction.replace(R.id.container, viewKartFragment).commit();
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
                    ViewProductsFragment viewProductsFragment = new ViewProductsFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();        Bundle args = new Bundle();
                    args.putString("table", table);
                    args.putString("txtDate", txtDate);
                    args.putString("tmBox", tmBox);
                    args.putString("name", name);
                    viewProductsFragment.setArguments(args);
                    fragmentTransaction.replace(R.id.container, viewProductsFragment).commit();
                }
            }
        });
    }

    private void filterList(String newText, String categoryName) {
        databaseReference.child("Product").orderByChild("Check1").equalTo(categoryName+"_1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                keylist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Products products = dataSnapshot.getValue(Products.class);

                        if (products.getProductname().contains(newText)) {
                            key = dataSnapshot.getKey();
                            arrayList.add(products);
                            keylist.add(key);
                    }
                }
                viewProductsListenerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
