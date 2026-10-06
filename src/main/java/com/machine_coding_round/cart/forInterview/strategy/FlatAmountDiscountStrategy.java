package com.machine_coding_round.cart.forInterview.strategy;

import com.machine_coding_round.cart.forInterview.exception.CartException;
import com.machine_coding_round.cart.forInterview.model.CartItem;

import java.util.List;

public class FlatAmountDiscountStrategy implements DiscountStrategy {
    private final double amount;

    public FlatAmountDiscountStrategy(double amount) {
        if (amount < 0) {
            throw new CartException("Discount amount cannot be negative");
        }
        this.amount = amount;
    }

    @Override
    public double calculateDiscount(List<CartItem> items) {
        double subtotal = items.stream().mapToDouble(CartItem::getSubtotal).sum();
        return Math.min(amount, subtotal);
    }
}
