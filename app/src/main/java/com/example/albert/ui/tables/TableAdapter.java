package com.example.albert.ui.tables;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albert.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.MyViewHolder>{

    Context context;
    ArrayList<Table> arrayList;
    ArrayList<String> keylist;
    String categoryName;

    public TableAdapter(Context context, ArrayList<Table> arrayList, ArrayList<String> keylist, String categoryName) {
        this.context = context;
        this.arrayList = arrayList;
        this.keylist = keylist;
        this.categoryName = categoryName;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_table,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Table table=arrayList.get(position);
        String table1=keylist.get(position);

        holder.TableName.setText(table.getTableName());
        holder.Chair.setText("Chair: "+table.getChair());

        holder.deleteTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.TableName.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Deleted Tables can't be undo!\n\""+table.getTableName()+"\" will be deleted permanently.");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Table").child(table1).child("Check1").setValue(categoryName+"_0");
                        FirebaseDatabase.getInstance().getReference().child("Table").child(table1).child("Status").setValue("0");
                        arrayList.clear();
                        keylist.clear();
                        Toast.makeText(view.getContext(), "Table \""+table.getTableName()+"\" Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(view.getContext(), "Table \""+table.getTableName()+"\" Deletion Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        holder.editTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(view.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_popup_table))
                        .setExpanded(true, 1200)
                        .create();

                View view1=dialogPlus.getHolderView();

                EditText ProductUpdateName = view1.findViewById(R.id.TableUpdateName);
                EditText ProductUpdateCategory = view1.findViewById(R.id.TableUpdateCategory);
                EditText ProductUpdateAmount = view1.findViewById(R.id.TableUpdateChair);
                Button ProductUpdateBtn = view1.findViewById(R.id.TableUpdateBtn);

                ProductUpdateName.setText(table.getTableName());
                ProductUpdateCategory.setText(table.getCategory());
                ProductUpdateAmount.setText(table.getChair());
                dialogPlus.show();

                ProductUpdateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("tableName", ProductUpdateName.getText().toString());
                        map.put("Category", ProductUpdateCategory.getText().toString());
                        map.put("Chair", ProductUpdateAmount.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("Table")
                                .child(table1).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.TableName.getContext(), "Product \""+table.getTableName()+"\" Updated Successfully", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(holder.TableName.getContext(), "Error while Updating", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                });
                        arrayList.clear();
                        keylist.clear();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView TableName, Chair;

        Button deleteTable, editTable;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            TableName=itemView.findViewById(R.id.TableName);
            Chair=itemView.findViewById(R.id.Chair);
            deleteTable=itemView.findViewById(R.id.deleteTable);
            editTable=itemView.findViewById(R.id.editTable);
        }
    }
}
