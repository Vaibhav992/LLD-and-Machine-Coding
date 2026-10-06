package com.machine_coding_round.cart.forInterview;

import com.machine_coding_round.cart.forInterview.model.Product;
import com.machine_coding_round.cart.forInterview.service.Cart;
import com.machine_coding_round.cart.forInterview.strategy.PercentageDiscountStrategy;

public class InterviewDemo {
    public static void main(String[] args) {
        Cart cart = new Cart();
        cart.addItem(new Product("p1", "Headphones", 2000, 2), 1);
        cart.addItem(new Product("p2", "Book", 500, 5), 2);
        cart.applyDiscount(new PercentageDiscountStrategy(10));

        System.out.println("=== Shopping cart (interview) ===");
        System.out.println("Total after 10% off: " + cart.getTotal());
        cart.checkout();
        System.out.println("Checked out: " + cart.isCheckedOut());
    }
}
