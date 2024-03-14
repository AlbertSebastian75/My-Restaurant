package com.example.albert.ui.addproducts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.albert.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class AddProductsFragment extends Fragment {

    private static final int REQUEST_CODE_IMAGE = 101;
    ImageView imageViewAdd;
    TextView textViewprogress;
    EditText inputImageName, price, stock;
    ProgressBar progressBar;
    Button btnUpload;

    Uri imageUri;
    boolean isImageAdded=false;

    DatabaseReference DataRef;
    StorageReference StorageRef;

    Spinner spinner;
    ArrayList<String> spinnerList;
    ArrayAdapter<String> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_add_products, container, false);
        imageViewAdd=root.findViewById(R.id.imageViewAdd);
        inputImageName=root.findViewById(R.id.inputImageName);
        price=root.findViewById(R.id.price);
        stock=root.findViewById(R.id.stock);
        textViewprogress=root.findViewById(R.id.textViewprogress);
        progressBar=root.findViewById(R.id.progressBar);
        spinner=root.findViewById(R.id.spinner);
        btnUpload=root.findViewById(R.id.btnUpload);



        //To View Category
        spinnerList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerList);
        spinner.setAdapter(adapter);
        Showspinner();

        //First we need to hide progress bar and textview
        textViewprogress.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        //Database references
        DataRef=FirebaseDatabase.getInstance().getReference().child("Product");
        StorageRef= FirebaseStorage.getInstance().getReference().child("ProductImage");

        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Select Image from Gallery.
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String imageName=inputImageName.getText().toString().toLowerCase(Locale.ROOT);
                final String category=spinner.getSelectedItem().toString();
                final String amount=price.getText().toString();
                final String stocks=stock.getText().toString();

                if(isImageAdded==false){
                    Toast.makeText(getActivity(), "Please, Upload the Image!", Toast.LENGTH_SHORT).show();
                }else if(imageName.isEmpty()){
                    showError(inputImageName,"Product Name can't be empty!");
                }else if(amount.isEmpty()){
                    showError(price,"Amount can't be empty!");
                }else if(stocks.isEmpty()){
                    showError(stock,"Stock can't be empty!");
                }
                else{
                    DataRef.child("Check").equalTo(imageName+"_"+category).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                showError(inputImageName,"Product Already Exist!");
                            }
                            else{
                                if(isImageAdded!=false && imageName!=null) {
                                    //Here we upload the image
                                    uploadImage(imageName, category, amount, stocks);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        setUpOnBackPressed();
        return root;
    }

    private void setUpOnBackPressed() {
        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //empty
            }
        });
    }

    private void uploadImage(final String imageName, final String category, final String amount, final String stocks) {
        //Make ProgressBar visible...
        textViewprogress.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        //create a random key
        String key=DataRef.push().getKey();
        //Upload Image... Giving name: "key with jpg extension"
        StorageRef.child(key+".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //While we upload the image on the storage, Here we will get that uploaded image's uri
                StorageRef.child(key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Link and name of the image is to be stored in the firebase
                        HashMap hashMap=new HashMap<>();
                        hashMap.put("Productname", imageName);
                        hashMap.put("Category", category);
                        hashMap.put("Amount", amount);
                        hashMap.put("Stock", stocks);
                        hashMap.put("Check1", category+"_1");
                        hashMap.put("Check", imageName+"_"+category);
                        hashMap.put("ImageUrl", uri.toString());

                        //Now we need to upload the data into firebase. Hence, we successfully added the image in the storage
                        DataRef.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(),"Product Uploaded Successfully",Toast.LENGTH_SHORT).show();
                                inputImageName.setText("");
                                price.setText("");
                                stock.setText("");
                                //imageViewAdd.setImageResource(0); // or myImageView.setImageDrawable(null);
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                //Here we get the progress of the uploading image
                double progress=(snapshot.getBytesTransferred()*100)/snapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
                textViewprogress.setText(progress+" %");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Select image URI from Gallery
        if(requestCode==REQUEST_CODE_IMAGE && data!=null){
            imageUri=data.getData();
            isImageAdded=true;
            //While selecting the image. Set it on the screen.
            imageViewAdd.setImageURI(imageUri);
        }
    }

    private void Showspinner() {
        FirebaseDatabase.getInstance().getReference().child("ProductCategory").orderByChild("CategoryName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item: snapshot.getChildren()){
                    if(!item.getValue().toString().equals("0")) {
                        spinnerList.add(item.getValue().toString());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }
}