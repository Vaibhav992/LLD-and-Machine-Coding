package com.machine_coding_round.splitwise.forInterview.split;

import com.machine_coding_round.splitwise.forInterview.exception.InvalidSplitException;
import com.machine_coding_round.splitwise.forInterview.model.Split;

import java.util.List;

public class ExactSplitStrategy implements SplitStrategy {
    @Override
    public void validate(List<Split> splits, double totalAmount) {
        double sum = 0;
        for (Split s : splits) sum += s.getAmount();
        if (Math.abs(sum - totalAmount) > 0.01) {
            throw new InvalidSplitException("Exact amounts must sum to " + totalAmount + ", got " + sum);
        }
    }

    @Override
    public void calculateSplits(List<Split> splits, double totalAmount) {
        // already set
    }
}
