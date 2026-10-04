package com.machine_coding_round.splitwise.mylearning.model;

public class Split {
    private final String userId;
    private double amount;

    public Split(String userId) {
        this.userId = userId;
    }

    public Split(String userId, double amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
