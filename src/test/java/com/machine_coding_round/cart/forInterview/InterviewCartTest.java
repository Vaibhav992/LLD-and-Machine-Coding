package com.machine_coding_round.cart.forInterview;

import com.machine_coding_round.cart.forInterview.exception.CartException;
import com.machine_coding_round.cart.forInterview.model.Product;
import com.machine_coding_round.cart.forInterview.service.Cart;
import com.machine_coding_round.cart.forInterview.strategy.PercentageDiscountStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterviewCartTest {

    @Test
    void discountThenCheckout() {
        Cart cart = new Cart();
        cart.addItem(new Product("p1", "Headphones", 2000, 2), 1);
        cart.addItem(new Product("p2", "Book", 500, 5), 2);
        cart.applyDiscount(new PercentageDiscountStrategy(10));
        assertEquals(2700, cart.getTotal());
        cart.checkout();
        assertTrue(cart.isCheckedOut());
        assertThrows(CartException.class, () -> cart.addItem(new Product("p3", "Cable", 100, 2), 1));
    }
}
