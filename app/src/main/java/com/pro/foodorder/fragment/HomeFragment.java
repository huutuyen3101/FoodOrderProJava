package com.pro.foodorder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.foodorder.ControllerApplication;
import com.pro.foodorder.R;
import com.pro.foodorder.activity.CategoryProductActivity;
import com.pro.foodorder.activity.FoodDetailActivity;
import com.pro.foodorder.activity.MainActivity;
import com.pro.foodorder.adapter.FoodGridAdapter;
import com.pro.foodorder.adapter.FoodPopularAdapter;
import com.pro.foodorder.constant.Constant;
import com.pro.foodorder.constant.GlobalFunction;
import com.pro.foodorder.databinding.FragmentHomeBinding;
import com.pro.foodorder.model.Food;
import com.pro.foodorder.model.User;
import com.pro.foodorder.prefs.DataStoreManager;
import com.pro.foodorder.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding mFragmentHomeBinding;

    private List<Food> mListFood;
    private List<Food> mListFoodPopular;

    private final Handler mHandlerBanner = new Handler();
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {
            if (mListFoodPopular == null || mListFoodPopular.isEmpty()) {
                return;
            }
            if (mFragmentHomeBinding.viewpager2.getCurrentItem() == mListFoodPopular.size() - 1) {
                mFragmentHomeBinding.viewpager2.setCurrentItem(0);
                return;
            }
            mFragmentHomeBinding.viewpager2.setCurrentItem(mFragmentHomeBinding.viewpager2.getCurrentItem() + 1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        getListFoodFromFirebase("");
        initListener();
        loadDataUser();
        return mFragmentHomeBinding.getRoot();
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(true, getString(R.string.home));
        }
    }

    private void initListener() {
        mFragmentHomeBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    if (mListFood != null) mListFood.clear();
                    getListFoodFromFirebase("");
                }
            }
        });

        mFragmentHomeBinding.cvNewproduct.setOnClickListener(view -> goToCategoryActivity());
        mFragmentHomeBinding.cvSpecialproduct.setOnClickListener(view -> goToCategoryActivity());
        mFragmentHomeBinding.cvCombo1.setOnClickListener(view -> goToCategoryActivity());
        mFragmentHomeBinding.cvCombo2.setOnClickListener(view -> goToCategoryActivity());

        mFragmentHomeBinding.imgSearch.setOnClickListener(view -> searchFood());

        mFragmentHomeBinding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchFood();
                return true;
            }
            return false;
        });
    }

    private void goToCategoryActivity() {
        Intent intent = new Intent(getActivity(), CategoryProductActivity.class);
        startActivity(intent);
    }

    private void displayListFoodPopular() {
        FoodPopularAdapter mFoodPopularAdapter = new FoodPopularAdapter(getListFoodPopular(), this::goToFoodDetail);
        mFragmentHomeBinding.viewpager2.setAdapter(mFoodPopularAdapter);
        mFragmentHomeBinding.indicator3.setViewPager(mFragmentHomeBinding.viewpager2);

        mFragmentHomeBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandlerBanner.removeCallbacks(mRunnableBanner);
                mHandlerBanner.postDelayed(mRunnableBanner, 3000);
            }
        });
    }

    private void displayListFoodSuggest() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mFragmentHomeBinding.rcvFood.setLayoutManager(gridLayoutManager);

        FoodGridAdapter mFoodGridAdapter = new FoodGridAdapter(mListFood, this::goToFoodDetail);
        mFragmentHomeBinding.rcvFood.setAdapter(mFoodGridAdapter);
    }

    private List<Food> getListFoodPopular() {
        mListFoodPopular = new ArrayList<>();
        if (mListFood == null || mListFood.isEmpty()) {
            return mListFoodPopular;
        }
        for (Food food : mListFood) {
            if (food.isPopular()) {
                mListFoodPopular.add(food);
            }
        }
        return mListFoodPopular;
    }

    private void getListFoodFromFirebase(String key) {
        if (getActivity() == null) {
            return;
        }
        ControllerApplication.get(getActivity()).getFoodDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFragmentHomeBinding.layoutContent.setVisibility(View.VISIBLE);
                mListFood = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    if (food == null) {
                        return;
                    }

                    if (StringUtil.isEmpty(key)) {
                        mListFood.add(0, food);
                    } else {
                        if (GlobalFunction.getTextSearch(food.getName()).toLowerCase().trim()
                                .contains(GlobalFunction.getTextSearch(key).toLowerCase().trim())) {
                            mListFood.add(0, food);
                        }
                    }
                }
                displayListFoodPopular();
                displayListFoodSuggest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void searchFood() {
        String strKey = mFragmentHomeBinding.edtSearchName.getText().toString().trim();
        if (mListFood != null) mListFood.clear();
        getListFoodFromFirebase(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    private void goToFoodDetail(@NonNull Food food) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_FOOD_OBJECT, food);
        GlobalFunction.startActivity(getActivity(), FoodDetailActivity.class, bundle);
    }

    private void loadDataUser() {
        FirebaseDatabase.getInstance(Constant.FIREBASE_URL).getReference().child("User")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user.getId().equals(DataStoreManager.getUser().getId())) {
                                setLocation(user);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void setLocation(User user) {
        mFragmentHomeBinding.tvLocation.setText(user.getAddress());
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandlerBanner.removeCallbacks(mRunnableBanner);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandlerBanner.postDelayed(mRunnableBanner, 3000);
    }
}
