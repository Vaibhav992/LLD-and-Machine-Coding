package com.machine_coding_round.cart.mylearning.strategy;

import com.machine_coding_round.cart.mylearning.enums.DiscountType;
import com.machine_coding_round.cart.mylearning.exception.CartException;
import com.machine_coding_round.cart.mylearning.model.CartItem;

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

    @Override
    public String getDescription() {
        return String.format("flat %.2f off", amount);
    }

    @Override
    public DiscountType getType() {
        return DiscountType.FLAT_AMOUNT;
    }
}
