package com.machine_coding_round.cart.mylearning.model;

import com.machine_coding_round.cart.mylearning.enums.ProductCategory;
import com.machine_coding_round.cart.mylearning.exception.CartException;
import lombok.Getter;

@Getter
public class Product {
    private final String id;
    private final String name;
    private final double price;
    private final ProductCategory category;
    private final int maxQuantityPerCart;

    public Product(String id, String name, double price, ProductCategory category, int maxQuantityPerCart) {
        if (id == null || id.isBlank() || name == null || name.isBlank()) {
            throw new CartException("Product id and name are required");
        }
        if (price < 0) {
            throw new CartException("Price cannot be negative");
        }
        if (category == null) {
            throw new CartException("Category is required");
        }
        if (maxQuantityPerCart <= 0) {
            throw new CartException("Max quantity per cart must be > 0");
        }
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.maxQuantityPerCart = maxQuantityPerCart;
    }
}
