package com.example.albert.ui.park;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albert.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class CancelledAdapter extends RecyclerView.Adapter<CancelledAdapter.MyViewHolder>{

    Context context;
    ArrayList<Park> arrayList;
    ArrayList<String> keyList;
    DatabaseReference databaseReference;

    public CancelledAdapter(Context context, ArrayList<Park> arrayList, ArrayList<String> keyList) {
        this.context = context;
        this.arrayList = arrayList;
        this.keyList = keyList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_cancelled_park,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Park park=arrayList.get(position);
        String products1=keyList.get(position);

        holder.Date.setText(park.getDate()+",");
        holder.Slot.setText(park.getTime());
        holder.Username.setText(park.getUsername());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Date, Slot, Username;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Date = itemView.findViewById(R.id.Date);
            Slot = itemView.findViewById(R.id.Slot);
            Username = itemView.findViewById(R.id.Username);
        }
    }
    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}