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

public class CustomerActivityViewParkingAdapter extends RecyclerView.Adapter<CustomerActivityViewParkingAdapter.MyViewHolder>{
    Context context;
    ArrayList<CustomerFragmentParkingGet> arrayList;
    ArrayList<String> keylist;
    DatabaseReference databaseReference;

    public CustomerActivityViewParkingAdapter(Context context, ArrayList<CustomerFragmentParkingGet> arrayList, ArrayList<String> keylist) {
        this.context = context;
        this.arrayList = arrayList;
        this.keylist = keylist;
    }

    @NonNull
    @Override
    public CustomerActivityViewParkingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_park,parent,false);
        return new CustomerActivityViewParkingAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerActivityViewParkingAdapter.MyViewHolder holder, int position) {
        CustomerFragmentParkingGet parking=arrayList.get(position);
        String parking1=keylist.get(position);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //view product name
        String p1 = parking.getPark();
        databaseReference.child("Slot").child(p1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CustomerFragmentParkingSlotGet slot = snapshot.getValue(CustomerFragmentParkingSlotGet.class);
                holder.Park.setText(slot.getCategory()+", "+slot.getSlotName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.Date.setText(parking.getDate()+",");
        holder.Time.setText(parking.getTime());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Park, Date, Time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Time=itemView.findViewById(R.id.Time);
            Date=itemView.findViewById(R.id.Date);
            Park=itemView.findViewById(R.id.Park);
        }
    }
}
