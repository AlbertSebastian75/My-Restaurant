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

public class CustomerFragmentProductAdapter extends RecyclerView.Adapter<CustomerFragmentProductAdapter.MyViewHolder>{

    Context context;
    ArrayList<String> arrayList;
    private final CustomerFragmentProductCategoryListenerInterface selectionListener;
    DatabaseReference databaseReference;

//    public void setFilteredList(ArrayList<String> filterList){
//        this.arrayList = filterList;
//        notifyDataSetChanged();
//    }

    public CustomerFragmentProductAdapter(Context context, ArrayList<String> arrayList, CustomerFragmentProductCategoryListenerInterface selectionListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.selectionListener = selectionListener;
    }

    @NonNull
    @Override
    public CustomerFragmentProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_fragment_products_list,parent,false);
        return new CustomerFragmentProductAdapter.MyViewHolder(view, selectionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerFragmentProductAdapter.MyViewHolder holder, int position) {
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

        public MyViewHolder(@NonNull View categoryView, CustomerFragmentProductCategoryListenerInterface selectionListener) {
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
