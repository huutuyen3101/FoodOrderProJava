package com.pro.foodorder.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pro.foodorder.R;
import com.pro.foodorder.databinding.ActivityCategoryProductBinding;
import com.pro.foodorder.databinding.FragmentCategoryProductBinding;
import com.pro.foodorder.utils.ProductAdapter_tabLayout;

public class CategoryProductFragment extends Fragment {
    FragmentCategoryProductBinding binding;
    private TabLayout tabLayoutProduct;
    private ViewPager2 viewPagerProduct;
    private ProductAdapter_tabLayout adapter_tabLayout;

    public CategoryProductFragment() {
        // Required empty public constructor
    }

    public void tabLayout(){
        tabLayoutProduct = binding.tabProductFragment;
        viewPagerProduct = binding.viewPagerProductFragment;
        adapter_tabLayout = new ProductAdapter_tabLayout(this);
        viewPagerProduct.setAdapter(adapter_tabLayout);
        new TabLayoutMediator(tabLayoutProduct, viewPagerProduct, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0: tab.setText("Món mới");
                        break;
                    case 1: tab.setText("Đặc biệt");
                        break;
                    case 2:tab.setText("Combo1");
                        break;
                    case 3:tab.setText("Combo2");
                        break;
                }
            }
        }).attach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCategoryProductBinding.inflate(inflater, container, false);
        tabLayout();
        return binding.getRoot();
    }
}