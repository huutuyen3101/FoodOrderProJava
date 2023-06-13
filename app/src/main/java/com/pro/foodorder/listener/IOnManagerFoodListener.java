package com.pro.foodorder.listener;

import com.pro.foodorder.model.Food;

public interface IOnManagerFoodListener {
    void onClickUpdateFood(Food food);
    void onClickDeleteFood(Food food);
}
