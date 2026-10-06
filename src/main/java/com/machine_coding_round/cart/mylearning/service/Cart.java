package com.machine_coding_round.cart.mylearning.service;

import com.machine_coding_round.cart.mylearning.enums.CartStatus;
import com.machine_coding_round.cart.mylearning.exception.CartException;
import com.machine_coding_round.cart.mylearning.model.CartItem;
import com.machine_coding_round.cart.mylearning.model.Customer;
import com.machine_coding_round.cart.mylearning.model.Product;
import com.machine_coding_round.cart.mylearning.observer.CartObserver;
import com.machine_coding_round.cart.mylearning.strategy.DiscountStrategy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One cart, one lock. Add, remove, discount, and checkout share this monitor
 * so two threads cannot both pass a quantity check or checkout a half-updated cart.
 */
public class Cart {
    private final String id;
    private final Customer customer;
    private final Map<String, CartItem> items = new LinkedHashMap<>();
    private final List<CartObserver> observers = new ArrayList<>();
    private DiscountStrategy discount;
    private CartStatus status = CartStatus.ACTIVE;

    public Cart(String id, Customer customer) {
        if (id == null || id.isBlank()) {
            throw new CartException("Cart id is required");
        }
        if (customer == null) {
            throw new CartException("Customer is required");
        }
        this.id = id;
        this.customer = customer;
    }

    public synchronized String getId() {
        return id;
    }

    public synchronized Customer getCustomer() {
        return customer;
    }

    public synchronized CartStatus getStatus() {
        return status;
    }

    public synchronized void addObserver(CartObserver observer) {
        if (observer == null) {
            throw new CartException("Observer is required");
        }
        observers.add(observer);
    }

    public synchronized void addItem(Product product, int quantity) {
        ensureActive();
        if (product == null) {
            throw new CartException("Product is required");
        }
        if (quantity <= 0) {
            throw new CartException("Quantity must be > 0");
        }
        CartItem existing = items.get(product.getId());
        int current = existing == null ? 0 : existing.getQuantity();
        int updated = current + quantity;
        if (updated > product.getMaxQuantityPerCart()) {
            throw new CartException("Max quantity exceeded for " + product.getName()
                    + ": " + updated + " > " + product.getMaxQuantityPerCart());
        }
        if (existing == null) {
            existing = new CartItem(product, quantity);
            items.put(product.getId(), existing);
        } else {
            existing.addQuantity(quantity);
        }
        for (CartObserver observer : observers) {
            observer.onItemAdded(this, existing);
        }
    }

    public synchronized void updateQuantity(String productId, int quantity) {
        ensureActive();
        CartItem item = requireItem(productId);
        if (quantity <= 0) {
            throw new CartException("Quantity must be > 0");
        }
        if (quantity > item.getProduct().getMaxQuantityPerCart()) {
            throw new CartException("Max quantity exceeded for " + item.getProduct().getName());
        }
        int delta = quantity - item.getQuantity();
        if (delta > 0) {
            item.addQuantity(delta);
        } else if (delta < 0) {
            item.addQuantity(delta);
        }
    }

    public synchronized void removeItem(String productId) {
        ensureActive();
        if (items.remove(productId) == null) {
            throw new CartException("Product not in cart: " + productId);
        }
        for (CartObserver observer : observers) {
            observer.onItemRemoved(this, productId);
        }
    }

    public synchronized void applyDiscount(DiscountStrategy discount) {
        ensureActive();
        if (discount == null) {
            throw new CartException("Discount is required");
        }
        this.discount = discount;
    }

    public synchronized void clearDiscount() {
        ensureActive();
        this.discount = null;
    }

    public synchronized double getSubtotal() {
        return items.values().stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public synchronized double getDiscountAmount() {
        if (discount == null || items.isEmpty()) {
            return 0;
        }
        return discount.calculateDiscount(new ArrayList<>(items.values()));
    }

    public synchronized double getTotal() {
        return Math.max(0, getSubtotal() - getDiscountAmount());
    }

    public synchronized List<CartItem> getItems() {
        return List.copyOf(items.values());
    }

    public synchronized void checkout() {
        ensureActive();
        if (items.isEmpty()) {
            throw new CartException("Cannot checkout an empty cart");
        }
        status = CartStatus.CHECKED_OUT;
        for (CartObserver observer : observers) {
            observer.onCartCheckedOut(this);
        }
    }

    public synchronized void abandon() {
        ensureActive();
        status = CartStatus.ABANDONED;
    }

    private void ensureActive() {
        if (status != CartStatus.ACTIVE) {
            throw new CartException("Cart is " + status);
        }
    }

    private CartItem requireItem(String productId) {
        CartItem item = items.get(productId);
        if (item == null) {
            throw new CartException("Product not in cart: " + productId);
        }
        return item;
    }
}
