package com.example.albert.ui.viewusers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
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

public class viewCancelledFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ViewCancelledAdapter viewCancelledAdapter;
    ArrayList<Orders> arrayList;
    ArrayList<String> keylist;
    String key, email;
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_view_history, container, false);
        email = getArguments().getString("email");

        recyclerView = root.findViewById(R.id.viewOrders);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Book");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        arrayList = new ArrayList<>();
        keylist = new ArrayList<>();
        viewCancelledAdapter = new ViewCancelledAdapter(getActivity(), arrayList, keylist);
        recyclerView.setAdapter(viewCancelledAdapter);

        //Refreshing
        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().recreate();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        databaseReference.orderByChild("Check").equalTo(email + "_cancelled").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Orders products = dataSnapshot.getValue(Orders.class);
                    key = dataSnapshot.getKey();
                    arrayList.add(products);
                    keylist.add(key);

                }
                viewCancelledAdapter.notifyDataSetChanged();
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
                if (isEnabled()) {
                    setEnabled(false);
                    ViewUsersFragment viewUsersFragment = new ViewUsersFragment();
                    Bundle args = new Bundle();
                    viewUsersFragment.setArguments(args);
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, viewUsersFragment).commit();
                }
            }
        });
    }
}