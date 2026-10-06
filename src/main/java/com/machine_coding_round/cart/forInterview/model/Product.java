package com.machine_coding_round.cart.forInterview.model;

import com.machine_coding_round.cart.forInterview.exception.CartException;
import lombok.Getter;

@Getter
public class Product {
    private final String id;
    private final String name;
    private final double price;
    private final int maxQuantity;

    public Product(String id, String name, double price, int maxQuantity) {
        if (id == null || id.isBlank() || name == null || name.isBlank()) {
            throw new CartException("Product id and name are required");
        }
        if (price < 0 || maxQuantity <= 0) {
            throw new CartException("Price must be >= 0 and max quantity > 0");
        }
        this.id = id;
        this.name = name;
        this.price = price;
        this.maxQuantity = maxQuantity;
    }
}
