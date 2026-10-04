package com.machine_coding_round.splitwise.forInterview.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BalanceSheet {
    // balances[debtor][creditor] > 0 => debtor owes creditor
    private final Map<String, Map<String, Double>> balances = new HashMap<>();

    public void updateBalance(String fromUserId, String toUserId, double amount) {
        if (fromUserId.equals(toUserId) || Math.abs(amount) < 0.01) return;
        balances.computeIfAbsent(fromUserId, k -> new HashMap<>());
        balances.computeIfAbsent(toUserId, k -> new HashMap<>());
        double current = balances.get(fromUserId).getOrDefault(toUserId, 0.0);
        balances.get(fromUserId).put(toUserId, current + amount);
        balances.get(toUserId).put(fromUserId, -(current + amount));
    }

    public void settleUp(String fromUserId, String toUserId, double amount) {
        updateBalance(fromUserId, toUserId, -amount);
    }

    public double getBalance(String a, String b) {
        return balances.getOrDefault(a, Collections.emptyMap()).getOrDefault(b, 0.0);
    }

    public Map<String, Double> getBalancesForUser(String userId) {
        Map<String, Double> copy = new HashMap<>();
        for (Map.Entry<String, Double> e : balances.getOrDefault(userId, Collections.emptyMap()).entrySet()) {
            if (Math.abs(e.getValue()) >= 0.01) copy.put(e.getKey(), e.getValue());
        }
        return copy;
    }
}
