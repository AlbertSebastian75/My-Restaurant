package com.example.albert.ui.bookStaff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albert.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewKartAdapter extends RecyclerView.Adapter<ViewKartAdapter.MyViewHolder>{

    Context context;
    ArrayList<Kart> arrayList;
    ArrayList<String> keyList;
    ArrayList<String> checkProduct = new ArrayList<>();

    public ViewKartAdapter(Context context, ArrayList<Kart> arrayList, ArrayList<String> keyList) {
        this.context = context;
        this.arrayList = arrayList;
        this.keyList = keyList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_fragment_kart_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Kart kart=arrayList.get(position);
        String products1=keyList.get(position);

        //Get the Customer name and product Id
        //holder.Username.setText(customerFragmentKartGet.getCustomer());
        //holder.ProductId.setText(customerFragmentKartGet.getProduct());

        //View product details: having the product key form kart table
        DatabaseReference DataRef;
        DataRef = FirebaseDatabase.getInstance().getReference().child("Product").child(kart.getProduct());
        DataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Products product = snapshot.getValue(Products.class);
                holder.Productname.setText(product.getProductname());
                holder.Category.setText(product.getCategory());
                holder.Amount.setText(product.getAmount());
                holder.Qty.setText(kart.getQuantity());
                holder.Stock.setText(product.getStock());
                Picasso.get().load(product.getImageUrl()).into(holder.ImageUrl);

                holder.checkKart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean checked = ((CheckBox) view).isChecked();
                        if(checked) {
                            checkProduct.add(products1);
                        }
                        else{
                            for(int i=0; i<checkProduct.size();i++){
                                if(products1.equals(checkProduct.get(i))){
                                    checkProduct.remove(products1);
                                }
                            }
                        }
                    }
                });

                //Saving Quantity
                holder.saveQty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String Quantity = holder.Qty.getText().toString();
                        if (Integer.parseInt(product.getStock())<Integer.parseInt(Quantity)){
                            Toast.makeText(view.getContext(), "The Quantity must not be greater than stock!", Toast.LENGTH_SHORT).show();
                            holder.Qty.setText(kart.getQuantity());
                            return;
                        }
                        if (Integer.parseInt(Quantity)==0){
                            Toast.makeText(view.getContext(), "The Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                            holder.Qty.setText(kart.getQuantity());
                            return;
                        }
                        editQty(products1, Quantity);
                        notifyDataSetChanged();
                        arrayList.clear();
                        keyList.clear();
                    }
                });

//                holder.removeKart.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.Productname.getContext());
//                        builder.setTitle("Are you sure?");
//                        builder.setMessage("The Product \""+product.getProductname()+"\" will removed from the Cart");
//
//                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                FirebaseDatabase.getInstance().getReference().child("Kart").child(products1).removeValue();
//                                Toast.makeText(holder.Productname.getContext(), "The Product \""+product.getProductname()+"\" is successfully removed from th Cart ", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Toast.makeText(holder.Productname.getContext(), "The Product \""+product.getProductname()+"\" removal from th Cart Canceled ", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        builder.show();
//                    }
//                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //The key
        String users1 = keyList.get(position);
        String userEmail = decodeUserEmail(users1);
    }

    private void editQty(String products1, String Qty) {
        DatabaseReference DataRef1;
        DataRef1 = FirebaseDatabase.getInstance().getReference();
        DataRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataRef1.child("Kart").child(products1).child("Quantity").setValue(Qty);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Username, Productname, Category, Amount, Stock;
        EditText Qty;
        CheckBox checkKart;
        Button removeKart, saveQty;
        ImageView ImageUrl;


        public MyViewHolder(@NonNull View userView) {
            super(userView);

            Username=userView.findViewById(R.id.Username);
            Productname=userView.findViewById(R.id.Productname);
            Category=userView.findViewById(R.id.Category);
            Amount=userView.findViewById(R.id.Amount);
            Stock=userView.findViewById(R.id.Stock);

            Qty=userView.findViewById(R.id.Qty);
            saveQty=userView.findViewById(R.id.saveQty);
            ImageUrl=userView.findViewById(R.id.ImageUrl);

            checkKart=userView.findViewById(R.id.checkKart);
        }
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

    public ArrayList<String> getSelectedKart(){
        return checkProduct;
    }

}