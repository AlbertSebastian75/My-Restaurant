package com.example.albert;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerFragmentProduct extends Fragment implements CustomerFragmentProductCategoryListenerInterface{
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    CustomerFragmentProductAdapter customerFragmentProductAdapter;
    ArrayList<String> arrayList;
    String value;
    SearchView searchView;

    private SwipeRefreshLayout swipeRefreshLayout;

    public CustomerFragmentProduct() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.customer_fragment_product, container, false);

        recyclerView = root.findViewById(R.id.viewCategories);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ProductCategory");
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        arrayList = new ArrayList<>();
        customerFragmentProductAdapter = new CustomerFragmentProductAdapter(getActivity(), arrayList, this);
        recyclerView.setAdapter(customerFragmentProductAdapter);

        //Refreshing
        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //getActivity().recreate();
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
                customerFragmentProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setUpOnBackPressed();
        return root;
    }

    private void setUpOnBackPressed() {
        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setEnabled(false);
                CustomerFragmentProduct viewProductsFragment = new CustomerFragmentProduct();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, viewProductsFragment).commit();
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
                customerFragmentProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCategorySelected(int position) {
        Intent intent = new Intent(getActivity(), CustomerFragmentProductCategoryListener.class);
        intent.putExtra("category", arrayList.get(position));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
        //Moving to another fragment
//        CustomerFragmentProductCategoryListener customerFragmentProductCategoryListener = new CustomerFragmentProductCategoryListener();
//        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//        Bundle args = new Bundle();
//        args.putString("category", arrayList.get(position));
//        customerFragmentProductCategoryListener.setArguments(args);
//        fragmentTransaction.replace(R.id.container, customerFragmentProductCategoryListener).commit();
    }
}