package com.example.albert.ui.viewAvailPark;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albert.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AvailParkAdapter extends RecyclerView.Adapter<AvailParkAdapter.MyViewHolder>{

    Context context;
    ArrayList<Slot> arrayList;
    ArrayList<String> keylist;
    String txtDate, tmBox, bookKey;

    public AvailParkAdapter(Context context, ArrayList<Slot> arrayList, ArrayList<String> keylist, String txtDate, String tmBox, String bookKey) {
        this.context = context;
        this.arrayList = arrayList;
        this.keylist = keylist;
        this.txtDate = txtDate;
        this.tmBox = tmBox;
        this.bookKey = bookKey;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_avail_park,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Slot slot=arrayList.get(position);
        String slot1=keylist.get(position);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        holder.ParkName.setText(slot.getSlotName());

        holder.reservePark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.ParkName.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Reservation Confirmation on \""+slot.getSlotName()+"\"");

                //Adding details
                LayoutInflater inflater;
                inflater=LayoutInflater.from(view.getContext());
                View view1 = inflater.inflate(R.layout.admin_book_park, null);

                builder.setPositiveButton("Book", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText admin_book_email=view1.findViewById(R.id.admin_book_email);
                        if(admin_book_email.getText().toString().isEmpty()){
                            Toast.makeText(view.getContext(), "Please, Fill the Username!", Toast.LENGTH_SHORT).show();
                        }
                        else if(!admin_book_email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
                            Toast.makeText(view.getContext(), "Invalid Email!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //Setting key
                            DatabaseReference DataRef= FirebaseDatabase.getInstance().getReference().child("Park");
                            String key = DataRef.push().getKey();
                            DataRef.child(key).setValue(key);

                            DataRef.child(key).child("Date").setValue(txtDate);
                            DataRef.child(key).child("Time").setValue(tmBox);
                            DataRef.child(key).child("Status").setValue("placed");
                            DataRef.child(key).child("Username").setValue(admin_book_email.getText().toString());
                            DataRef.child(key).child("Park").setValue(slot1);
                            databaseReference.child("Book").child(bookKey).child("Park").setValue(key);
                            moveBack();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.ParkName.getContext(), "Reservation Confirmation is Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setView(view1).create().show();
            }
        });
    }

    private void moveBack() {
        Toast.makeText(context, "Reservation is successful", Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        ViewAvailParkFragment myFragment = new ViewAvailParkFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, myFragment);
        fragmentTransaction.addToBackStack(null).commit();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView ParkName;
        Button reservePark;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ParkName=itemView.findViewById(R.id.ParkName);
            reservePark=itemView.findViewById(R.id.reservePark);
        }
    }
}