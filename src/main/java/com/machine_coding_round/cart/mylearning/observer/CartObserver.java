package com.machine_coding_round.cart.mylearning.observer;

import com.machine_coding_round.cart.mylearning.model.CartItem;
import com.machine_coding_round.cart.mylearning.service.Cart;

public interface CartObserver {
    void onItemAdded(Cart cart, CartItem item);

    void onItemRemoved(Cart cart, String productId);

    void onCartCheckedOut(Cart cart);
}
