package com.example.albert.ui.viewAvailTable;

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

public class AvailTableListenerFragment extends Fragment implements TableSelectionListener{

    RecyclerView recyclerView;
    DatabaseReference databaseReference, DataRef;
    AvailTableAdapter tableAdapter;
    ArrayList<Table> arrayList;
    ArrayList<String> keylist;
    String key;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    String txtDate, tmBox, categoryName;
    boolean flag;
    TextView txtCat;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
        View root = inflater.inflate(R.layout.fragment_avail_table_listener, container, false);
        categoryName = getArguments().getString("block");
        txtDate = getArguments().getString("txtDate");
        tmBox = getArguments().getString("tmBox");

        //View
        recyclerView=root.findViewById(R.id.viewTableCategory);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Table");
        DataRef = FirebaseDatabase.getInstance().getReference().child("Book");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        arrayList = new ArrayList<>();
        keylist = new ArrayList<>();
        tableAdapter = new AvailTableAdapter(getActivity(), arrayList, keylist, this, txtDate, tmBox);
        recyclerView.setAdapter(tableAdapter);

        txtCat = root.findViewById(R.id.txtCat);
        txtCat.setText("Category: "+categoryName.toUpperCase());

        //Refreshing
        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
                //view table details
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                            //checking same table id in Book
                            DataRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    flag=false;
                                    for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                                        String table = snapshot2.child("Table").getValue(String.class);
                                        String time = snapshot2.child("Time").getValue(String.class);
                                        String date = snapshot2.child("Date").getValue(String.class);
                                        if(((dataSnapshot.getKey()).equals(table)) && (txtDate.equals(date)) && (tmBox.equals(time))){
                                            flag=true;
                                        }
                                    }
                                    if(!flag){
                                        Table table = dataSnapshot.getValue(Table.class);
                                        key = dataSnapshot.getKey();
                                        keylist.add(key);
                                        arrayList.add(table);
                                    }
                                    tableAdapter.notifyDataSetChanged();
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
                    AvailTableFragment tableFragment = new AvailTableFragment();
                    Bundle args = new Bundle();
                    args.putString("txtDate", txtDate);
                    args.putString("tmBox", tmBox);
                    tableFragment.setArguments(args);
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, tableFragment).commit();
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

                            //checking same table id in Book
                            DataRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    flag=false;
                                    for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                                        Book book = snapshot2.getValue(Book.class);
                                        if(((dataSnapshot.getKey()).equals(book.getTable())) && (txtDate.equals(book.getDate())) && (tmBox.equals(book.getTime()))){
                                            flag=true;
                                        }
                                    }
                                    if(!flag){
                                        Table table = dataSnapshot.getValue(Table.class);
                                        if(table.getTableName().contains(newText)) {
                                            key = dataSnapshot.getKey();
                                            keylist.add(key);
                                            arrayList.add(table);
                                        }
                                    }
                                    tableAdapter.notifyDataSetChanged();
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

    @Override
    public void onTableSelected(int position) {
        ViewProductsFragment viewProductsFragment = new ViewProductsFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("table", keylist.get(position));
        args.putString("txtDate", txtDate);
        args.putString("tmBox", tmBox);
        viewProductsFragment.setArguments(args);
        fragmentTransaction.replace(R.id.container, viewProductsFragment).commit();
    }
}

