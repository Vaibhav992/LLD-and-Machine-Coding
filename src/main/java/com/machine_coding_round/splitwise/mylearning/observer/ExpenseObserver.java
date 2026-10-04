package com.machine_coding_round.splitwise.mylearning.observer;

import com.machine_coding_round.splitwise.mylearning.model.Expense;

public interface ExpenseObserver {
    void onExpenseAdded(Expense expense);

    void onSettlement(String fromUserId, String toUserId, double amount);
}
