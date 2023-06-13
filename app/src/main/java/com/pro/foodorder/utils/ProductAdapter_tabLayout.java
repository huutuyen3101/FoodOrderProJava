package com.pro.foodorder.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pro.foodorder.fragment.FourFragment;
import com.pro.foodorder.fragment.NewProductFragment;
import com.pro.foodorder.fragment.TowFragment;
import com.pro.foodorder.fragment.TreeFragment;

public class ProductAdapter_tabLayout extends FragmentStateAdapter {

    public ProductAdapter_tabLayout(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new NewProductFragment();
            case 1: return new TowFragment();
            case 2: return new TreeFragment();
            case 3: return new FourFragment();
            default:return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
