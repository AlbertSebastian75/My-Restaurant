package com.example.albert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomerFragmentKartAdapter extends RecyclerView.Adapter<CustomerFragmentKartAdapter.MyViewHolder>{

    Context context;
    ArrayList<CustomerFragmentKartGet> arrayList;
    ArrayList<String> keyList;
    ArrayList<String> checkList = new ArrayList<>();
    boolean flag=true;

    public CustomerFragmentKartAdapter(Context context, ArrayList<CustomerFragmentKartGet> arrayList, ArrayList<String> keyList) {
        this.context = context;
        this.arrayList = arrayList;
        this.keyList = keyList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_fragment_kart_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CustomerFragmentKartGet customerFragmentKartGet=arrayList.get(position);
        String products1=keyList.get(position);

        //Get the Customer name and product Id
        //holder.Username.setText(customerFragmentKartGet.getCustomer());
        //holder.ProductId.setText(customerFragmentKartGet.getProduct());

        //View product details: having the product key form kart table
        DatabaseReference DataRef;
        DataRef = FirebaseDatabase.getInstance().getReference().child("Product").child(customerFragmentKartGet.getProduct());
        DataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CustomerFragmentProductGet product = snapshot.getValue(CustomerFragmentProductGet.class);
                holder.Productname.setText(product.getProductname());
                holder.Category.setText(product.getCategory());
                holder.Amount.setText(product.getAmount());
                holder.Stock.setText(product.getStock());
                Picasso.get().load(product.getImageUrl()).into(holder.ImageUrl);
                holder.Qty.setText(customerFragmentKartGet.getQuantity());

                holder.checkKart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean checked = ((CheckBox) view).isChecked();
                        if(checked) {
                            checkList.add(products1);
                        }
                        else{
                            // work on unchecked situation
                            for(int i=0; i<checkList.size();i++){
                                if(products1.equals(checkList.get(i))){
                                    checkList.remove(products1);
                                }
                            }
                        }
                    }
                });

                String Quantity = holder.Qty.getText().toString();
                if (Integer.parseInt(product.getStock())<Integer.parseInt(Quantity)){
                    flag = false;
                }

                //Saving Quantity
                holder.saveQty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String Quantity = holder.Qty.getText().toString();
                        if (Integer.parseInt(product.getStock())<Integer.parseInt(Quantity)){
                            Toast.makeText(view.getContext(), "The Quantity must not be greater than stock!", Toast.LENGTH_SHORT).show();
                            // continue with the same kart qty...
                            holder.Qty.setText(customerFragmentKartGet.getQuantity());
                            return;
                        }
                        if (Integer.parseInt(Quantity)==0){
                            Toast.makeText(view.getContext(), "The Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                            holder.Qty.setText(customerFragmentKartGet.getQuantity());
                            return;
                        }
                        editQty(products1, Quantity);
                        notifyDataSetChanged();
                        arrayList.clear();
                        keyList.clear();
                    }
                });

                holder.removeKart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.Productname.getContext());
                        builder.setTitle("Are you sure?");
                        builder.setMessage("\""+product.getProductname()+"\" will be removed from Cart.");


                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase.getInstance().getReference().child("Kart").child(products1).removeValue();
                                Toast.makeText(view.getContext(), "Product \""+product.getProductname()+"\" Removed from Cart Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(holder.Productname.getContext(), "Product \""+product.getProductname()+"\" removal from Cart Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                    }
                });
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
            removeKart=userView.findViewById(R.id.removeKart);
        }
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

    public ArrayList<String> getSelectedKart(){
        return checkList;
    }

    public boolean getQtyy(){
        return flag;
    }
}