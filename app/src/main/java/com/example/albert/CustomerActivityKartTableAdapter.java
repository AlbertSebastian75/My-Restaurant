package com.example.albert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerActivityKartTableAdapter extends RecyclerView.Adapter<CustomerActivityKartTableAdapter.MyViewHolder>{

    Context context;
    ArrayList<CustomerActivityKartTableGet> arrayList;
    ArrayList<String> keylist, checkList;
    String txtDate, tmBox;

    DatabaseReference DataRef, databaseReference, database;
    SharedPreferences sharedPreferences;

    public CustomerActivityKartTableAdapter(Context context, ArrayList<CustomerActivityKartTableGet> arrayList, ArrayList<String> keylist, ArrayList<String> checkList, String txtDate, String tmBox) {
        this.context = context;
        this.arrayList = arrayList;
        this.keylist = keylist;
        this.checkList= checkList;
        this.txtDate = txtDate;
        this.tmBox = tmBox;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_table,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CustomerActivityKartTableGet table=arrayList.get(position);
        String table1=keylist.get(position);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        DataRef= databaseReference.child("Book");

        //get email: user
        sharedPreferences = this.context.getSharedPreferences("com.example.albert", Context.MODE_PRIVATE);
        String name =  sharedPreferences.getString("name", "not found");

        holder.TableName.setText(table.getTableName());
        holder.Chair.setText(table.getChair());

        holder.bookTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.TableName.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Hai "+name+"\n"+"Conforming the Booking of \""+table.getTableName()+"\"");

                builder.setPositiveButton("Book", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Setting key
                        String key = DataRef.push().getKey();
                        DataRef.child(key).setValue(key);

                        //Get kart item, Date and time
                        DataRef.child(key).child("Date").setValue(txtDate);
                        DataRef.child(key).child("Time").setValue(tmBox);

                        DataRef.child(key).child("Username").setValue(name);
                        DataRef.child(key).child("Table").setValue(table1);
                        DataRef.child(key).child("Status").setValue("placed");
                        DataRef.child(key).child("Check").setValue(name+"_placed");
                        DataRef.child(key).child("Park").setValue("");

                        //get product details
                        getProductDetails(key, checkList);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.TableName.getContext(), "Table Booking Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

    }

    private void moveBack() {
        //remove kart
        for(int m=0;m<checkList.size();m++){
            FirebaseDatabase.getInstance().getReference().child("Kart").child(checkList.get(m)).removeValue();
        }

        Intent intent = new Intent(context, CustomerActivityKartBlock.class);
        Toast.makeText(context, "Booking Successful", Toast.LENGTH_SHORT).show();
        context.startActivity(intent);
    }

    private void getProductDetails(String key, ArrayList<String> checkList) {

        //from checkList(Kart id): we need product id(checkList1)
        ArrayList<String> checkList1 = new ArrayList<>();
        ArrayList<String> checkList2 = new ArrayList<>();
        ArrayList<String> checkList3 = new ArrayList<>();

        for(int k=0;k<checkList.size();k++){
            String productid = checkList.get(k);
            databaseReference.child("Kart").child(productid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    CustomerFragmentKartGet product = snapshot.getValue(CustomerFragmentKartGet.class);
                    checkList1.add(product.getProduct());
                    DataRef.child(key).child("Products").setValue(String.valueOf(checkList1));

                    //Add Quantity
                    checkList2.add(product.getQuantity());
                    DataRef.child(key).child("Quantity").setValue(String.valueOf(checkList2));


                    String temp = product.getProduct();
                    //Get Amount also
                    databaseReference.child("Product").child(product.getProduct()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            CustomerFragmentProductGet product1 = snapshot1.getValue(CustomerFragmentProductGet.class);
                            checkList3.add(product1.getAmount());
                            DataRef.child(key).child("Amount").setValue(String.valueOf(checkList3));


                            //reduce qty.
                            int s = Integer.parseInt(product1.getStock()) - Integer.parseInt(product.getQuantity());
                            database = FirebaseDatabase.getInstance().getReference().child("Product");
                            database.child(temp).child("Stock").setValue(""+s);

                            moveBack();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error1) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView TableName, Chair;
        Button bookTable;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            TableName=itemView.findViewById(R.id.TableName);
            Chair=itemView.findViewById(R.id.Chair);
            bookTable=itemView.findViewById(R.id.bookTable);
        }
    }
}