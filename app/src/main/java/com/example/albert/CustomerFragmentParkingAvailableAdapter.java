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

public class CustomerFragmentParkingAvailableAdapter extends RecyclerView.Adapter<CustomerFragmentParkingAvailableAdapter.MyViewHolder>{

    Context context;
    ArrayList<String> arrayList;
    private final CustomerFragmentParkingInterface customerFragmentParkingInterface;
    DatabaseReference databaseReference;

    public CustomerFragmentParkingAvailableAdapter(Context context, ArrayList<String> arrayList, CustomerFragmentParkingInterface customerFragmentParkingInterface) {
        this.context = context;
        this.arrayList = arrayList;
        this.customerFragmentParkingInterface = customerFragmentParkingInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_parking,parent,false);
        return new MyViewHolder(view, customerFragmentParkingInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String products=arrayList.get(position);

        //get the total number of products.
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Slot");
        databaseReference.orderByChild("Category").equalTo(products).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CustomerFragmentParkingGet p = dataSnapshot.getValue(CustomerFragmentParkingGet.class);
                    if(!p.getStatus().equals("0")) {
                        count++;
                    }
                }
                holder.Count.setText("Slots: " + String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.AreaListenerName.setText(products);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView AreaListenerName, Count;

        public MyViewHolder(@NonNull View categoryView, CustomerFragmentParkingInterface customerFragmentParkingInterface) {
            super(categoryView);
            AreaListenerName=categoryView.findViewById(R.id.AreaListenerName);
            Count=categoryView.findViewById(R.id.Count);

            categoryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    customerFragmentParkingInterface.onCategorySelected(pos);
                }
            });
        }
    }
}