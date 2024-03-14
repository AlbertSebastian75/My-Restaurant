package com.example.albert;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.albert.ui.bookStaff.AvailTableFragment;
import com.example.albert.ui.bookStaffView.HomeFragment;

import java.util.Calendar;

public class WaiterActivity extends AppCompatActivity {

    Button view_Booking;
    TextView textDateBox, email;
    ImageView dateBox;
    Button dateBtn, signout;
    int y, d, m;
    Spinner timeBox;
    int flag=0;
    String[] timeslot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter);
        email = findViewById(R.id.email);
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.albert", Context.MODE_PRIVATE);
        String name =  sharedPreferences.getString("name", "not found");
        email.setText(name);

        signout = findViewById(R.id.signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WaiterActivity.this);
                builder.setTitle("Logout Confirmation");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("LogOut", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        Toast.makeText(getApplicationContext(), "Logout Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(WaiterActivity.this, LoginActivity.class));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Logout Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        view_Booking = findViewById(R.id.view_Booking);
        view_Booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment myFragment = new HomeFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.container, myFragment);
                fragmentTransaction.addToBackStack("ParkActivity").commit();
            }
        });

        dateBox = findViewById(R.id.dateBox);
        textDateBox = findViewById(R.id.textDateBox);
        dateBtn = findViewById(R.id.dateBtn);
        Calendar cal = Calendar.getInstance();

        dateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                y = cal.get(Calendar.YEAR);
                m = cal.get(Calendar.MONTH);
                m++;
                d = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog g = new DatePickerDialog(WaiterActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        timeBox = findViewById(R.id.timeBox);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, timeslot);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        timeBox.setAdapter(adapter);
                    }

                }, y, m - 1, d);

                g.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 30);
                g.getDatePicker().setMaxDate(calendar.getTimeInMillis());

                g.show();
            }
        });

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Getting date and time to reserve
                String txtDate = textDateBox.getText().toString();
                if (txtDate.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please, Pick your desired Slot", Toast.LENGTH_SHORT).show();
                } else {
                    String tmBox = timeBox.getSelectedItem().toString();

                    if (tmBox.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please, Pick a Valid Slot", Toast.LENGTH_SHORT).show();
                    } else {
                        //Moving to another Fragment
                        AvailTableFragment myFragment = new AvailTableFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putString("txtDate", txtDate);
                        bundle.putString("tmBox", tmBox);
                        bundle.putString("name", name);
                        myFragment.setArguments(bundle);
                        fragmentTransaction.add(R.id.container, myFragment);
                        fragmentTransaction.addToBackStack("ParkActivity").commit();
                    }
                }
            }
        });
        setUpOnBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.staff, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewRestaurantDetails:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpOnBackPressed() {
        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //empty
            }
        });
    }

}