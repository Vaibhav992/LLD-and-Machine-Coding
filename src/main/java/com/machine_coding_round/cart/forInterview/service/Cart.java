package com.machine_coding_round.cart.forInterview.service;

import com.machine_coding_round.cart.forInterview.exception.CartException;
import com.machine_coding_round.cart.forInterview.model.CartItem;
import com.machine_coding_round.cart.forInterview.model.Product;
import com.machine_coding_round.cart.forInterview.strategy.DiscountStrategy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
    private final Map<String, CartItem> items = new LinkedHashMap<>();
    private DiscountStrategy discount;
    private boolean checkedOut;

    public synchronized void addItem(Product product, int quantity) {
        if (checkedOut) {
            throw new CartException("Cart already checked out");
        }
        if (product == null || quantity <= 0) {
            throw new CartException("Product and positive quantity are required");
        }
        CartItem existing = items.get(product.getId());
        int updated = (existing == null ? 0 : existing.getQuantity()) + quantity;
        if (updated > product.getMaxQuantity()) {
            throw new CartException("Max quantity exceeded for " + product.getName());
        }
        if (existing == null) {
            items.put(product.getId(), new CartItem(product, quantity));
        } else {
            existing.addQuantity(quantity);
        }
    }

    public synchronized void applyDiscount(DiscountStrategy discount) {
        if (checkedOut) {
            throw new CartException("Cart already checked out");
        }
        if (discount == null) {
            throw new CartException("Discount is required");
        }
        this.discount = discount;
    }

    public synchronized double getTotal() {
        double subtotal = items.values().stream().mapToDouble(CartItem::getSubtotal).sum();
        double off = discount == null ? 0 : discount.calculateDiscount(new ArrayList<>(items.values()));
        return Math.max(0, subtotal - off);
    }

    public synchronized void checkout() {
        if (checkedOut) {
            throw new CartException("Cart already checked out");
        }
        if (items.isEmpty()) {
            throw new CartException("Cannot checkout an empty cart");
        }
        checkedOut = true;
    }

    public synchronized boolean isCheckedOut() {
        return checkedOut;
    }

    public synchronized int quantityOf(String productId) {
        CartItem item = items.get(productId);
        return item == null ? 0 : item.getQuantity();
    }
}
