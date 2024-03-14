package com.example.albert.ui.viewusers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

public class UserDetailsFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    UserDetailsAdapter userDetailsAdapter;
    ArrayList<Orders> arrayList;
    ArrayList<String> keylist;
    String key, email;
    private SwipeRefreshLayout swipeRefreshLayout;
    Button viewHistory, viewCancelled;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
        View root = inflater.inflate(R.layout.fragment_user_details, container, false);
        String categoryName = getArguments().getString("category");
        email = decodeUserEmail(categoryName);

        recyclerView=root.findViewById(R.id.viewOrders);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Book");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        arrayList = new ArrayList<>();
        keylist = new ArrayList<>();
        userDetailsAdapter = new UserDetailsAdapter(getActivity(), arrayList, keylist);
        recyclerView.setAdapter(userDetailsAdapter);

        viewHistory = root.findViewById(R.id.viewHistory);
        viewCancelled = root.findViewById(R.id.viewCancelled);

        //Refreshing
        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().recreate();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        databaseReference.orderByChild("Check").equalTo(email+"_placed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Orders products = dataSnapshot.getValue(Orders.class);
                    key = dataSnapshot.getKey();
                    arrayList.add(products);
                    keylist.add(key);

                }
                userDetailsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHistoryFragment viewHistoryFragment = new viewHistoryFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putString("email", email);
                viewHistoryFragment.setArguments(args);
                fragmentTransaction.replace(R.id.container, viewHistoryFragment).commit();
            }
        });

        viewCancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCancelledFragment viewCancelledFragment = new viewCancelledFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putString("email", email);
                viewCancelledFragment.setArguments(args);
                fragmentTransaction.replace(R.id.container, viewCancelledFragment).commit();
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
                    ViewUsersFragment viewUsersFragment = new ViewUsersFragment();
                    Bundle args = new Bundle();
                    viewUsersFragment.setArguments(args);
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, viewUsersFragment).commit();
                }
            }
        });
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

}