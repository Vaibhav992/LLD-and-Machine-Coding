package com.machine_coding_round.cart.mylearning.strategy;

import com.machine_coding_round.cart.mylearning.enums.DiscountType;
import com.machine_coding_round.cart.mylearning.model.CartItem;

import java.util.List;

public interface DiscountStrategy {
    double calculateDiscount(List<CartItem> items);

    String getDescription();

    DiscountType getType();
}
