package com.example.albert.ui.staff;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.albert.R;

public class StaffFragment extends Fragment {

    Button manager, chef, waiter, kitchenStaff, cleaningStaff, parkingStaff;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_staff, container, false);

        manager = root.findViewById(R.id.manager);
        chef = root.findViewById(R.id.chef);
        waiter = root.findViewById(R.id.waiter);
        kitchenStaff = root.findViewById(R.id.kitchenStaff);
        cleaningStaff = root.findViewById(R.id.cleaningStaff);
        parkingStaff = root.findViewById(R.id.parkingStaff);

        manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCategorySelected("Manager");
            }
        });

        chef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCategorySelected("Chef");
            }
        });

        waiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCategorySelected("Waiter");
            }
        });

        kitchenStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCategorySelected("Kitchen Staff");
            }
        });

        cleaningStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCategorySelected("Cleaning Staff");
            }
        });

        parkingStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCategorySelected("Parking Staff");
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


    public void onCategorySelected(String position) {
        UserCategoryListenerFragment userCategoryListenerFragment = new UserCategoryListenerFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("category", position);
        userCategoryListenerFragment.setArguments(args);
        fragmentTransaction.replace(R.id.container, userCategoryListenerFragment).commit();
    }
}