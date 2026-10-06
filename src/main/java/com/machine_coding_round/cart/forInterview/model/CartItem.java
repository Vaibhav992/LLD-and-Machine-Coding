package com.machine_coding_round.cart.forInterview.model;

import lombok.Getter;

@Getter
public class CartItem {
    private final Product product;
    private int quantity;
    private final double priceAtAddition;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.priceAtAddition = product.getPrice();
    }

    public double getSubtotal() {
        return quantity * priceAtAddition;
    }

    public void addQuantity(int more) {
        quantity += more;
    }
}
