package com.machine_coding_round.cart.mylearning.service;

import com.machine_coding_round.cart.mylearning.exception.CartException;
import com.machine_coding_round.cart.mylearning.model.Customer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Many carts. Each cart locks itself, so two customers do not block each other. */
public class CartService {
    private final Map<String, Cart> carts = new ConcurrentHashMap<>();

    public Cart createCart(String cartId, Customer customer) {
        Cart cart = new Cart(cartId, customer);
        Cart existing = carts.putIfAbsent(cartId, cart);
        if (existing != null) {
            throw new CartException("Cart already exists: " + cartId);
        }
        return cart;
    }

    public Cart getCart(String cartId) {
        Cart cart = carts.get(cartId);
        if (cart == null) {
            throw new CartException("Cart not found: " + cartId);
        }
        return cart;
    }
}
