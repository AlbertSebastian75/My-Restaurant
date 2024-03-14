package com.example.albert.ui.bookStaff;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albert.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewProductsListenerAdapter extends RecyclerView.Adapter<ViewProductsListenerAdapter.MyViewHolder>{

    Context context;
    ArrayList<Products> arrayList;
    ArrayList<String> keylist;
    String name;

    public ViewProductsListenerAdapter(Context context, ArrayList<Products> arrayList, ArrayList<String> keylist, String name) {
        this.context = context;
        this.arrayList = arrayList;
        this.keylist = keylist;
        this.name = name;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_admin_book,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Products products=arrayList.get(position);
        String products1=keylist.get(position);

        holder.Productname.setText(products.getProductname());
        holder.Amount.setText(products.getAmount());
        holder.Stock.setText(products.getStock());
        Picasso.get().load(products.getImageUrl()).into(holder.ImageUrl);
        int stock1 = Integer.parseInt(products.getStock());

        holder.AddKart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Kart");
                database.orderByChild("Check").equalTo(name+"_"+products1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(view.getContext(), "The Product \"" + products.getProductname() + "\" is in the Kart", Toast.LENGTH_SHORT).show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(holder.Productname.getContext());

                            //Adding qty
                            LayoutInflater inflater;
                            inflater = LayoutInflater.from(view.getContext());
                            View view1 = inflater.inflate(R.layout.reset_qty, null);

                            builder.setTitle("Are you sure?");
                            builder.setMessage("\"" + products.Productname + "\" will be Added to the Kart");

                            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    //Getting qty
                                    EditText reset_qty_pop = view1.findViewById(R.id.reset_qty_pop);
                                    if (reset_qty_pop.getText().toString().isEmpty()) {
                                        Toast.makeText(view.getContext(), "Quantity can't be empty!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    int stock2 = Integer.parseInt(reset_qty_pop.getText().toString());
                                    if (stock2 == 0) {
                                        Toast.makeText(view.getContext(), "The Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (stock1 < stock2) {
                                        Toast.makeText(view.getContext(), "The Quantity must not be greater than stock!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    DatabaseReference DataRef = FirebaseDatabase.getInstance().getReference().child("Kart");
                                    String key = DataRef.push().getKey();
                                    HashMap hashMap = new HashMap<>();
                                    hashMap.put("Product", products1);
                                    hashMap.put("Quantity", reset_qty_pop.getText().toString());
                                    hashMap.put("Customer", name);
                                    hashMap.put("Check", name+"_"+products1);

                                    DataRef.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(holder.Productname.getContext(), " Product \"" + products.getProductname() + "\" added to the Kart Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(holder.Productname.getContext(), " Product \"" + products.getProductname() + "\" adding to the Kart Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            });
                            builder.setView(view1).create().show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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

        TextView Productname, Amount, Stock;
        ImageView ImageUrl;
        Button AddKart;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Productname=itemView.findViewById(R.id.Productname);
            Amount=itemView.findViewById(R.id.Amount);
            Stock=itemView.findViewById(R.id.Stock);
            ImageUrl=itemView.findViewById(R.id.ImageUrl);
            AddKart=itemView.findViewById(R.id.AddKart);
        }
    }
}
