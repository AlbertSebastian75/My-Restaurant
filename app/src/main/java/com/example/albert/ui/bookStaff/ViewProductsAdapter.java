package com.example.albert.ui.bookStaff;

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

public class ViewProductsAdapter extends RecyclerView.Adapter<ViewProductsAdapter.MyViewHolder>{

    Context context;
    ArrayList<String> arrayList;
    private final CategorySelectionListener selectionListener;
    DatabaseReference databaseReference;

    public ViewProductsAdapter(Context context, ArrayList<String> arrayList, CategorySelectionListener selectionListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.selectionListener = selectionListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_products,parent,false);
        return new MyViewHolder(view, selectionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String products=arrayList.get(position);

        //get the total number of products.
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Product");
        databaseReference.orderByChild("Check1").equalTo(products+"_1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count =  snapshot.getChildrenCount();
                holder.Count.setText("Products: " + String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.CategoryName.setText(products);
    }



    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView CategoryName, Count;

        public MyViewHolder(@NonNull View categoryView, CategorySelectionListener selectionListener) {
            super(categoryView);
            CategoryName=categoryView.findViewById(R.id.CategoryName);
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