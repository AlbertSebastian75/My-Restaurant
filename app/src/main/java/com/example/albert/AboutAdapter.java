package com.example.albert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.MyViewHolder>{

    Context context;
    ArrayList<CustomerFragmentReviewsGet> arrayList;
    ArrayList<String> keylist;

    public AboutAdapter(Context context, ArrayList<CustomerFragmentReviewsGet> arrayList, ArrayList<String> keylist) {
        this.context = context;
        this.arrayList = arrayList;
        this.keylist = keylist;
    }

    @NonNull
    @Override
    public AboutAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_details,parent,false);
        return new AboutAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutAdapter.MyViewHolder holder, int position) {
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
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtReview, Username, UserLogo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtReview=itemView.findViewById(R.id.txtReview);
            Username=itemView.findViewById(R.id.Username);
            UserLogo=itemView.findViewById(R.id.UserLogo);
        }
    }
}
