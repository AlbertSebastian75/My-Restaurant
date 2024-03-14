package com.example.albert;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.Calendar;

public class CustomerFragmentKart extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    CustomerFragmentKartAdapter customerFragmentKartAdapter;
    ArrayList<CustomerFragmentKartGet> arrayList;
    ArrayList<String> keyList = new ArrayList<>();
    String key;
    SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

    TextView textDateBox;
    ImageView dateBox;
    Button dateBtn;
    int y, d, m;
    Spinner timeBox;

    Button bookKart;
    ArrayList<String> checkList = new ArrayList<>();
    String temp;

    int flag=0;
    String[] timeslot;

    public CustomerFragmentKart() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.customer_fragment_kart, container, false);

        bookKart=root.findViewById(R.id.bookKart);
        recyclerView=root.findViewById(R.id.viewKart);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Kart");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        arrayList = new ArrayList<>();
        customerFragmentKartAdapter = new CustomerFragmentKartAdapter(getActivity(), arrayList, keyList);
        recyclerView.setAdapter(customerFragmentKartAdapter);

        SharedPreferences sharedPreferences;

        bookKart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Book button click: Get the details of checked items from adapter
                checkList = customerFragmentKartAdapter.getSelectedKart();
//                System.out.println(checkList);
                temp = String.valueOf(customerFragmentKartAdapter.getQtyy());
                if(checkList.isEmpty()){
                    Toast.makeText(getActivity(), "Please, Check in the products from the Cart!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(temp.equals("false")){
                    Toast.makeText(getActivity(), "The Quantity must not be greater than stock!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Get the another xml
                final DialogPlus dialogPlus = DialogPlus.newDialog(getActivity())
                        .setContentHolder(new ViewHolder(R.layout.kartimepick_popup))
                        .setExpanded(true, 675)
                        .create();

                //Add date
                View view1 = dialogPlus.getHolderView();
                dateBox = view1.findViewById(R.id.dateBox);
                textDateBox = view1.findViewById(R.id.textDateBox);
                dateBtn = view1.findViewById(R.id.dateBtn);
                Calendar cal = Calendar.getInstance();

                dateBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // current date
                        y  = cal.get(Calendar.YEAR);
                        m  = cal.get(Calendar.MONTH);
                        m++;
                        d  = cal.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog g = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                flag=0;
                                textDateBox.setText(i2+"/"+(i1+1)+"/"+i);
                                if(i==y && (i1+1)==m && i2==d){
                                    flag=1;
                                    sameDate();
                                }
                                setSlot(flag);
                            }

                            //if same date..
                            int hour, minute;
                            private void sameDate() {
                                hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                                minute = Calendar.getInstance().get(Calendar.MINUTE);
                            }

                            //Setting time
                            private void setSlot(int flag) {
                                if(flag==0) {
                                    timeslot = new String[]{"7:00 AM", "7:30 AM", "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
                                            "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM",
                                            "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                }
                                else{
                                    if ((hour<7)||(hour == 7 && minute < 15)){
                                        timeslot = new String[]{"7:00 AM", "7:30 AM", "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if (hour == 7 && minute < 45) {
                                        timeslot = new String[]{"7:30 AM", "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if ((hour == 7)||(hour == 8 && minute < 15)){
                                        timeslot = new String[]{"8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if (hour == 8 && minute < 45) {
                                        timeslot = new String[]{"8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if ((hour == 8)||(hour == 9 && minute < 15)){
                                        timeslot = new String[]{"9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if (hour == 9 && minute < 45) {
                                        timeslot = new String[]{"9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if ((hour == 9)||(hour == 10 && minute < 15)){
                                        timeslot = new String[]{"10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if (hour == 10 && minute < 45) {
                                        timeslot = new String[]{"10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if ((hour == 10)||(hour == 11 && minute < 15)){
                                        timeslot = new String[]{"11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if (hour == 11 && minute < 45) {
                                        timeslot = new String[]{"11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if ((hour == 11)||(hour == 12 && minute < 15)){
                                        timeslot = new String[]{"12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if (hour == 12 && minute < 45) {
                                        timeslot = new String[]{"12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if ((hour == 12)||(hour == 13 && minute < 15)){
                                        timeslot = new String[]{"1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if (hour == 13 && minute < 45) {
                                        timeslot = new String[]{"1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if ((hour == 13)||(hour == 14 && minute < 15)){
                                        timeslot = new String[]{"2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if (hour == 14 && minute < 45) {
                                        timeslot = new String[]{"2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if ((hour == 14)||(hour == 15 && minute < 15)){
                                        timeslot = new String[]{"3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if (hour == 15 && minute < 45) {
                                        timeslot = new String[]{"3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if ((hour == 15)||(hour == 16 && minute < 15)){
                                        timeslot = new String[]{"4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if (hour == 16 && minute < 45) {
                                        timeslot = new String[]{"4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if ((hour == 16)||(hour == 17 && minute < 15)){
                                        timeslot = new String[]{"5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if (hour == 17 && minute < 45) {
                                        timeslot = new String[]{"5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if ((hour == 17)||(hour == 18 && minute < 15)){
                                        timeslot = new String[]{"6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if (hour == 18 && minute < 45) {
                                        timeslot = new String[]{"6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if ((hour == 18)||(hour == 19 && minute < 15)){
                                        timeslot = new String[]{"7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if (hour == 19 && minute < 45) {
                                        timeslot = new String[]{"7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if ((hour == 19)||(hour == 20 && minute < 15)){
                                        timeslot = new String[]{"8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if (hour == 20 && minute < 45) {
                                        timeslot = new String[]{"8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if ((hour == 20)||(hour == 21 && minute < 15)){
                                        timeslot = new String[]{"9:00 PM", "9:30 PM", "10:00 PM"};
                                    } else if (hour == 21 && minute < 45) {
                                        timeslot = new String[]{"9:30 PM", "10:00 PM"};
                                    } else if ((hour == 21)||(hour == 22 && minute < 15)){
                                        timeslot = new String[]{"10:00 PM"};
                                    } else {
                                        timeslot = new String[]{""};
                                    }

                                }

                                //Add time
                                timeBox = view1.findViewById(R.id.timeBox);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, timeslot);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                timeBox.setAdapter(adapter);
                            }

                        }, y, m-1, d);
                        g.getDatePicker().setMinDate(System.currentTimeMillis()-1000);

                        Calendar calendar = Calendar.getInstance();
                        // Show only 15 days (setMaxDate)
                        calendar.add(Calendar.DAY_OF_MONTH, 15);
                        g.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                        g.show();
                    }
                });

                dateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Getting date and time to reserve
                        String txtDate = textDateBox.getText().toString();

                        if(txtDate.isEmpty()){
                            Toast.makeText(getActivity(), "Please, Pick your desired Slot",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String tmBox = timeBox.getSelectedItem().toString();

                            if(tmBox.equals("")){
                                Toast.makeText(getActivity(), "Please, Pick a Valid Slot",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                //Moving to another Activity
                                Intent intent = new Intent(getActivity(), CustomerActivityKartBlock.class);
                                intent.putExtra("txtDate", txtDate);
                                intent.putExtra("tmBox", tmBox);
                                intent.putStringArrayListExtra("checklist", checkList);
                                getActivity().startActivity(intent);
                            }
                        }
                    }
                });

                dialogPlus.show();
            }
        });

        //Refreshing
        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().recreate();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        sharedPreferences=this.getActivity().getSharedPreferences("com.example.albert", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "not found");
        databaseReference.orderByChild("Customer").equalTo(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                keyList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CustomerFragmentKartGet customerFragmentKartGet = dataSnapshot.getValue(CustomerFragmentKartGet.class);
                    key = dataSnapshot.getKey();
                    arrayList.add(customerFragmentKartGet);
                    keyList.add(key);
                }
                customerFragmentKartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        setUpOnBackPressed();
        return root;
    }
//
//    private void setUpOnBackPressed() {
//        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                //empty
//            }
//        });
//    }

}