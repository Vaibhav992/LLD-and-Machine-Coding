package com.machine_coding_round.cart.mylearning.strategy;

import com.machine_coding_round.cart.mylearning.enums.DiscountType;
import com.machine_coding_round.cart.mylearning.exception.CartException;
import com.machine_coding_round.cart.mylearning.model.CartItem;

import java.util.List;

/**
 * Per line: every (buy + free) units, {@code freeCount} units are free.
 * Buy 2 get 1, quantity 3 → 1 free unit.
 */
public class BuyXGetYFreeStrategy implements DiscountStrategy {
    private final int buyCount;
    private final int freeCount;

    public BuyXGetYFreeStrategy(int buyCount, int freeCount) {
        if (buyCount <= 0 || freeCount <= 0) {
            throw new CartException("Buy and free counts must be > 0");
        }
        this.buyCount = buyCount;
        this.freeCount = freeCount;
    }

    @Override
    public double calculateDiscount(List<CartItem> items) {
        int group = buyCount + freeCount;
        double discount = 0;
        for (CartItem item : items) {
            int freeUnits = (item.getQuantity() / group) * freeCount;
            discount += freeUnits * item.getPriceAtAddition();
        }
        return discount;
    }

    @Override
    public String getDescription() {
        return "buy " + buyCount + " get " + freeCount + " free";
    }

    @Override
    public DiscountType getType() {
        return DiscountType.BUY_X_GET_Y;
    }
}
