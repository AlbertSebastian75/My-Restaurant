package com.example.albert.ui.categories;

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

public class CategoriesFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    MyAdapter myAdapter;
    ArrayList<String> arrayList;
    ArrayList<String> keylist;
    String key, value;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

    EditText productcategoryname;
    Button productcategorybtnadd;
    DatabaseReference DataRef;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_categories, container, false);

        //Add Category
        productcategoryname=root.findViewById(R.id.productcategoryname);
        productcategorybtnadd=root.findViewById(R.id.productcategorybtnadd);
        DataRef= FirebaseDatabase.getInstance().getReference().child("ProductCategory");

        //View Category
        recyclerView = root.findViewById(R.id.viewCategories);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ProductCategory");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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

        //Add
        productcategorybtnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryname=productcategoryname.getText().toString();
                categoryname = categoryname.toLowerCase();

                if(categoryname.isEmpty()){
                    showError(productcategoryname,"Category can't be empty!");
                }
                else if(categoryname.matches(".*[^A-Z^a-z ].*")){
                    showError(productcategoryname,"Category is not valid!");
                }
                else {
                    String temp = categoryname;
                    DataRef.orderByValue().equalTo(categoryname).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                showError(productcategoryname,"Category Already Exist!");
                            }
                            else{
                                String key = DataRef.push().getKey();
                                DataRef.child(key).setValue(temp);
                                productcategoryname.setText("");
                                Toast.makeText(getActivity(), "Category \""+temp+"\" added successfully", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
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
//        ArrayList<String> filterList = new ArrayList<>();
//        for (String products : arrayList){
//            if(products.toLowerCase().contains(newText.toLowerCase())){
//                filterList.add(products);
//            }
//        }
//        if (filterList.isEmpty()){
//
//        }
//        else{
//            myAdapter.setFilteredList(filterList);
//        }
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