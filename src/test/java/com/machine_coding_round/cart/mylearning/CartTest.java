package com.machine_coding_round.cart.mylearning;

import com.machine_coding_round.cart.mylearning.enums.CartStatus;
import com.machine_coding_round.cart.mylearning.enums.ProductCategory;
import com.machine_coding_round.cart.mylearning.exception.CartException;
import com.machine_coding_round.cart.mylearning.model.Customer;
import com.machine_coding_round.cart.mylearning.model.Product;
import com.machine_coding_round.cart.mylearning.service.Cart;
import com.machine_coding_round.cart.mylearning.strategy.BuyXGetYFreeStrategy;
import com.machine_coding_round.cart.mylearning.strategy.FlatAmountDiscountStrategy;
import com.machine_coding_round.cart.mylearning.strategy.PercentageDiscountStrategy;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartTest {

    private final Customer alice = new Customer("u1", "Alice", "a@x.com");

    @Test
    void mergesQuantityAndAppliesDiscounts() {
        Cart cart = new Cart("c1", alice);
        Product book = new Product("p2", "LLD Notes", 100, ProductCategory.BOOKS, 10);
        cart.addItem(book, 1);
        cart.addItem(book, 2);
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(300, cart.getSubtotal());

        cart.applyDiscount(new PercentageDiscountStrategy(10));
        assertEquals(30, cart.getDiscountAmount());
        assertEquals(270, cart.getTotal());

        cart.applyDiscount(new BuyXGetYFreeStrategy(2, 1));
        assertEquals(100, cart.getDiscountAmount());
        assertEquals(200, cart.getTotal());

        cart.applyDiscount(new FlatAmountDiscountStrategy(1000));
        assertEquals(0, cart.getTotal());
    }

    @Test
    void rejectsOverMaxAndEmptyCheckout() {
        Cart cart = new Cart("c1", alice);
        Product phone = new Product("p1", "Phone", 1000, ProductCategory.ELECTRONICS, 2);
        cart.addItem(phone, 2);
        assertThrows(CartException.class, () -> cart.addItem(phone, 1));
        assertEquals(2, cart.getItems().get(0).getQuantity());

        Cart empty = new Cart("c2", alice);
        assertThrows(CartException.class, empty::checkout);
    }

    @Test
    void checkoutIsFinal() {
        Cart cart = new Cart("c1", alice);
        cart.addItem(new Product("p1", "Pen", 10, ProductCategory.BOOKS, 5), 1);
        cart.checkout();
        assertEquals(CartStatus.CHECKED_OUT, cart.getStatus());
        assertThrows(CartException.class, () -> cart.addItem(
                new Product("p2", "Book", 20, ProductCategory.BOOKS, 5), 1));
    }

    @Test
    void concurrentAddsDoNotLoseUpdatesOrPassMax() throws InterruptedException {
        Product pen = new Product("pen", "Pen", 10, ProductCategory.BOOKS, 10);
        Cart cart = new Cart("c1", alice);
        AtomicInteger rejected = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 25; i++) {
            pool.submit(() -> {
                try {
                    cart.addItem(pen, 1);
                } catch (CartException ex) {
                    rejected.incrementAndGet();
                }
            });
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
        assertEquals(10, cart.getItems().get(0).getQuantity());
        assertEquals(15, rejected.get());
    }
}
