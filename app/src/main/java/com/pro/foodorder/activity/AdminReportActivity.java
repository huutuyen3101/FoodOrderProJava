package com.pro.foodorder.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.foodorder.ControllerApplication;
import com.pro.foodorder.R;
import com.pro.foodorder.adapter.RevenueAdapter;
import com.pro.foodorder.constant.Constant;
import com.pro.foodorder.constant.GlobalFunction;
import com.pro.foodorder.databinding.ActivityAdminReportBinding;
import com.pro.foodorder.listener.IOnSingleClickListener;
import com.pro.foodorder.model.Order;
import com.pro.foodorder.utils.DateTimeUtils;
import com.pro.foodorder.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AdminReportActivity extends AppCompatActivity {

    private ActivityAdminReportBinding mActivityAdminReportBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityAdminReportBinding = ActivityAdminReportBinding.inflate(getLayoutInflater());
        setContentView(mActivityAdminReportBinding.getRoot());

        initToolbar();
        initListener();
        getListRevenue();
    }

    private void initToolbar() {
        mActivityAdminReportBinding.toolbar.imgBack.setVisibility(View.VISIBLE);
        mActivityAdminReportBinding.toolbar.imgCart.setVisibility(View.GONE);
        mActivityAdminReportBinding.toolbar.tvTitle.setText(getString(R.string.revenue));

        mActivityAdminReportBinding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void initListener() {
        mActivityAdminReportBinding.tvDateFrom.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFunction.showDatePicker(AdminReportActivity.this,
                        mActivityAdminReportBinding.tvDateFrom.getText().toString(), date -> {
                            mActivityAdminReportBinding.tvDateFrom.setText(date);
                            getListRevenue();
                });
            }
        });

        mActivityAdminReportBinding.tvDateTo.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFunction.showDatePicker(AdminReportActivity.this,
                        mActivityAdminReportBinding.tvDateTo.getText().toString(), date -> {
                            mActivityAdminReportBinding.tvDateTo.setText(date);
                            getListRevenue();
                });
            }
        });
    }

    private void getListRevenue() {
        ControllerApplication.get(this).getBookingDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (canAddOrder(order)) {
                        list.add(0, order);
                    }
                }
                handleDataHistories(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private boolean canAddOrder(@Nullable Order order) {
        if (order == null) {
            return false;
        }
        if (!order.isCompleted()) {
            return false;
        }
        String strDateFrom = mActivityAdminReportBinding.tvDateFrom.getText().toString();
        String strDateTo = mActivityAdminReportBinding.tvDateTo.getText().toString();
        if (StringUtil.isEmpty(strDateFrom) && StringUtil.isEmpty(strDateTo)) {
            return true;
        }
        String strDateOrder = DateTimeUtils.convertTimeStampToDate_2(order.getId());
        long longOrder = Long.parseLong(DateTimeUtils.convertDate2ToTimeStamp(strDateOrder));

        if (StringUtil.isEmpty(strDateFrom) && !StringUtil.isEmpty(strDateTo)) {
            long longDateTo = Long.parseLong(DateTimeUtils.convertDate2ToTimeStamp(strDateTo));
            return longOrder <= longDateTo;
        }
        if (!StringUtil.isEmpty(strDateFrom) && StringUtil.isEmpty(strDateTo)) {
            long longDateFrom = Long.parseLong(DateTimeUtils.convertDate2ToTimeStamp(strDateFrom));
            return longOrder >= longDateFrom;
        }
        long longDateTo = Long.parseLong(DateTimeUtils.convertDate2ToTimeStamp(strDateTo));
        long longDateFrom = Long.parseLong(DateTimeUtils.convertDate2ToTimeStamp(strDateFrom));
        return longOrder >= longDateFrom && longOrder <= longDateTo;
    }

    private void handleDataHistories(List<Order> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mActivityAdminReportBinding.rcvOrderHistory.setLayoutManager(linearLayoutManager);
        RevenueAdapter revenueAdapter = new RevenueAdapter(list);
        mActivityAdminReportBinding.rcvOrderHistory.setAdapter(revenueAdapter);

        // Calculate total
        String strTotalValue = getTotalValues(list) + Constant.CURRENCY;
        mActivityAdminReportBinding.tvTotalValue.setText(strTotalValue);
    }

    private int getTotalValues(List<Order> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }

        int total = 0;
        for (Order order : list) {
            total += order.getAmount();
        }
        return total;
    }
}