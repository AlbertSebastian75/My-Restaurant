package com.example.albert.ui.bookStaffView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albert.CustomerActivityKartTableGet;
import com.example.albert.CustomerFragmentParkingGet;
import com.example.albert.CustomerFragmentParkingSlotGet;
import com.example.albert.CustomerFragmentProductGet;
import com.example.albert.R;
import com.example.albert.ui.viewAvailPark.AvailParkFragment;
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

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    Context context;
    ArrayList<Book> arrayList;
    ArrayList<String> keyList;
    DatabaseReference databaseReference;

    public MyAdapter(Context context, ArrayList<Book> arrayList, ArrayList<String> keyList) {
        this.context = context;
        this.arrayList = arrayList;
        this.keyList = keyList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_home,parent,false);
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

        //view table name
        String p2 = book.getTable();
        p2 = p2.replace("[", "");
        p2 = p2.replace("]", "");
        databaseReference.child("Table").child(p2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CustomerActivityKartTableGet product = snapshot.getValue(CustomerActivityKartTableGet.class);
                String p22 = product.getCategory()+", "+ product.getTableName()+", Chair:"+ product.getChair();
                holder.Table.setText(p22);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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


        if(book.getStatus().equals("payed")) {
            holder.payBtn.setVisibility(View.VISIBLE);
            holder.payBtn.setText("Payed");
        }

        //view parking details
        if(!book.getPark().isEmpty() && !book.getPark().equals("0")) {
            holder.ParkText.setVisibility(View.VISIBLE);
            holder.Park.setVisibility(View.VISIBLE);
            databaseReference.child("Park").child(book.getPark()).addListenerForSingleValueEvent(new ValueEventListener() {
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

        if(book.getStatus().equals("placed")){
            holder.payBtn.setVisibility(View.VISIBLE);
            holder.payBtn.setText("Pay");
            holder.cancelOrders.setVisibility(View.VISIBLE);
            int finalP5 = p51;
            holder.payBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = "Albert Sebastian";
                    String upiId = "albertsebastian75@okhdfcbank";
                    String number = "8590956627";
                    String amount = String.valueOf(finalP5);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(holder.Products.getContext());
                    alertDialogBuilder.setTitle(name);
                    alertDialogBuilder.setMessage("Pay "+number+" or "+upiId+" of ₹"+ amount);
                    alertDialogBuilder.setPositiveButton("Confirm Payment", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            databaseReference.child("Book").child(products1).child("Status").setValue("payed");
                            databaseReference.child("Book").child(products1).child("Check").setValue(book.getUsername()+"_payed");
                            arrayList.clear();
                            keyList.clear();
                            Toast.makeText(view.getContext(), "The payment is successful!",Toast.LENGTH_LONG).show();
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(view.getContext(), "The payment is Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });

            holder.cancelOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Are you sure?");
                    builder.setMessage("The Booking will be cancel.");
                    builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                                    for(int j=0;j<p11.length;j++){
                                        int finalJ = j;
                                        database.child("Product").child(p11[finalJ]).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshott) {
                                                CustomerFragmentProductGet pr = snapshott.getValue(CustomerFragmentProductGet.class);
                                                int temp = Integer.parseInt(pr.getStock())+p32[finalJ];
                                                database.child("Product").child(p11[finalJ]).child("Stock").setValue(""+temp);
                                                arrayList.clear();
                                                keyList.clear();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                            database.child("Book").child(products1).child("Status").setValue("cancelled");
                            database.child("Book").child(products1).child("Check").setValue(book.getUsername()+"_cancelled");
                            database.child("Book").child(products1).child("Table").setValue("0");
                            Toast.makeText(view.getContext(), "Booking Cancelled Successfully", Toast.LENGTH_SHORT).show();


                            Query query = database.child("Park").orderByKey().equalTo(book.getPark());
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
                            HomeFragment homeFragment = new HomeFragment();
                            fragmentTransaction.add(R.id.container, homeFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(view.getContext(), "Booking Cancellation Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                }
            });

            //current date and time.
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
            String date = sdf.format(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            //get date and time of the orders.
            String pp = book.getTime();
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
                d1 = dateFormat.parse(date);
                d2 = dateFormat.parse(book.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Add park button
            if (((date.equals(book.getDate())) && (hour < pp2[0])) ||
                    (date.equals(book.getDate()) && (hour == pp2[0]) && ((minute - 10) <= pp2[1])) ||
                    (d1.compareTo(d2) < 0)) {
                if (book.getPark().isEmpty()) {
                    holder.parkBook.setVisibility(View.VISIBLE);
                    holder.parkBook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            AvailParkFragment myFragment = new AvailParkFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("txtDate", book.getDate());
                            bundle.putString("tmBox", book.getTime());
                            bundle.putString("bookKey", products1);
                            myFragment.setArguments(bundle);
                            fragmentTransaction.replace(R.id.container, myFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Products, Amount, Date, Slot, Username, Table, Quantity, Total, Status, Park, ParkText;
        Button payBtn, cancelOrders, parkBook;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Products=itemView.findViewById(R.id.Products);
            Date=itemView.findViewById(R.id.Date);
            Amount=itemView.findViewById(R.id.Amount);
            Slot=itemView.findViewById(R.id.Slot);
            Quantity=itemView.findViewById(R.id.Quantity);
            Username=itemView.findViewById(R.id.Username);
            Table=itemView.findViewById(R.id.Table);
            Total=itemView.findViewById(R.id.Total);
            payBtn=itemView.findViewById(R.id.payBtn);
            cancelOrders=itemView.findViewById(R.id.cancelOrders);
            parkBook=itemView.findViewById(R.id.parkBook);
            ParkText=itemView.findViewById(R.id.parkText);
            Park=itemView.findViewById(R.id.Park);
        }
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}