package com.example.albert.ui.park;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albert.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    Context context;
    ArrayList<Park> arrayList;
    ArrayList<String> keyList;
    DatabaseReference databaseReference;

    public MyAdapter(Context context, ArrayList<Park> arrayList, ArrayList<String> keyList) {
        this.context = context;
        this.arrayList = arrayList;
        this.keyList = keyList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_park_book,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Park park=arrayList.get(position);
        String products1=keyList.get(position);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //view park name
        String p1 = park.getPark();
        databaseReference.child("Slot").child(p1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Slot slot = snapshot.getValue(Slot.class);
                holder.Park.setText(slot.getCategory()+", "+slot.getSlotName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.Date.setText(park.getDate()+",");
        holder.Slot.setText(park.getTime());
        holder.Username.setText(park.getUsername());

        holder.cancelOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("The Parking will be cancel.");
                builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        database.child("Park").child(products1).child("Status").setValue("cancelled");
                        database.child("Park").child(products1).child("Park").setValue("0");

                        Query query = database.child("Book").orderByChild("Park").equalTo(products1);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    childSnapshot.child("Park").getRef().setValue("");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle the error
                            }
                        });

                        arrayList.clear();
                        keyList.clear();
                        Toast.makeText(view.getContext(), "Booking Cancelled Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(view.getContext(), "Parking Cancellation Failed", Toast.LENGTH_SHORT).show();
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

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Park, Date, Slot, Username;
        Button cancelOrders;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Park=itemView.findViewById(R.id.Park);
            Date=itemView.findViewById(R.id.Date);
            Slot=itemView.findViewById(R.id.Slot);
            Username=itemView.findViewById(R.id.Username);
            cancelOrders=itemView.findViewById(R.id.cancelOrders);
        }
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}