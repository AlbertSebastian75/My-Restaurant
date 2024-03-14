package com.example.albert.ui.viewusers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albert.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ParkingAvailableListenerAdapter extends RecyclerView.Adapter<ParkingAvailableListenerAdapter.MyViewHolder>{

    Context context;
    ArrayList<Slot> arrayList;
    ArrayList<String> keylist;
    DatabaseReference databaseReference, DataRef;
    SharedPreferences sharedPreferences;
    ArrayList<String> checkList;
    String txtDate, tmBox, bookKey;

    public ParkingAvailableListenerAdapter(Context context, ArrayList<Slot> arrayList, ArrayList<String> keylist, String txtDate, String tmBox, String bookKey) {
        this.context = context;
        this.arrayList = arrayList;
        this.keylist = keylist;
        this.checkList= checkList;
        this.txtDate = txtDate;
        this.tmBox = tmBox;
        this.bookKey = bookKey;
    }

    @NonNull
    @Override
    public ParkingAvailableListenerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_avail_slot,parent,false);
        return new ParkingAvailableListenerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingAvailableListenerAdapter.MyViewHolder holder, int position) {
        Slot slot=arrayList.get(position);
        String slot1=keylist.get(position);

        holder.SlotName.setText(slot.getSlotName());

        //get email: user
        sharedPreferences = this.context.getSharedPreferences("com.example.albert", Context.MODE_PRIVATE);
        String name =  sharedPreferences.getString("name", "not found");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        DataRef= databaseReference.child("Park");

        holder.bookPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.SlotName.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Hai "+name+"\n"+"Conforming the Booking of \""+slot.getSlotName()+"\"");

                builder.setPositiveButton("Book", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Setting key
                        String key = DataRef.push().getKey();
                        DataRef.child(key).setValue(key);

                        //Get kart item, Date and time
                        DataRef.child(key).child("Date").setValue(txtDate);
                        DataRef.child(key).child("Time").setValue(tmBox);
                        DataRef.child(key).child("Status").setValue("placed");

                        DataRef.child(key).child("Username").setValue(name);
                        DataRef.child(key).child("Park").setValue(slot1);

                        databaseReference.child("Book").child(bookKey).child("Park").setValue(key);
                        moveBack();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.SlotName.getContext(), "Park Booking Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private void moveBack() {
        Intent intent = new Intent(context, ViewUsersFragment.class);
        Toast.makeText(context, "Booking Successful", Toast.LENGTH_SHORT).show();
        context.startActivity(intent);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView SlotName;

        Button bookPark;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            SlotName=itemView.findViewById(R.id.SlotName);
            bookPark=itemView.findViewById(R.id.bookPark);
        }
    }
}
