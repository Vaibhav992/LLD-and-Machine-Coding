package com.machine_coding_round.cart.forInterview.strategy;

import com.machine_coding_round.cart.forInterview.exception.CartException;
import com.machine_coding_round.cart.forInterview.model.CartItem;

import java.util.List;

public class PercentageDiscountStrategy implements DiscountStrategy {
    private final double percentage;

    public PercentageDiscountStrategy(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new CartException("Percentage must be between 0 and 100");
        }
        this.percentage = percentage;
    }

    @Override
    public double calculateDiscount(List<CartItem> items) {
        double subtotal = items.stream().mapToDouble(CartItem::getSubtotal).sum();
        return subtotal * (percentage / 100.0);
    }
}
