package com.example.albert.ui.parkStaffBook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albert.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    Context context;
    ArrayList<String> arrayList;
    private final BlockSelectionListener selectionListener;
    DatabaseReference databaseReference;

    public MyAdapter(Context context, ArrayList<String> arrayList, BlockSelectionListener selectionListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.selectionListener = selectionListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_park_listener,parent,false);
        return new MyViewHolder(view, selectionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String products=arrayList.get(position);

        //get the total number of products.
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Slot");
        databaseReference.orderByChild("Check1").equalTo(products+"_1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count =  snapshot.getChildrenCount();
                holder.Count.setText("Parking: "+String.valueOf(count));
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

        public MyViewHolder(@NonNull View categoryView, BlockSelectionListener selectionListener) {
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