package com.example.albert;

import android.app.DatePickerDialog;
import android.content.Intent;
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

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Calendar;

public class CustomerFragmentParking extends Fragment {

    TextView textDateBox;
    ImageView dateBox;
    Button dateBtn, viewParkingBtn;
    int y, d, m;
    Spinner timeBox;
    int flag=0;
    String[] timeslot;
    private SwipeRefreshLayout swipeRefreshLayout;

    public CustomerFragmentParking() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.customer_fragment_parking, container, false);

        dateBox = root.findViewById(R.id.dateBox);
        textDateBox = root.findViewById(R.id.textDateBox);
        dateBtn = root.findViewById(R.id.dateBtn);
        viewParkingBtn = root.findViewById(R.id.viewParkingBtn);
        Calendar cal = Calendar.getInstance();

        dateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                y = cal.get(Calendar.YEAR);
                m = cal.get(Calendar.MONTH);
                m++;
                d = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog g = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        flag = 0;
                        textDateBox.setText(i2 + "/" + (i1 + 1) + "/" + i);
                        if (i == y && (i1 + 1) == m && i2 == d) {
                            flag = 1;
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
                        if (flag == 0) {
                            timeslot = new String[]{"7:00 AM", "7:30 AM", "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
                                    "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM",
                                    "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                        } else {
                            if ((hour < 7) || (hour == 7 && minute < 15)) {
                                timeslot = new String[]{"7:00 AM", "7:30 AM", "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if (hour == 7 && minute < 45) {
                                timeslot = new String[]{"7:30 AM", "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if ((hour == 7) || (hour == 8 && minute < 15)) {
                                timeslot = new String[]{"8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if (hour == 8 && minute < 45) {
                                timeslot = new String[]{"8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if ((hour == 8) || (hour == 9 && minute < 15)) {
                                timeslot = new String[]{"9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if (hour == 9 && minute < 45) {
                                timeslot = new String[]{"9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if ((hour == 9) || (hour == 10 && minute < 15)) {
                                timeslot = new String[]{"10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if (hour == 10 && minute < 45) {
                                timeslot = new String[]{"10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if ((hour == 10) || (hour == 11 && minute < 15)) {
                                timeslot = new String[]{"11:00 AM", "11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if (hour == 11 && minute < 45) {
                                timeslot = new String[]{"11:30 AM", "12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if ((hour == 11) || (hour == 12 && minute < 15)) {
                                timeslot = new String[]{"12:00 PM", "12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if (hour == 12 && minute < 45) {
                                timeslot = new String[]{"12:30  PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if ((hour == 12) || (hour == 13 && minute < 15)) {
                                timeslot = new String[]{"1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if (hour == 13 && minute < 45) {
                                timeslot = new String[]{"1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if ((hour == 13) || (hour == 14 && minute < 15)) {
                                timeslot = new String[]{"2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if (hour == 14 && minute < 45) {
                                timeslot = new String[]{"2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if ((hour == 14) || (hour == 15 && minute < 15)) {
                                timeslot = new String[]{"3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if (hour == 15 && minute < 45) {
                                timeslot = new String[]{"3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if ((hour == 15) || (hour == 16 && minute < 15)) {
                                timeslot = new String[]{"4:00 PM", "4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if (hour == 16 && minute < 45) {
                                timeslot = new String[]{"4:30 PM", "5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if ((hour == 16) || (hour == 17 && minute < 15)) {
                                timeslot = new String[]{"5:00 PM", "5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if (hour == 17 && minute < 45) {
                                timeslot = new String[]{"5:30  PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if ((hour == 17) || (hour == 18 && minute < 15)) {
                                timeslot = new String[]{"6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if (hour == 18 && minute < 45) {
                                timeslot = new String[]{"6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if ((hour == 18) || (hour == 19 && minute < 15)) {
                                timeslot = new String[]{"7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if (hour == 19 && minute < 45) {
                                timeslot = new String[]{"7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if ((hour == 19) || (hour == 20 && minute < 15)) {
                                timeslot = new String[]{"8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if (hour == 20 && minute < 45) {
                                timeslot = new String[]{"8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if ((hour == 20) || (hour == 21 && minute < 15)) {
                                timeslot = new String[]{"9:00 PM", "9:30 PM", "10:00 PM"};
                            } else if (hour == 21 && minute < 45) {
                                timeslot = new String[]{"9:30 PM", "10:00 PM"};
                            } else if ((hour == 21) || (hour == 22 && minute < 15)) {
                                timeslot = new String[]{"10:00 PM"};
                            } else {
                                timeslot = new String[]{""};
                            }

                        }

                        //Add time
                        timeBox = root.findViewById(R.id.timeBox);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, timeslot);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        timeBox.setAdapter(adapter);
                    }

                }, y, m - 1, d);

                g.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);


                Calendar calendar = Calendar.getInstance();
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

                    if (tmBox.equals("")) {
                        Toast.makeText(getActivity(), "Please, Pick a Valid Slot", Toast.LENGTH_SHORT).show();
                    } else {
                        //Moving to another Fragment
                        //CustomerFragmentParkingAvailable customerFragmentParkingAdapter = new CustomerFragmentParkingAvailable();
                        //FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        //Bundle args = new Bundle();
                        //args.putString("txtDate", txtDate);
                        //args.putString("tmBox", tmBox);
                        //customerFragmentParkingAdapter.setArguments(args);
                        //fragmentTransaction.replace(R.id.container, customerFragmentParkingAdapter).commit();

                        //Moving to another Activity
                        Intent intent = new Intent(getActivity(), CustomerFragmentParkingAvailable.class);
                        intent.putExtra("txtDate", txtDate);
                        intent.putExtra("tmBox", tmBox);
                        getActivity().startActivity(intent);
                    }
                }
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

        viewParkingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Moving to another Activity
                Intent intent = new Intent(getActivity(), CustomerActivityViewParking.class);
                getActivity().startActivity(intent);
            }
        });

        return root;
    }
}