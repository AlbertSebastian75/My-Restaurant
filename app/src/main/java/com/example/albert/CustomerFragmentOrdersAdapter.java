package com.example.albert;

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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class CustomerFragmentOrdersAdapter extends RecyclerView.Adapter<CustomerFragmentOrdersAdapter.MyViewHolder>{

    Context context;
    ArrayList<CustomerFragmentOrdersGet> arrayList;
    ArrayList<String> keylist;
    DatabaseReference databaseReference;

    public CustomerFragmentOrdersAdapter(Context context, ArrayList<CustomerFragmentOrdersGet> arrayList, ArrayList<String> keylist) {
        this.context = context;
        this.arrayList = arrayList;
        this.keylist = keylist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_orders,parent,false);
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
        // we have product id from book table in array.
        String[] p11 = p1.split(",");
        for(int j=0;j<p11.length;j++){
            databaseReference.child("Product").child(p11[j]).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CustomerFragmentProductGet product = snapshot.getValue(CustomerFragmentProductGet.class);
                    p12.add(product.getProductname());
                    String p13 =String.valueOf(p12);
                    //substring starting from the second character
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

        String finalP5 = ""+p51;

        // show pay button for placed order
        if(products.getStatus().equals("placed")) {
            holder.payBtn.setText("Pay");
            holder.payBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = "Albert Sebastian";
                    String upiId = "albertsebastian75@okhdfcbank";
                    String number = "8590956627";
                    String amount = finalP5;
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(holder.Products.getContext());
                    alertDialogBuilder.setTitle(name);
                    alertDialogBuilder.setMessage("Pay " + number + " or " + upiId + " of ₹" + amount);
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Toast.makeText(view.getContext(), "Visit Again!", Toast.LENGTH_LONG).show();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });

            //current date and time.
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
            String date = sdf.format(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            //get date and time of the orders.
            String pp = products.getTime();
            String lastTwoChars = pp.substring(pp.length() - 2);
            pp = pp.substring(0, pp.length() - 3);
            String[] pp1 = pp.split(":");
            int[] pp2 = new int[pp1.length];
            for (int i = 0; i < pp1.length; i++) {
                pp2[i] = Integer.parseInt(pp1[i].trim());
            }
            if (lastTwoChars.equals("PM")) {
                pp2[0] += 12;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
            Date d1 = null, d2 = null;
            try {
                //get current date and ordered date
                d1 = dateFormat.parse(date);
                d2 = dateFormat.parse(products.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // show cancel and park button: Only when the current date and time is prior to the ordered date and time
            if (((date.equals(products.getDate())) && (hour < pp2[0])) ||
                    (date.equals(products.getDate()) && (hour == pp2[0]) && ((minute - 10) <= pp2[1])) ||
                    (d1.compareTo(d2) < 0)) {
                holder.cancelOrders.setVisibility(View.VISIBLE);
                holder.cancelOrders.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Are you sure?");
                        builder.setMessage("The Booking will be cancel.");

                        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //Edit the product quantity back
                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                for (int j = 0; j < p11.length; j++) {
                                    int finalJ = j;
                                    db.child("Product").child(p11[finalJ]).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshott) {
                                            CustomerFragmentProductGet pr = snapshott.getValue(CustomerFragmentProductGet.class);
                                            int temp = Integer.parseInt(pr.getStock()) + p32[finalJ];
                                            db.child("Product").child(p11[finalJ]).child("Stock").setValue("" + temp);
                                            arrayList.clear();
                                            keylist.clear();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                                SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.albert", Context.MODE_PRIVATE);
                                String name = sharedPreferences.getString("name", "not found");
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                database.child("Book").child(products1).child("Status").setValue("cancelled");
                                database.child("Book").child(products1).child("Check").setValue(name + "_cancelled");
                                database.child("Book").child(products1).child("Table").setValue("0");
                                Toast.makeText(view.getContext(), "Booking Cancelled Successfully", Toast.LENGTH_SHORT).show();

                                //cancel park as well. when order cancelled
                                Query query = database.child("Park").orderByKey().equalTo(products.getPark());
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            childSnapshot.child("Park").getRef().setValue("0");
                                            childSnapshot.child("Status").getRef().setValue("Cancelled");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle the error
                                    }
                                });
                                database.child("Book").child(products1).child("Park").setValue("");
                                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                CustomerFragmentOrders customerFragmentOrders = new CustomerFragmentOrders();
                                fragmentTransaction.add(R.id.container, customerFragmentOrders);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(view.getContext(), "Booking Cancellation Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                    }
                });

                //display parking button
                if (products.getPark().isEmpty()) {
                    holder.parkBook.setVisibility(View.VISIBLE);
                    holder.parkBook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, CustomerFragmentParkingAvailable.class);
                            intent.putExtra("txtDate", products.getDate());
                            intent.putExtra("tmBox", products.getTime());
                            intent.putExtra("bookKey", products1);
                            context.startActivity(intent);
                        }
                    });
                }
            }
        }
        if(products.getStatus().equals("payed")){
            holder.payBtn.setText("Payed");
        }

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
        Button payBtn, cancelOrders, parkBook;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Products=itemView.findViewById(R.id.Products);
            Date=itemView.findViewById(R.id.Date);
            Amount=itemView.findViewById(R.id.Amount);
            Slot=itemView.findViewById(R.id.Slot);
            Quantity=itemView.findViewById(R.id.Quantity);
            Table=itemView.findViewById(R.id.Table);
            Total=itemView.findViewById(R.id.Total);
            payBtn=itemView.findViewById(R.id.payBtn);
            cancelOrders=itemView.findViewById(R.id.cancelOrders);
            parkBook=itemView.findViewById(R.id.parkBook);
            ParkText=itemView.findViewById(R.id.parkText);
            Park=itemView.findViewById(R.id.Park);

        }
    }
}
