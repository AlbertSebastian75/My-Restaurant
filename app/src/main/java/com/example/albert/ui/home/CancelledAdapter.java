package com.example.albert.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albert.CustomerFragmentProductGet;
import com.example.albert.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CancelledAdapter extends RecyclerView.Adapter<CancelledAdapter.MyViewHolder>{

    Context context;
    ArrayList<Book> arrayList;
    ArrayList<String> keyList;
    DatabaseReference databaseReference;

    public CancelledAdapter(Context context, ArrayList<Book> arrayList, ArrayList<String> keyList) {
        this.context = context;
        this.arrayList = arrayList;
        this.keyList = keyList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_cancelled,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Book book=arrayList.get(position);
        String products1=keyList.get(position);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //view product name
        ArrayList<String> p12 = new ArrayList<>();
        String p1 = book.getProducts();
        p1 = p1.replace("[", "");
        p1 = p1.replace("]", "");
        p1 = p1.replace(" ", "");
        String[] p11 = p1.split(",");
        for(int j=0;j<p11.length;j++){
            databaseReference.child("Product").child(p11[j]).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CustomerFragmentProductGet product = snapshot.getValue(CustomerFragmentProductGet.class);
                    p12.add(product.getProductname());
                    String p13 =String.valueOf(p12);
                    p13 = p13.substring(1);
                    p13 =p13.replace("]", "");
                    holder.Products.setText(p13);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        //Get Quantity purchased
        String p3 = book.getQuantity();
        p3 = p3.replace("[", "");
        p3 = p3.replace("]", "");
        holder.Quantity.setText(p3);
        //Converting to integer(for calculation)
        p3 = p3.replace(" ", "");
        String[] p31 = p3.split(",");
        int p32[] = new int[p31.length];
        for(int j=0;j<p31.length;j++){
            //we have each product quantity
            p32[j]=Integer.parseInt(p31[j]);
        }

        //Get Amount
        String p4 = book.getAmount();
        p4 = p4.replace("[", "");
        p4 = p4.replace("]", "");
        holder.Amount.setText(p4);
        //Converting to integer(for calculation)
        p4 = p4.replace(" ", "");
        String[] p41 = p4.split(",");
        int p42[] = new int[p41.length];
        for(int j=0;j<p41.length;j++){
            //we have each product amount
            p42[j]=Integer.parseInt(p41[j]);
        }

        //Calculate total amount
        int p51 = 0;
        int p5[] = new int[p41.length];
        for(int k=0;k< p42.length;k++){
            p5[k] = p32[k] * p42[k];
            p51 = p51 + p5[k];
            holder.Total.setText(""+p51+"/-");
        }
        holder.Date.setText(book.getDate()+",");
        holder.Slot.setText(book.getTime());
        holder.Username.setText(book.getUsername());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Products, Amount, Date, Slot, Username, Quantity, Total;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Products=itemView.findViewById(R.id.Products);
            Date=itemView.findViewById(R.id.Date);
            Amount=itemView.findViewById(R.id.Amount);
            Slot=itemView.findViewById(R.id.Slot);
            Quantity=itemView.findViewById(R.id.Quantity);
            Username=itemView.findViewById(R.id.Username);
            Total=itemView.findViewById(R.id.Total);
        }
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}