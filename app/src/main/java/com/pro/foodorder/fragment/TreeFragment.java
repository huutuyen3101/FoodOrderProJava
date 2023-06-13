package com.pro.foodorder.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.foodorder.ControllerApplication;
import com.pro.foodorder.R;
import com.pro.foodorder.activity.FoodDetailActivity;
import com.pro.foodorder.adapter.FoodGridAdapter;
import com.pro.foodorder.constant.Constant;
import com.pro.foodorder.constant.GlobalFunction;
import com.pro.foodorder.databinding.FragmentNewProductBinding;
import com.pro.foodorder.databinding.FragmentTreeBinding;
import com.pro.foodorder.model.Food;

import java.util.ArrayList;
import java.util.List;

public class TreeFragment extends Fragment {

    private FragmentTreeBinding binding;
    private List<Food> mListFood;

    public TreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void getListFoodFromFirebase(String key) {
        if (getActivity() == null) {
            return;
        }
        ControllerApplication.get(getActivity()).getFoodDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListFood = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    if (food == null) {
                        return;
                    }
                    if (food.getType() == 3){
                        mListFood.add(0, food);
                    }
                }
                displayListFoodSuggest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void goToFoodDetail(@NonNull Food food) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_FOOD_OBJECT, food);
        GlobalFunction.startActivity(getActivity(), FoodDetailActivity.class, bundle);
    }

    private void displayListFoodSuggest() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        binding.rcvFood.setLayoutManager(gridLayoutManager);

        FoodGridAdapter mFoodGridAdapter = new FoodGridAdapter(mListFood, this::goToFoodDetail);
        binding.rcvFood.setAdapter(mFoodGridAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTreeBinding.inflate(inflater, container, false);
        getListFoodFromFirebase("");
        return binding.getRoot();
    }
}