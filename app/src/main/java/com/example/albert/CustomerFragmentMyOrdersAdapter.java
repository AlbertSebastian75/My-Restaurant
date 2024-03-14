package com.example.albert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class
CustomerFragmentMyOrdersAdapter extends RecyclerView.Adapter<CustomerFragmentMyOrdersAdapter.MyViewHolder>{

    Context context;
    ArrayList<CustomerFragmentOrdersGet> arrayList;
    ArrayList<String> keylist;
    DatabaseReference databaseReference;

    public CustomerFragmentMyOrdersAdapter(Context context, ArrayList<CustomerFragmentOrdersGet> arrayList, ArrayList<String> keylist) {
        this.context = context;
        this.arrayList = arrayList;
        this.keylist = keylist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_myorders,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CustomerFragmentOrdersGet products=arrayList.get(position);
        String products1=keylist.get(position);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //view product name
        ArrayList<String> p12 = new ArrayList<>();
        String p1 = products.getProducts();
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

        //view table name
        String p2 = products.getTable();
        p2 = p2.replace("[", "");
        p2 = p2.replace("]", "");
        databaseReference.child("Table").child(p2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CustomerActivityKartTableGet product = snapshot.getValue(CustomerActivityKartTableGet.class);
                String p22 = product.getCategory()+", "+ product.getTableName()+", "+ product.getChair();
                holder.Table.setText(p22);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Get Quantity purchased
        String p3 = products.getQuantity();
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
        String p4 = products.getAmount();
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
        holder.Date.setText(products.getDate()+",");
        holder.Slot.setText(products.getTime());

        //view parking details
        if(!products.getPark().isEmpty() && !products.getPark().equals("0")) {
            holder.ParkText.setVisibility(View.VISIBLE);
            holder.Park.setVisibility(View.VISIBLE);
            databaseReference.child("Park").child(products.getPark()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CustomerFragmentParkingGet product = snapshot.getValue(CustomerFragmentParkingGet.class);

                        databaseReference.child("Slot").child(product.getPark()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                CustomerFragmentParkingSlotGet ppppp = snapshot.getValue(CustomerFragmentParkingSlotGet.class);
                                String p22 = ppppp.getCategory()+", "+ppppp.getSlotName();
                                holder.Park.setText(p22);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

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

        TextView Products, Amount, Date, Slot, Table, Quantity, Total, Park, ParkText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Products=itemView.findViewById(R.id.Products);
            Date=itemView.findViewById(R.id.Date);
            Amount=itemView.findViewById(R.id.Amount);
            Slot=itemView.findViewById(R.id.Slot);
            Quantity=itemView.findViewById(R.id.Quantity);
            Table=itemView.findViewById(R.id.Table);
            Total=itemView.findViewById(R.id.Total);
            ParkText=itemView.findViewById(R.id.parkText);
            Park=itemView.findViewById(R.id.Park);

        }
    }
}
