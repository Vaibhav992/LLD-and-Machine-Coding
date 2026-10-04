package com.machine_coding_round.splitwise.mylearning.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Pairwise balances with mirrored updates.
 * balances[A][B] > 0 means A owes B that amount.
 * balances[B][A] stores the negated value.
 */
public class BalanceSheet {
    private final Map<String, Map<String, Double>> balances = new HashMap<>();

    public synchronized void updateBalance(String fromUserId, String toUserId, double amount) {
        if (fromUserId.equals(toUserId) || Math.abs(amount) < 0.01) {
            return;
        }
        balances.computeIfAbsent(fromUserId, k -> new HashMap<>());
        balances.computeIfAbsent(toUserId, k -> new HashMap<>());

        double current = balances.get(fromUserId).getOrDefault(toUserId, 0.0);
        balances.get(fromUserId).put(toUserId, current + amount);
        balances.get(toUserId).put(fromUserId, -(current + amount));
    }

    public synchronized void settleUp(String fromUserId, String toUserId, double amount) {
        // paying reduces what fromUser owes toUser
        updateBalance(fromUserId, toUserId, -amount);
    }

    public synchronized double getBalance(String userId1, String userId2) {
        return balances.getOrDefault(userId1, Collections.emptyMap())
                .getOrDefault(userId2, 0.0);
    }

    public synchronized Map<String, Double> getBalancesForUser(String userId) {
        Map<String, Double> raw = balances.getOrDefault(userId, Collections.emptyMap());
        Map<String, Double> copy = new HashMap<>();
        for (Map.Entry<String, Double> e : raw.entrySet()) {
            if (Math.abs(e.getValue()) >= 0.01) {
                copy.put(e.getKey(), e.getValue());
            }
        }
        return copy;
    }
}
