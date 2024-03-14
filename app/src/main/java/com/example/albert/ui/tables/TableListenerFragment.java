package com.example.albert.ui.tables;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

public class TableListenerFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    TableAdapter tableAdapter;
    ArrayList<Table> arrayList;
    ArrayList<String> keylist;
    String key;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

    EditText tableName, chair;
    Button tableAddBtn;
    DatabaseReference DataRef;
    TextView txtCat;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
        View root = inflater.inflate(R.layout.fragment_table_listener, container, false);
        String categoryName = getArguments().getString("block");

        //Add Category
        tableAddBtn=root.findViewById(R.id.tableAddBtn);
        tableName=root.findViewById(R.id.tableName);
        chair=root.findViewById(R.id.chair);
        DataRef= FirebaseDatabase.getInstance().getReference().child("Table");

        txtCat=root.findViewById(R.id.txtCat);
        txtCat.setText("Category: "+categoryName.toUpperCase());

        //View
        recyclerView=root.findViewById(R.id.viewTableCategory);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Table");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        arrayList = new ArrayList<>();
        keylist = new ArrayList<>();
        tableAdapter = new TableAdapter(getActivity(), arrayList, keylist, categoryName);
        recyclerView.setAdapter(tableAdapter);

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
                filterList(newText, categoryName);
                return true;
            }
        });

        //Add
        tableAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String t=tableName.getText().toString().toLowerCase(Locale.ROOT);
                String chairNo=chair.getText().toString();

                if(t.isEmpty()){
                    showError(tableName,"Table can't be empty!");
                }
                else if(t.matches(".*[^A-Z^a-z^0-9 ].*")) {
                    showError(tableName,"Table is not valid!");
                }
                else if(chairNo.isEmpty()){
                    showError(chair,"Chair can't be empty!");
                }
                else {
                    String temp = t;
                    DataRef.orderByChild("Check").equalTo(t+"_"+categoryName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                showError(tableName, "Slot Already Exist!");
                            }
                            else{
                                String key = DataRef.push().getKey();
                                DataRef.child(key).child("tableName").setValue(t);
                                DataRef.child(key).child("Chair").setValue(chairNo);
                                DataRef.child(key).child("Category").setValue(categoryName);
                                DataRef.child(key).child("Check1").setValue(categoryName+"_1");
                                DataRef.child(key).child("Status").setValue("1");
                                DataRef.child(key).child("Check").setValue(temp+"_"+categoryName);
                                tableName.setText("");
                                chair.setText("");
                                Toast.makeText(getActivity(), "Slot \""+temp+"\" added successfully", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        databaseReference.orderByChild("Check1").equalTo(categoryName+"_1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                keylist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Table table = dataSnapshot.getValue(Table.class);
                    key = dataSnapshot.getKey();
                    arrayList.add(table);
                    keylist.add(key);
                }

                tableAdapter.notifyDataSetChanged();
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
                    TableFragment tableFragment = new TableFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, tableFragment).commit();
                }
            }
        });
    }

    private void filterList(String newText, String categoryName) {
        databaseReference.orderByChild("Category").equalTo(categoryName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                keylist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Table table = dataSnapshot.getValue(Table.class);

                    if(table.getStatus().equals("1")) {
                        if (table.getTableName().contains(newText)) {
                            key = dataSnapshot.getKey();
                            arrayList.add(table);
                            keylist.add(key);
                        }
                    }
                }
                tableAdapter.notifyDataSetChanged();
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

