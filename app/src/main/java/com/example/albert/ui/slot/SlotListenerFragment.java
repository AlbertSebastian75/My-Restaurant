package com.example.albert.ui.slot;

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

public class SlotListenerFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    SlotAdapter slotAdapter;
    ArrayList<Slot> arrayList;
    ArrayList<String> keylist;
    String key;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

    EditText slotName;
    Button slotAddBtn;
    DatabaseReference DataRef;
    TextView txtCat;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
        View root = inflater.inflate(R.layout.fragment_slot_listener, container, false);
        String categoryName = getArguments().getString("area");

        //Add Category
        slotAddBtn=root.findViewById(R.id.slotAddBtn);
        slotName=root.findViewById(R.id.slotName);
        DataRef= FirebaseDatabase.getInstance().getReference().child("Slot");

        txtCat=root.findViewById(R.id.txtCat);
        txtCat.setText("Category: "+categoryName.toUpperCase());

        //View
        recyclerView=root.findViewById(R.id.viewSlotCategory);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Slot");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        arrayList = new ArrayList<>();
        keylist = new ArrayList<>();
        slotAdapter = new SlotAdapter(getActivity(), arrayList, keylist, categoryName);
        recyclerView.setAdapter(slotAdapter);

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

        //Display
        databaseReference.orderByChild("Check1").equalTo(categoryName+"_1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                keylist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Slot slot = dataSnapshot.getValue(Slot.class);
                    key = dataSnapshot.getKey();
                    arrayList.add(slot);
                    keylist.add(key);
                }
                slotAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Add
        slotAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String slot=slotName.getText().toString().toLowerCase(Locale.ROOT);

                if(slot.isEmpty()){
                    showError(slotName,"Slot can't be empty!");
                }
                else if(slot.matches(".*[^A-Z^a-z^0-9 ].*")) {
                    showError(slotName,"Slot is not valid!");
                }
                else {
                    String temp = slot;
                    DataRef.orderByChild("Check").equalTo(slot+"_"+categoryName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                showError(slotName, "Slot Already Exist!");
                            }
                            else{
                                String key = DataRef.push().getKey();
                                DataRef.child(key).child("slotName").setValue(temp);
                                DataRef.child(key).child("Check1").setValue(categoryName+"_1");
                                DataRef.child(key).child("Category").setValue(categoryName);
                                DataRef.child(key).child("Check").setValue(temp+"_"+categoryName);
                                DataRef.child(key).child("Status").setValue("1");
                                slotName.setText("");
                                arrayList.clear();
                                keylist.clear();
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
                    SlotFragment slotFragment = new SlotFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, slotFragment).commit();
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
                    Slot slot = dataSnapshot.getValue(Slot.class);

                    if(slot.getStatus().equals("1")) {
                        if (slot.getSlotName().contains(newText)) {
                            key = dataSnapshot.getKey();
                            arrayList.add(slot);
                            keylist.add(key);
                        }
                    }
                }
                slotAdapter.notifyDataSetChanged();
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
