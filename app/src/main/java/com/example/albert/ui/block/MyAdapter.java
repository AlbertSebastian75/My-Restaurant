package com.example.albert.ui.block;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albert.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    Context context;
    ArrayList<String> arrayList;
    ArrayList<String> keylist;

    public MyAdapter(Context context, ArrayList<String> arrayList, ArrayList<String> keylist) {
        this.context = context;
        this.arrayList = arrayList;
        this.keylist = keylist;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_block,parent,false);
        return new MyAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        String products=arrayList.get(position);
        String products1=keylist.get(position);

        holder.BlockName.setText(products);

        holder.deleteBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.BlockName.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Deleted Category can't be undo!\n\""+products+"\" will be deleted permanently.");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Block").child(products1).setValue("0");
                        Toast.makeText(view.getContext(), "Category \""+products+"\" Deleted Successfully", Toast.LENGTH_SHORT).show();
                        arrayList.clear();
                        keylist.clear();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(view.getContext(), "Category \""+products+"\" Deletion Cancelled", Toast.LENGTH_SHORT).show();
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

        TextView BlockName;
        Button deleteBlock;

        public MyViewHolder(@NonNull View categoryView) {
            super(categoryView);
            BlockName=categoryView.findViewById(R.id.BlockName);
            deleteBlock=categoryView.findViewById(R.id.deleteBlock);
        }
    }
}