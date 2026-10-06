package com.machine_coding_round.cart.mylearning.observer;

import com.machine_coding_round.cart.mylearning.model.CartItem;
import com.machine_coding_round.cart.mylearning.service.Cart;

/** Side-effect only. Must not call back into the cart (that lock is already held). */
public class LoggingCartObserver implements CartObserver {
    @Override
    public void onItemAdded(Cart cart, CartItem item) {
        System.out.println("[cart " + cart.getId() + "] " + item.getProduct().getName()
                + " qty " + item.getQuantity());
    }

    @Override
    public void onItemRemoved(Cart cart, String productId) {
        System.out.println("[cart " + cart.getId() + "] removed " + productId);
    }

    @Override
    public void onCartCheckedOut(Cart cart) {
        System.out.println("[cart " + cart.getId() + "] checked out, total " + cart.getTotal());
    }
}
