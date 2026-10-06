package com.machine_coding_round.cart.mylearning.strategy;

import com.machine_coding_round.cart.mylearning.enums.DiscountType;
import com.machine_coding_round.cart.mylearning.exception.CartException;
import com.machine_coding_round.cart.mylearning.model.CartItem;

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

    @Override
    public String getDescription() {
        return percentage + "% off";
    }

    @Override
    public DiscountType getType() {
        return DiscountType.PERCENTAGE;
    }
}
