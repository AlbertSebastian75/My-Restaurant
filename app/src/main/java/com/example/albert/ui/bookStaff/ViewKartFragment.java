package com.example.albert.ui.bookStaff;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewKartFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference, database;
    ViewKartAdapter viewKartAdapter;
    ArrayList<Kart> arrayList;
    ArrayList<String> keyList = new ArrayList<>();
    String key;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

    Button bookKart;
    ArrayList<String> checkProduct = new ArrayList<>();

    String txtDate, tmBox, table, name;

    public ViewKartFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        container.removeAllViews();
        View root =  inflater.inflate(R.layout.admin_fragment_kart, container, false);

        txtDate = getArguments().getString("txtDate");
        tmBox = getArguments().getString("tmBox");
        table = getArguments().getString("table");
        name = getArguments().getString("name");

        bookKart=root.findViewById(R.id.bookKart);
        recyclerView=root.findViewById(R.id.viewKart);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Kart");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        arrayList = new ArrayList<>();
        viewKartAdapter = new ViewKartAdapter(getActivity(), arrayList, keyList);
        recyclerView.setAdapter(viewKartAdapter);

        bookKart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkProduct = viewKartAdapter.getSelectedKart();

                if(checkProduct.isEmpty()){
                    Toast.makeText(getActivity(),"Please, Check in the product from Cart", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Order Confirmation\n");

                //Adding details
                LayoutInflater inflater;
                inflater=LayoutInflater.from(view.getContext());
                View view1 = inflater.inflate(R.layout.admin_book, null);

                builder.setPositiveButton("Book", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText admin_book_email=view1.findViewById(R.id.admin_book_email);

                        if(admin_book_email.getText().toString().isEmpty()){
                            Toast.makeText(view.getContext(), "Please, Fill the Username!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(!admin_book_email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
                            Toast.makeText(view.getContext(), "Invalid Email!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            //Setting key
                            DatabaseReference DataRef= FirebaseDatabase.getInstance().getReference().child("Book");
                            String key = DataRef.push().getKey();
                            DataRef.child(key).setValue(key);
                            DataRef.child(key).child("Username").setValue(admin_book_email.getText().toString());

                            getProductDetails(key, checkProduct);
                            DataRef.child(key).child("Status").setValue("placed");
                            DataRef.child(key).child("Date").setValue(txtDate);
                            DataRef.child(key).child("Time").setValue(tmBox);
                            DataRef.child(key).child("Table").setValue(table);
                            DataRef.child(key).child("Park").setValue("");
                            DataRef.child(key).child("Check").setValue(admin_book_email.getText().toString()+"_placed");

                            //delete form kart
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference parentRef = database.getReference("Kart");
                            Query query = parentRef.orderByChild("Customer").equalTo(name);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                            Toast.makeText(getContext(), "Ordered Successfully", Toast.LENGTH_SHORT).show();
                            }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Order Confirmation is Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setView(view1).create().show();

            }
        });

        //Refreshing
        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().recreate();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        databaseReference.orderByChild("Customer").equalTo(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                keyList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Kart kart = dataSnapshot.getValue(Kart.class);
                    key = dataSnapshot.getKey();
                    arrayList.add(kart);
                    keyList.add(key);
                }
                viewKartAdapter.notifyDataSetChanged();
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
                    ViewProductsFragment productsFragment = new ViewProductsFragment();
                    Bundle args = new Bundle();
                    args.putString("txtDate", txtDate);
                    args.putString("tmBox", tmBox);
                    args.putString("table", table);
                    args.putString("name", name);
                    productsFragment.setArguments(args);
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, productsFragment).commit();
                }
            }
        });
    }

    private void getProductDetails(String key, ArrayList<String> checkProduct) {

        //from checkList(Kart id): we need product id(checkList1)
        ArrayList<String> checkList1 = new ArrayList<>();
        ArrayList<String> checkList2 = new ArrayList<>();
        ArrayList<String> checkList3 = new ArrayList<>();

        DatabaseReference DataRef= FirebaseDatabase.getInstance().getReference().child("Book");
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        for(int k=0;k<checkProduct.size();k++){
            String productid = checkProduct.get(k);
            databaseRef.child("Kart").child(productid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Kart kart = snapshot.getValue(Kart.class);
                    checkList1.add(kart.getProduct());
                    DataRef.child(key).child("Products").setValue(String.valueOf(checkList1));

                    //Add Quantity
                    checkList2.add(kart.getQuantity());
                    DataRef.child(key).child("Quantity").setValue(String.valueOf(checkList2));

                    String temp = kart.getProduct();
                    //Get Amount also
                    databaseRef.child("Product").child(kart.getProduct()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            Products product1 = snapshot1.getValue(Products.class);
                            checkList3.add(product1.getAmount());
                            DataRef.child(key).child("Amount").setValue(String.valueOf(checkList3));

                            //reduce qty.
                            int s = Integer.parseInt(product1.getStock()) - Integer.parseInt(kart.getQuantity());
                            database = FirebaseDatabase.getInstance().getReference().child("Product");
                            database.child(temp).child("Stock").setValue(""+s);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error1) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}