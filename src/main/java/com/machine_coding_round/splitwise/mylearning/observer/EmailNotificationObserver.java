package com.machine_coding_round.splitwise.mylearning.observer;

import com.machine_coding_round.splitwise.mylearning.model.Expense;

public class EmailNotificationObserver implements ExpenseObserver {

    @Override
    public void onExpenseAdded(Expense expense) {
        System.out.println("[NOTIFY] Expense added: " + expense.getDescription()
                + " ($" + expense.getAmount() + ") paid by " + expense.getPaidByUserId());
    }

    @Override
    public void onSettlement(String fromUserId, String toUserId, double amount) {
        System.out.println("[NOTIFY] Settlement: " + fromUserId
                + " paid $" + amount + " to " + toUserId);
    }
}
