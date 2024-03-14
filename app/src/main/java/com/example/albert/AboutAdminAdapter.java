package com.example.albert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AboutAdminAdapter extends RecyclerView.Adapter<AboutAdminAdapter.MyViewHolder>{

    Context context;
    ArrayList<CustomerFragmentReviewsGet> arrayList;
    ArrayList<String> keylist;

    public AboutAdminAdapter(Context context, ArrayList<CustomerFragmentReviewsGet> arrayList, ArrayList<String> keylist) {
        this.context = context;
        this.arrayList = arrayList;
        this.keylist = keylist;
    }

    @NonNull
    @Override
    public AboutAdminAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_admin_details,parent,false);
        return new AboutAdminAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutAdminAdapter.MyViewHolder holder, int position) {
        CustomerFragmentReviewsGet products = arrayList.get(position);
        String products1 = keylist.get(position);
        holder.txtReview.setText(products.getReviewText());
        // Assuming that "holder" is an instance of the ViewHolder class that holds your views.

        holder.txtReview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Set visibility of the other text view to visible when pressed
                        holder.UserLogo.setVisibility(View.VISIBLE);
                        holder.Username.setVisibility(View.VISIBLE);
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // Set visibility of the other text view to gone when released
                        holder.Username.setVisibility(View.GONE);
                        holder.UserLogo.setVisibility(View.GONE);
                        return true;
                }
                return false;
            }
        });

        holder.Username.setText(products.getName());
        holder.deleteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Review Deletion Confirmation");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Review").child(products1).removeValue();
                        arrayList.clear();
                        keylist.clear();
                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "Logout Cancelled", Toast.LENGTH_SHORT).show();
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
        TextView txtReview, Username, UserLogo;
        Button deleteReview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtReview=itemView.findViewById(R.id.txtReview);
            Username=itemView.findViewById(R.id.Username);
            UserLogo=itemView.findViewById(R.id.UserLogo);
            deleteReview=itemView.findViewById(R.id.deleteReview);
        }
    }
}
