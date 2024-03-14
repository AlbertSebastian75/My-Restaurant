package com.example.albert.ui.slot;

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
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.MyViewHolder>{

    Context context;
    ArrayList<Slot> arrayList;
    ArrayList<String> keylist;
    String categoryName;

    public SlotAdapter(Context context, ArrayList<Slot> arrayList, ArrayList<String> keylist, String categoryName) {
        this.context = context;
        this.arrayList = arrayList;
        this.keylist = keylist;
        this.categoryName = categoryName;
    }

    @NonNull
    @Override
    public SlotAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_slot,parent,false);
        return new SlotAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotAdapter.MyViewHolder holder, int position) {
        Slot slot=arrayList.get(position);
        String slot1=keylist.get(position);

        holder.SlotName.setText(slot.getSlotName());

        holder.deleteSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.SlotName.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Deleted Slots can't be undo!\n\""+slot.getSlotName()+"\" will be deleted permanently.");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Slot").child(slot1).child("Status").setValue("0");
                        FirebaseDatabase.getInstance().getReference().child("Slot").child(slot1).child("Check1").setValue(categoryName+"_0");
                        arrayList.clear();
                        keylist.clear();
                        Toast.makeText(view.getContext(), "Parking Slot \""+slot.getSlotName()+"\" Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(view.getContext(), "Parking Slot \""+slot.getSlotName()+"\" Deletion Cancelled", Toast.LENGTH_SHORT).show();
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

        TextView SlotName;
        Button deleteSlot;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            SlotName=itemView.findViewById(R.id.SlotName);
            deleteSlot=itemView.findViewById(R.id.deleteSlot);
        }
    }
}
