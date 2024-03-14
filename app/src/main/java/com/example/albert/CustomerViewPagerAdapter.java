package com.example.albert;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class CustomerViewPagerAdapter extends FragmentPagerAdapter {

    public CustomerViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // Redirect to which fragment as per the tabs
        if (position==0){
            return new CustomerFragmentProduct();
        }
        else if (position==1){
            return new CustomerFragmentKart();
        }
        else {
            return new CustomerFragmentOrders();
        }
    }

    @Override
    public int getCount() {
        // No. of tabs
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Setting titles to each tabs
        if (position==0){
            return "Products";
        }
        else if (position==1){
            return "My Cart";
        }
        else {
            return "Orders";
        }
    }
}
