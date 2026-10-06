package com.machine_coding_round.cart.mylearning;

import com.machine_coding_round.cart.mylearning.enums.ProductCategory;
import com.machine_coding_round.cart.mylearning.exception.CartException;
import com.machine_coding_round.cart.mylearning.model.Customer;
import com.machine_coding_round.cart.mylearning.model.Product;
import com.machine_coding_round.cart.mylearning.service.Cart;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class ConcurrencyDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Cart concurrency ===");
        exactAdds();
        cappedAdds();
    }

    private static void exactAdds() throws InterruptedException {
        Product pen = new Product("pen", "Pen", 10, ProductCategory.BOOKS, 100);
        Cart cart = new Cart("c-exact", new Customer("u1", "Alice", "a@x.com"));
        int threads = 8;
        int addsPerThread = 10;
        runAdds(cart, pen, threads, addsPerThread);
        int qty = cart.getItems().get(0).getQuantity();
        int expected = threads * addsPerThread;
        System.out.println("Exact adds: qty " + qty + " expected " + expected
                + (qty == expected ? " PASS" : " FAIL"));
    }

    private static void cappedAdds() throws InterruptedException {
        Product pen = new Product("pen", "Pen", 10, ProductCategory.BOOKS, 10);
        Cart cart = new Cart("c-cap", new Customer("u1", "Alice", "a@x.com"));
        AtomicInteger rejected = new AtomicInteger();
        int threads = 20;
        ExecutorService pool = Executors.newFixedThreadPool(8);
        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    cart.addItem(pen, 1);
                } catch (CartException ex) {
                    rejected.incrementAndGet();
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        int qty = cart.getItems().get(0).getQuantity();
        System.out.println("Capped adds: qty " + qty + " rejected " + rejected.get()
                + (qty == 10 && rejected.get() == 10 ? " PASS" : " FAIL"));
    }

    private static void runAdds(Cart cart, Product product, int threads, int addsPerThread) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                for (int i = 0; i < addsPerThread; i++) {
                    cart.addItem(product, 1);
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
    }
}
