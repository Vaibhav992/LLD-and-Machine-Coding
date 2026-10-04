package com.machine_coding_round.splitwise.mylearning.model;

import lombok.Getter;

@Getter
public class PercentageSplit extends Split {
    private final double percentage;

    public PercentageSplit(String userId, double percentage) {
        super(userId);
        this.percentage = percentage;
    }
}
