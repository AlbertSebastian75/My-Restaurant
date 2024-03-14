package com.example.albert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerActivityKartBlockAdapter extends RecyclerView.Adapter<CustomerActivityKartBlockAdapter.MyViewHolder>{

    Context context;
    ArrayList<String> arrayList;
    private final CustomerFragmentKartInterface selectionListener;
    DatabaseReference databaseReference;

    public CustomerActivityKartBlockAdapter(Context context, ArrayList<String> arrayList, CustomerFragmentKartInterface selectionListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.selectionListener = selectionListener;
    }

    @NonNull
    @Override
    public CustomerActivityKartBlockAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_block_listener,parent,false);
        return new CustomerActivityKartBlockAdapter.MyViewHolder(view, selectionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerActivityKartBlockAdapter.MyViewHolder holder, int position) {
        String products=arrayList.get(position);

        //get the total number of products.
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Table");
        databaseReference.orderByChild("Check1").equalTo(products+"_1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count =  snapshot.getChildrenCount();
                holder.Count.setText("Tables: "+String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.BlockListenerName.setText(products);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView BlockListenerName, Count;

        public MyViewHolder(@NonNull View categoryView, CustomerFragmentKartInterface selectionListener) {
            super(categoryView);
            BlockListenerName=categoryView.findViewById(R.id.BlockListenerName);
            Count=categoryView.findViewById(R.id.Count);

            categoryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    selectionListener.onCategorySelected(pos);
                }
            });
        }
    }
}