package com.machine_coding_round.splitwise.mylearning.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Pairwise balances with mirrored updates.
 * Thread-safe via ReadWriteLock:
 * - many concurrent getBalance calls
 * - exclusive updateBalance / settleUp
 *
 * balances[A][B] > 0 means A owes B.
 */
public class BalanceSheet {

    private final Map<String, Map<String, Double>> balances = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public void updateBalance(String fromUserId, String toUserId, double amount) {
        if (fromUserId.equals(toUserId) || Math.abs(amount) < 0.01) {
            return;
        }
        lock.writeLock().lock();
        try {
            balances.computeIfAbsent(fromUserId, k -> new HashMap<>());
            balances.computeIfAbsent(toUserId, k -> new HashMap<>());

            double current = balances.get(fromUserId).getOrDefault(toUserId, 0.0);
            double next = current + amount;
            balances.get(fromUserId).put(toUserId, next);
            balances.get(toUserId).put(fromUserId, -next);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void settleUp(String fromUserId, String toUserId, double amount) {
        updateBalance(fromUserId, toUserId, -amount);
    }

    public double getBalance(String userId1, String userId2) {
        lock.readLock().lock();
        try {
            return balances.getOrDefault(userId1, Collections.emptyMap())
                    .getOrDefault(userId2, 0.0);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<String, Double> getBalancesForUser(String userId) {
        lock.readLock().lock();
        try {
            Map<String, Double> raw = balances.getOrDefault(userId, Collections.emptyMap());
            Map<String, Double> copy = new HashMap<>();
            for (Map.Entry<String, Double> e : raw.entrySet()) {
                if (Math.abs(e.getValue()) >= 0.01) {
                    copy.put(e.getKey(), e.getValue());
                }
            }
            return copy;
        } finally {
            lock.readLock().unlock();
        }
    }
}
