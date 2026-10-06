package com.machine_coding_round.cart.mylearning;

import com.machine_coding_round.cart.mylearning.enums.ProductCategory;
import com.machine_coding_round.cart.mylearning.model.Customer;
import com.machine_coding_round.cart.mylearning.model.Product;
import com.machine_coding_round.cart.mylearning.observer.LoggingCartObserver;
import com.machine_coding_round.cart.mylearning.service.Cart;
import com.machine_coding_round.cart.mylearning.service.CartService;
import com.machine_coding_round.cart.mylearning.strategy.BuyXGetYFreeStrategy;
import com.machine_coding_round.cart.mylearning.strategy.PercentageDiscountStrategy;

public class CartDemo {
    public static void main(String[] args) {
        CartService service = new CartService();
        Customer alice = new Customer("u1", "Alice", "alice@x.com");
        Cart cart = service.createCart("c1", alice);
        cart.addObserver(new LoggingCartObserver());

        Product headphones = new Product("p1", "Headphones", 2000, ProductCategory.ELECTRONICS, 2);
        Product book = new Product("p2", "LLD Notes", 100, ProductCategory.BOOKS, 10);

        System.out.println("=== Shopping cart (learning) ===");
        cart.addItem(headphones, 1);
        cart.addItem(book, 3);
        cart.applyDiscount(new PercentageDiscountStrategy(10));
        System.out.printf("10%% off → subtotal %.2f, discount %.2f, total %.2f%n",
                cart.getSubtotal(), cart.getDiscountAmount(), cart.getTotal());

        cart.applyDiscount(new BuyXGetYFreeStrategy(2, 1));
        System.out.printf("Buy 2 get 1 → discount %.2f, total %.2f%n",
                cart.getDiscountAmount(), cart.getTotal());

        cart.checkout();
        System.out.println("Status: " + cart.getStatus());
    }
}
