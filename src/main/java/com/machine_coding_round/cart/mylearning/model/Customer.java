package com.machine_coding_round.cart.mylearning.model;

import com.machine_coding_round.cart.mylearning.exception.CartException;
import lombok.Getter;

@Getter
public class Customer {
    private final String id;
    private final String name;
    private final String email;

    public Customer(String id, String name, String email) {
        if (id == null || id.isBlank() || name == null || name.isBlank()) {
            throw new CartException("Customer id and name are required");
        }
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
