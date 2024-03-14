package com.example.albert.ui.viewusers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albert.R;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    Context context;
    ArrayList<Users> arrayList;
    ArrayList<String> keyList;
    private final CategorySelectionListener selectionListener;

    public MyAdapter(Context context, ArrayList<Users> arrayList, ArrayList<String> keyList, CategorySelectionListener selectionListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.keyList = keyList;
        this.selectionListener = selectionListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_user,parent,false);
        return new MyViewHolder(view, selectionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Users users=arrayList.get(position);

        holder.Username.setText(users.getUsername());
        holder.Mobile.setText(users.getMobile());

        String users1 = keyList.get(position);
        String userEmail = decodeUserEmail(users1);
        holder.Email.setText(userEmail);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Username, Mobile, Email;

        public MyViewHolder(@NonNull View userView, CategorySelectionListener selectionListener) {
            super(userView);

            Username=userView.findViewById(R.id.Username);
            Mobile=userView.findViewById(R.id.Mobile);
            Email=userView.findViewById(R.id.Email);

            userView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    selectionListener.onCategorySelected(pos);
                }
            });
        }
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}