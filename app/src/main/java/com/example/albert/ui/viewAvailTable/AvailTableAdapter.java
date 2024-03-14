package com.example.albert.ui.viewAvailTable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albert.R;

import java.util.ArrayList;

public class AvailTableAdapter extends RecyclerView.Adapter<AvailTableAdapter.MyViewHolder>{

    Context context;
    ArrayList<Table> arrayList;
    ArrayList<String> keylist;
    private final TableSelectionListener selectionListener;
    String txtDate, tmBox;

    public AvailTableAdapter(Context context, ArrayList<Table> arrayList, ArrayList<String> keylist, TableSelectionListener selectionListener, String txtDate, String tmBox) {
        this.context = context;
        this.arrayList = arrayList;
        this.keylist = keylist;
        this.selectionListener = selectionListener;
        this.txtDate = txtDate;
        this.tmBox = tmBox;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_avail_table,parent,false);
        return new MyViewHolder(view, selectionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Table table=arrayList.get(position);
        String table1=keylist.get(position);

        holder.TableName.setText(table.getTableName());
        holder.Chair.setText(table.getChair());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView TableName, Chair;

        public MyViewHolder(@NonNull View itemView, TableSelectionListener selectionListener) {
            super(itemView);

            TableName=itemView.findViewById(R.id.TableName);
            Chair=itemView.findViewById(R.id.Chair);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    selectionListener.onTableSelected(pos);
                }
            });

        }
    }
}