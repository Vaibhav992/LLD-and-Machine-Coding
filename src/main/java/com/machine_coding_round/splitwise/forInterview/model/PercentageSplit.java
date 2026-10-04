package com.machine_coding_round.splitwise.forInterview.model;

public class PercentageSplit extends Split {
    private final double percentage;

    public PercentageSplit(String userId, double percentage) {
        super(userId);
        this.percentage = percentage;
    }

    public double getPercentage() { return percentage; }
}
