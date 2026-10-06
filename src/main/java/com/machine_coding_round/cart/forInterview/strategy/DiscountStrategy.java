package com.machine_coding_round.cart.forInterview.strategy;

import com.machine_coding_round.cart.forInterview.model.CartItem;

import java.util.List;

public interface DiscountStrategy {
    double calculateDiscount(List<CartItem> items);
}
