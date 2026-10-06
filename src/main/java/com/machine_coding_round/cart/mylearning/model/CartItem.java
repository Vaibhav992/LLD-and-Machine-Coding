package com.machine_coding_round.cart.mylearning.model;

import com.machine_coding_round.cart.mylearning.exception.CartException;
import lombok.Getter;

@Getter
public class CartItem {
    private final Product product;
    private int quantity;
    /** Price copied at add time so a later catalog change does not rewrite this line. */
    private final double priceAtAddition;

    public CartItem(Product product, int quantity) {
        if (product == null) {
            throw new CartException("Product is required");
        }
        if (quantity <= 0) {
            throw new CartException("Quantity must be > 0");
        }
        this.product = product;
        this.quantity = quantity;
        this.priceAtAddition = product.getPrice();
    }

    public double getSubtotal() {
        return quantity * priceAtAddition;
    }

    public void addQuantity(int more) {
        this.quantity += more;
    }
}
