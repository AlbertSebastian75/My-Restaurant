package com.example.albert.ui.staff;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albert.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Users> arrayList;
    ArrayList<String> keyList;

    public MyAdapter(Context context, ArrayList<Users> arrayList, ArrayList<String> keyList) {
        this.context = context;
        this.arrayList = arrayList;
        this.keyList = keyList;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_staff, parent, false);
        return new MyAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {

        Users users = arrayList.get(position);

        holder.Username.setText(users.getUsername());
        holder.Mobile.setText(users.getMobile());

        String users1 = keyList.get(position);
        String userEmail = decodeUserEmail(users1);
        holder.Email.setText(userEmail);

        holder.acceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.Email.getContext())
                        .setContentHolder(new ViewHolder(R.layout.accept_staff))
                        .setExpanded(true, 600)
                        .create();


                View view1 = dialogPlus.getHolderView();

                EditText StaffEmailAccept = view1.findViewById(R.id.StaffEmailAccept);
                Button StaffAcceptBtn = view1.findViewById(R.id.StaffAcceptBtn);

                //Drop down menu
                String[] Designation = {"Manager", "Waiter", "Chef", "Parking Staff", "Kitchen Staff", "Cleaning Staff"};
                Spinner StaffDesignationAccept = view1.findViewById(R.id.StaffDesignationAccept);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(view1.getContext(), android.R.layout.simple_spinner_item, Designation);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                StaffDesignationAccept.setAdapter(adapter);

                StaffEmailAccept.setText(userEmail);
                dialogPlus.show();

                StaffAcceptBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("Status", StaffDesignationAccept.getSelectedItem().toString());

                        FirebaseDatabase.getInstance().getReference().child("users")
                                .child(users1).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.Email.getContext(), "User \""+users.getUsername()+"\" Updated Successfully", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(holder.Email.getContext(), "Error while Updating", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                });
                    }
                });
                arrayList.clear();
                keyList.clear();
            }
        });

        holder.declineRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.Email.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Deleted staff can't be undo!\n\""+users.getUsername()+"\" will be deleted permanently.");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(users1).child("Status").setValue("1");
                        arrayList.clear();
                        keyList.clear();
                        Toast.makeText(view.getContext(), "\""+users.getUsername()+"\" Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.Email.getContext(), "\""+users.getUsername()+"\" Deletion Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Username, Mobile, Email;
        Button acceptRequest, declineRequest;

        public MyViewHolder(@NonNull View userView) {
            super(userView);

            Username = userView.findViewById(R.id.Username);
            Mobile = userView.findViewById(R.id.Mobile);
            Email = userView.findViewById(R.id.Email);
            acceptRequest = userView.findViewById(R.id.acceptRequest);
            declineRequest = userView.findViewById(R.id.declineRequest);
        }

    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}