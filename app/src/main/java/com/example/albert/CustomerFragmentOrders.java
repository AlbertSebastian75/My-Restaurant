package com.example.albert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerFragmentOrders extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    CustomerFragmentOrdersAdapter customerFragmentOrdersAdapter;
    ArrayList<CustomerFragmentOrdersGet> arrayList;
    ArrayList<String> keylist;
    String key;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedPreferences;
    Button viewBookings, viewCancelled;

    public CustomerFragmentOrders() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.customer_fragment_orders, container, false);

        recyclerView=root.findViewById(R.id.viewOrders);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Book");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        arrayList = new ArrayList<>();
        keylist = new ArrayList<>();
        customerFragmentOrdersAdapter = new CustomerFragmentOrdersAdapter(getActivity(), arrayList, keylist);
        recyclerView.setAdapter(customerFragmentOrdersAdapter);

        viewBookings = root.findViewById(R.id.viewBookings);
        viewCancelled = root.findViewById(R.id.viewCancelled);

        sharedPreferences=this.getActivity().getSharedPreferences("com.example.albert", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "not found");

        //Refreshing
        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().recreate();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        databaseReference.orderByChild("Check").equalTo(name+"_placed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CustomerFragmentOrdersGet products = dataSnapshot.getValue(CustomerFragmentOrdersGet.class);
                        key = dataSnapshot.getKey();
                        arrayList.add(products);
                        keylist.add(key);

                }
                customerFragmentOrdersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomerFragmentMyOrder.class);
                getActivity().startActivity(intent);
            }
        });

        viewCancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomerFragmentMyOrderCancelled.class);
                getActivity().startActivity(intent);
            }
        });

//        setUpOnBackPressed();
        return root;
    }

//    private void setUpOnBackPressed() {
//        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                //empty
//            }
//        });
//    }

}
