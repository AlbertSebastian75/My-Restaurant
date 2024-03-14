package com.example.albert.ui.block;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.albert.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class BlockFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    MyAdapter myAdapter;
    ArrayList<String> arrayList;
    ArrayList<String> keylist;
    String key, value;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

    EditText blockName;
    Button blockAddBtn;
    DatabaseReference DataRef;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_block, container, false);

        //Add
        blockName=root.findViewById(R.id.blockName);
        blockAddBtn=root.findViewById(R.id.blockAddBtn);
        DataRef= FirebaseDatabase.getInstance().getReference().child("Block");

        //View
        recyclerView = root.findViewById(R.id.viewBlock);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Block");
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        arrayList = new ArrayList<>();
        keylist = new ArrayList<>();
        myAdapter = new MyAdapter(getActivity(), arrayList, keylist);
        recyclerView.setAdapter(myAdapter);

        //Refreshing
        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().recreate();
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

        //Add new Block
        blockAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String block = blockName.getText().toString().toLowerCase(Locale.ROOT);

                if (block.isEmpty()) {
                    showError(blockName, "Block Name can't be empty!");
                } else if (block.matches(".*[^A-Z^a-z^0-9 ].*")) {
                    showError(blockName, "Block Name is not valid!");
                } else {
                    String temp = block;
                    DataRef.orderByValue().equalTo(block).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                showError(blockName, "Category Already Exist!");
                            } else {
                                String key = DataRef.push().getKey();
                                DataRef.child(key).setValue(temp);
                                blockName.setText("");
                                Toast.makeText(getActivity(), "Category \"" + temp + "\" added successfully", Toast.LENGTH_SHORT).show();
                                arrayList.clear();
                                keylist.clear();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        //Display
        databaseReference.orderByValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(!dataSnapshot.getValue().toString().equals("0")) {
                        value = dataSnapshot.getValue().toString();
                        key = dataSnapshot.getKey();
                        arrayList.add(value);
                        keylist.add(key);
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
        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //empty
            }
        });
    }

    private void filterList(String newText) {
        databaseReference.orderByValue().startAt(newText).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                keylist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(!dataSnapshot.getValue().toString().equals("0")) {
                        value = dataSnapshot.getValue().toString();
                        key = dataSnapshot.getKey();
                        arrayList.add(value);
                        keylist.add(key);
                    }
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }
}