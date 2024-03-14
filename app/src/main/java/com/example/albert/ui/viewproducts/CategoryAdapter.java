package com.example.albert.ui.viewproducts;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>{

    Context context;
    ArrayList<Products> arrayList;
    ArrayList<String> keylist;

    public CategoryAdapter(Context context, ArrayList<Products> arrayList, ArrayList<String> keylist) {
        this.context = context;
        this.arrayList = arrayList;
        this.keylist = keylist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_admin,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Products products=arrayList.get(position);
        String products1=keylist.get(position);

        holder.Productname.setText(products.getProductname());
        holder.Category.setText(products.getCategory());
        holder.Amount.setText(products.getAmount());
        holder.Stock.setText(products.getStock());
        Picasso.get().load(products.getImageUrl()).into(holder.ImageUrl);

        //Accessing update_pop.xml
        holder.editProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.ImageUrl.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_popup))
                        .setExpanded(true, 1200)
                        .create();

                View view1=dialogPlus.getHolderView();

                EditText ProductUpdateName = view1.findViewById(R.id.TableUpdateName);
                EditText ProductUpdateCategory = view1.findViewById(R.id.TableUpdateCategory);
                EditText ProductUpdateAmount = view1.findViewById(R.id.TableUpdateAmount);
                EditText ProductUpdateStock = view1.findViewById(R.id.TableUpdateStock);
                Button ProductUpdateBtn = view1.findViewById(R.id.TableUpdateBtn);

                ProductUpdateName.setText(products.getProductname());
                ProductUpdateCategory.setText(products.getCategory());
                ProductUpdateAmount.setText(products.getAmount());
                ProductUpdateStock.setText(products.getStock());

                dialogPlus.show();

                ProductUpdateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("Productname", ProductUpdateName.getText().toString());
                        map.put("Category", ProductUpdateCategory.getText().toString());
                        map.put("Amount", ProductUpdateAmount.getText().toString());
                        map.put("Stock", ProductUpdateStock.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("Product")
                                .child(products1).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.Productname.getContext(), "Product \""+products.getProductname()+"\" Updated Successfully", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(holder.Productname.getContext(), "Error while Updating", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                });
                        arrayList.clear();
                        keylist.clear();
                    }
                });

            }
        });

        holder.deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.Productname.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Deleted Products can't be undo!\n\""+products.getProductname()+"\" will be deleted permanently.");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Product").child(products1).child("Check1").setValue(products.getCategory()+"_0");
                        arrayList.clear();
                        keylist.clear();
                        Toast.makeText(view.getContext(), "Product \""+products.getProductname()+"\" Deleted Successfully", Toast.LENGTH_SHORT).show();

                        //the cart added products must be deleted
                        FirebaseDatabase.getInstance().getReference().child("Kart").orderByChild("Product").equalTo(products1).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    String temp = dataSnapshot.getKey();
                                    FirebaseDatabase.getInstance().getReference().child("Kart").child(temp).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.Productname.getContext(), "Product \""+products.getProductname()+"\" Deletion Cancelled", Toast.LENGTH_SHORT).show();
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

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Productname, Category, Amount, Stock;
        ImageView ImageUrl;

        Button deleteProduct, editProduct;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Productname=itemView.findViewById(R.id.Productname);
            Category=itemView.findViewById(R.id.Category);
            Amount=itemView.findViewById(R.id.Amount);
            ImageUrl=itemView.findViewById(R.id.ImageUrl);
            Stock=itemView.findViewById(R.id.Stock);

            deleteProduct=itemView.findViewById(R.id.deleteProduct);
            editProduct=itemView.findViewById(R.id.editProduct);
        }
    }
}
